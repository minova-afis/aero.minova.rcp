package aero.minova.rcp.rcp.parts;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByDataLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.resize.command.AutoResizeColumnsCommand;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionUtils;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.tree.config.TreeLayerExpandCollapseKeyBindings;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.rcp.nattable.MinovaDisplayConfiguration;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.DateTimeUtil;
import aero.minova.rcp.rcp.util.PersistTableSelection;
import aero.minova.rcp.rcp.util.TimeUtil;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;

public class WFCIndexPart extends WFCFormPart {

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Inject
	private IMinovaJsonService mjs;

	@Inject
	private ESelectionService selectionService;

	private Table data;

	private FormToolkit formToolkit;

	private Composite composite;

	private Gson gson;

	private NatTable natTable;
	private BodyLayerStack<Row> bodyLayerStack;
	private IEclipseContext context;

	private Map<String, Field> fields = new HashMap();

	@Inject
	TranslationService translationService;

	@Inject
	@Named(value = TranslationService.LOCALE)
	Locale locale;

	private MinovaColumnPropertyAccessor columnPropertyAccessor;

	private ColumnHeaderLayer columnHeaderLayer;

	@PostConstruct
	public void createComposite(Composite parent, MPart part, EModelService modelService) {

		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		if (getForm(parent) == null) {
			return;
		}

		perspective.getContext().set(Form.class, form); // Wir merken es uns im Context; so können andere es nutzen

		String tableName = form.getIndexView().getSource();

		String string = prefs.get(tableName, null);
		data = dataFormService.getTableFromFormIndex(form);
		data.addRow();
		if (string != null) {
			data = mjs.json2Table(string);
		}

		parent.setLayout(new GridLayout());

		for (Object headOrPage : form.getDetail().getHeadAndPage()) {
			if (headOrPage instanceof Page) {
				Page page = (Page) headOrPage;
				for (Object fieldOrGrid : page.getFieldOrGrid()) {
					if (fieldOrGrid instanceof Field) {
						Field field = (Field) fieldOrGrid;
						fields.put(field.getName(), field);
					}
				}

			}
		}

		natTable = createNatTable(parent, form, data, selectionService, perspective.getContext());

		gson = new Gson();
		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.setPrettyPrinting() //
				.create();

		try {
			Path path = Path.of(dataService.getStoragePath().toString(), "cache", "jsonTableIndex");
			File jsonFile = new File(path.toString());
			jsonFile.createNewFile();

			String content = Files.readString(path, StandardCharsets.UTF_8);
			if (!content.equals("")) {
				Table indexTable = gson.fromJson(content, Table.class);
				if (indexTable.getRows() != null) {
					updateData(indexTable.getRows());
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@PersistTableSelection
	public void savePrefs() {
		// TODO INDEX Part reihenfolge + Gruppierung speichern
	}

	/**
	 * Diese Methode ließt die Index-Apalten aus und erstellet daraus eine Tabel,
	 * diese wir dann an den CAS als Anfrage übergeben.
	 */
	@Inject
	@Optional
	public void load(@UIEventTopic(Constants.BROKER_LOADINDEXTABLE) Map<MPerspective, Table> map) {
		if (map.get(perspective) != null) {
			Table table = map.get(perspective);

			for (Row r : table.getRows()) {
				for (int i = 0; i < r.size(); i++) {
					Field field = fields.get(table.getColumnName(i));
					if (field != null) {
						if (field.getShortDate() != null) {
							r.setValue(new Value(DateTimeUtil.getDateString(r.getValue(i).getInstantValue(), locale),
									DataType.STRING), i);
						} else if (field.getShortTime() != null) {
							r.setValue(new Value(TimeUtil.getTimeString(r.getValue(i).getInstantValue(), locale),
									DataType.STRING), i);
						} else if (field.getNumber() != null) {
							int decimals = field.getNumber().getDecimals();

							NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
							numberFormat.setMaximumFractionDigits(decimals); // wir wollen genau so viele
																				// Nachkommastellen
							numberFormat.setMinimumFractionDigits(decimals); // dito

							Value value = r.getValue(i);
							Value displayValue;
							if (value.getType().equals(DataType.DOUBLE)) {
								displayValue = new Value(numberFormat.format(value.getDoubleValue()));
							} else {
								displayValue = new Value(numberFormat.format(value.getIntegerValue()));
							}
							r.setValue(displayValue, i);
						}
					}
				}
			}

			updateData(table.getRows());

			try {
				Path file = Path.of(dataService.getStoragePath().toString(), "cache" + "jsonTableIndex");
				Files.write(file, gson.toJson(table).getBytes(StandardCharsets.UTF_8));
				System.out.println("Table saved");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		this.locale = s;
		translate(translationService);
	}

	/**
	 * Übersetz die Headerzeile in der NatTable
	 *
	 * @param translationService2
	 */
	private void translate(TranslationService translationService2) {
		// TODO wir hlören auf das LocaleChangeEvent dann entfällt der Check!
		if (columnPropertyAccessor != null) {
			columnPropertyAccessor.translate(translationService);
			String[] propertyNames = columnPropertyAccessor.getPropertyNames();
			for (int i = 0; i < columnPropertyAccessor.getColumnCount(); i++) {
				columnHeaderLayer.renameColumnIndex(i,
						columnPropertyAccessor.getTableHeadersMap().get(propertyNames[i]));
			}
		}

	}

	public NatTable createNatTable(Composite parent, Form form, Table table, ESelectionService selectionService,
			IEclipseContext context) {

		this.context = context;

		// Datenmodel für die Eingaben
		ConfigRegistry configRegistry = new ConfigRegistry();
		columnPropertyAccessor = new MinovaColumnPropertyAccessor(table, form);
		columnPropertyAccessor.initPropertyNames(translationService);

		// create the body stack
		bodyLayerStack = new BodyLayerStack<>(table.getRows(), columnPropertyAccessor);
		bodyLayerStack.getBodyDataLayer().setConfigLabelAccumulator(new ColumnLabelAccumulator());

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(
				columnPropertyAccessor.getPropertyNames(), columnPropertyAccessor.getTableHeadersMap());
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayerStack,
				bodyLayerStack.getSelectionLayer());

		columnHeaderDataLayer.setConfigLabelAccumulator(new ColumnLabelAccumulator());

		SortHeaderLayer<Row> sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer, new GlazedListsSortModel<>(
				bodyLayerStack.getSortedList(), columnPropertyAccessor, configRegistry, columnHeaderDataLayer));

		// build the row header layer
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyLayerStack.getBodyDataProvider());
		DataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, bodyLayerStack,
				bodyLayerStack.getSelectionLayer());

		// build the corner layer
		IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider,
				rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		ILayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

		// build the grid layer
		GridLayer gridLayer = new GridLayer(bodyLayerStack, sortHeaderLayer, rowHeaderLayer, cornerLayer);

		// set the group by header on top of the grid
		CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
		GroupByHeaderLayer groupByHeaderLayer = new GroupByHeaderLayer(bodyLayerStack.getGroupByModel(), gridLayer,
				columnHeaderDataProvider, columnHeaderLayer);
		compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, groupByHeaderLayer, 0, 0);
		compositeGridLayer.setChildLayer("Grid", gridLayer, 0, 1);

		natTable = new NatTable(parent, compositeGridLayer, false);
		// as the autoconfiguration of the NatTable is turned off, we have to
		// add the DefaultNatTableStyleConfiguration and the ConfigRegistry
		// manually
		natTable.setConfigRegistry(configRegistry);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new SingleClickSortConfiguration());
		natTable.addConfiguration(new MinovaDisplayConfiguration(table.getColumns(), translationService, form));

//		natTable.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(natTable));
//
//		// add group by configuration
		natTable.addConfiguration(new GroupByHeaderMenuConfiguration(natTable, groupByHeaderLayer));
		// adds the key bindings that allow space bar to be pressed to
		// expand/collapse tree nodes
		natTable.addConfiguration(new TreeLayerExpandCollapseKeyBindings(bodyLayerStack.getTreeLayer(),
				bodyLayerStack.getSelectionLayer()));

		natTable.configure();
		// Hier können wir das Theme setzen
//		ThemeConfiguration darkThemeConfig = new DarkNatTableThemeConfiguration();
//		natTable.setTheme(darkThemeConfig);
		natTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		return natTable;

	}

	/**
	 * Always encapsulate the body layer stack in an AbstractLayerTransform to
	 * ensure that the index transformations are performed in later commands.
	 *
	 * @param <T>
	 */
	class BodyLayerStack<T> extends AbstractLayerTransform {

		private final SortedList<T> sortedList;

		private final IDataProvider bodyDataProvider;

		private final SelectionLayer selectionLayer;

		private final GroupByModel groupByModel = new GroupByModel();

		private EventList<T> eventList;

		private GroupByDataLayer<T> bodyDataLayer;

		private TreeLayer treeLayer;

		public BodyLayerStack(List<T> values, IColumnPropertyAccessor<T> columnPropertyAccessor) {
			eventList = GlazedLists.eventList(values);
			TransformedList<T, T> rowObjectsGlazedList = GlazedLists.threadSafeList(eventList);

			// use the SortedList constructor with 'null' for the Comparator
			// because the Comparator
			// will be set by configuration
			this.sortedList = new SortedList<>(rowObjectsGlazedList, null);

			bodyDataLayer = new GroupByDataLayer<>(getGroupByModel(), this.sortedList, columnPropertyAccessor);


			// we register a custom UpdateDataCommandHandler so that we could add a new
			// value if desired
			// alternative we could add a new line during line selection
//			this.bodyDataLayer.unregisterCommandHandler(UpdateDataCommand.class);
//			this.bodyDataLayer.registerCommandHandler(new UpdateDataCommandHandler(this.bodyDataLayer) {
//				@SuppressWarnings("unchecked")
//				@Override
//				protected boolean doCommand(UpdateDataCommand command) {
//					if (super.doCommand(command)) {
//						System.out.println("Custom update handler called");
//						return true;
//					}
//					return false;
//				}
//			});

			// get the IDataProvider that was created by the GroupByDataLayer
			this.bodyDataProvider = bodyDataLayer.getDataProvider();


			// Apply a ColumnLabelAccumulator to address the columns in the
			// EditConfiguration class
			// first colum starts at 0, etc
//			ColumnLabelAccumulator columnLabelAccumulator = new ColumnLabelAccumulator(bodyDataProvider);
//			bodyDataLayer.setConfigLabelAccumulator(columnLabelAccumulator);

			// layer for event handling of GlazedLists and PropertyChanges
			GlazedListsEventLayer<T> glazedListsEventLayer = new GlazedListsEventLayer<>(bodyDataLayer,
					this.sortedList);

			this.selectionLayer = new SelectionLayer(glazedListsEventLayer);

			selectionLayer.addLayerListener(new ILayerListener() {

				@Override
				public void handleLayerEvent(ILayerEvent event) {
					Object c = SelectionUtils.getSelectedRowObjects(selectionLayer,
							(IRowDataProvider<T>) bodyDataProvider, false);
					context.set(Constants.BROKER_ACTIVEROWS, c);
				}
			});

			treeLayer = new TreeLayer(this.selectionLayer, bodyDataLayer.getTreeRowModel());

			ViewportLayer viewportLayer = new ViewportLayer(treeLayer);

			setUnderlyingLayer(viewportLayer);
		}

		public SelectionLayer getSelectionLayer() {
			return this.selectionLayer;
		}

		public SortedList<T> getSortedList() {
			return this.sortedList;
		}

		public GroupByDataLayer<T> getBodyDataLayer() {
			return this.bodyDataLayer;
		}

		public TreeLayer getTreeLayer() {
			return this.treeLayer;
		}

		public IDataProvider getBodyDataProvider() {
			return this.bodyDataProvider;
		}

		public GroupByModel getGroupByModel() {
			return this.groupByModel;
		}

		public EventList<T> getList() {
			return eventList;
		}
	}

	public static void resizeTable(NatTable table) {
		if (!table.isDisposed()) {

			/*
			 * Collection<ILayer> underlyingLayersByColumnPosition =
			 * table.getUnderlyingLayersByColumnPosition(0); int[] selectedColumnPositions =
			 * null; for (ILayer iLayer : underlyingLayersByColumnPosition) {
			 *
			 * if (iLayer instanceof ViewportLayer)
			 *
			 * { int minColumnPosition = ((ViewportLayer)
			 * iLayer).getMinimumOriginColumnPosition();
			 *
			 * int columnCount = ((ViewportLayer) iLayer).getColumnCount();
			 *
			 * int maxColumnPosition = minColumnPosition + columnCount - 1;
			 *
			 * selectedColumnPositions = new int[columnCount];
			 *
			 * for (int i = minColumnPosition; i <= maxColumnPosition; i++) {
			 *
			 * int idx = i - minColumnPosition;
			 *
			 * selectedColumnPositions[idx] = i;
			 *
			 * }
			 *
			 * }
			 */

			int[] selectedColumnPositions = new int[table.getColumnCount()];

			for (int i = table.getColumnCount() - 1; i > -1; i--) {

				selectedColumnPositions[i] = i;

			}

			// }
			AutoResizeColumnsCommand columnCommand = new AutoResizeColumnsCommand(table, false,
					selectedColumnPositions);

			table.doCommand(columnCommand);
		}
	}

	public void updateData(List<Row> list) {
		bodyLayerStack.getSortedList().clear();
		bodyLayerStack.getSortedList().addAll(list);
	}

}
