package aero.minova.rcp.rcp.parts;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByDataLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.FixedSummaryRowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.painter.layer.GridLineCellLayerPainter;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionUtils;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.summaryrow.FixedSummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.summaryrow.ISummaryProvider;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummationSummaryProvider;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeCollapseAllCommand;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeExpandAllCommand;
import org.eclipse.nebula.widgets.nattable.tree.config.TreeLayerExpandCollapseKeyBindings;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.constants.AggregateOption;
import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.rcp.nattable.MinovaDisplayConfiguration;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.rcp.util.PersistTableSelection;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;

public class WFCIndexPart extends WFCFormPart {

	@Inject
	private ESelectionService selectionService;

	private Table data;

	private NatTable natTable;
	private BodyLayerStack<Row> bodyLayerStack;
	private IEclipseContext context;

	private Map<String, Field> fields = new HashMap<>();

	@Inject
	TranslationService translationService;

	@Inject
	@Named(value = TranslationService.LOCALE)
	Locale locale;

	private MinovaColumnPropertyAccessor columnPropertyAccessor;

	private ColumnHeaderLayer columnHeaderLayer;

	@Inject
	MPart mpart;

	@PostConstruct
	public void createComposite(Composite parent, EModelService modelService) {
		new FormToolkit(parent.getDisplay());
		if (getForm(parent) == null) {
			return;
		}

		// perspective.getContext().set(Form.class, form); // Wir merken es uns im Context; so können andere es nutzen

		data = dataFormService.getTableFromFormIndex(form);

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
	}

	@PersistTableSelection
	public void savePrefs() {
		// TODO INDEX Part reihenfolge + Gruppierung speichern
	}

	/**
	 * Setzt die größe der Spalten aus dem sichtbaren Bereiches im Index-Bereich auf die Maximale Breite des Inhalts.
	 *
	 * @param mPart
	 */
	@Inject
	@Optional
	public void load(@UIEventTopic(Constants.BROKER_RESIZETABLE) MPart mPart) {
		if (!mPart.equals(this.mpart)) {
			return;
		}
		NatTableUtil.resize(natTable);
	}

	/**
	 * Diese Methode ließt die Index-Spalten aus und erstellet daraus eine Table, diese wird dann an den CAS als Anfrage übergeben.
	 */
	@Inject
	@Optional
	public void load(@UIEventTopic(Constants.BROKER_LOADINDEXTABLE) Map<MPerspective, Table> map) {
		if (map.get(perspective) != null) {
			Table table = map.get(perspective);
			updateData(table.getRows());
		}
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		this.locale = s;
		translate(translationService);
	}

	@Inject
	@Optional
	private void translationDownloaded(@UIEventTopic(Constants.BROKER_TRANSLATION_CHANGED) Locale s) {
		this.locale = s;
		translate(translationService);
	}

	@Inject
	@Optional
	private void collapseGroups(@UIEventTopic(Constants.BROKER_COLLAPSEINDEX) String s) {
		natTable.doCommand(new TreeCollapseAllCommand());
	}

	@Inject
	@Optional
	private void expandGroups(@UIEventTopic(Constants.BROKER_EXPANDINDEX) String s) {
		natTable.doCommand(new TreeExpandAllCommand());
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
				columnHeaderLayer.renameColumnIndex(i, columnPropertyAccessor.getTableHeadersMap().get(propertyNames[i]));
			}
		}

	}

	public NatTable createNatTable(Composite parent, Form form, Table table, ESelectionService selectionService, IEclipseContext context) {

		this.context = context;

		// Datenmodel für die Eingaben
		ConfigRegistry configRegistry = new ConfigRegistry();
		columnPropertyAccessor = new MinovaColumnPropertyAccessor(table, form);
		columnPropertyAccessor.initPropertyNames(translationService);

		// create the body stack
		bodyLayerStack = new BodyLayerStack<Row>(table.getRows(), columnPropertyAccessor, configRegistry);
		bodyLayerStack.getBodyDataLayer().setConfigLabelAccumulator(new ColumnLabelAccumulator());

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(columnPropertyAccessor.getPropertyNames(),
				columnPropertyAccessor.getTableHeadersMap());
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayerStack, bodyLayerStack.getSelectionLayer());

		SortHeaderLayer<Row> sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
				new GlazedListsSortModel<>(bodyLayerStack.getSortedList(), columnPropertyAccessor, configRegistry, columnHeaderDataLayer));

		// connect sortModel to GroupByDataLayer to support sorting by group by summary values
		bodyLayerStack.getBodyDataLayer().initializeTreeComparator(sortHeaderLayer.getSortModel(), bodyLayerStack.getTreeLayer(), true);

		// build the Summary Row
		FixedSummaryRowLayer summaryRowLayer = new FixedSummaryRowLayer(bodyLayerStack.getGlazedListsEventLayer(), bodyLayerStack.getViewportLayer(),
				configRegistry, false);
		summaryRowLayer.setHorizontalCompositeDependency(false);
		CompositeLayer summaryComposite = new CompositeLayer(1, 2);
		summaryComposite.setChildLayer("SUMMARY", summaryRowLayer, 0, 0);
		summaryComposite.setChildLayer(GridRegion.BODY, bodyLayerStack.getViewportLayer(), 0, 1);

		// build the row header layer
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyLayerStack.getBodyDataProvider());
		DataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
		// Special RowHeader for summary
		ILayer rowHeaderLayer = new FixedSummaryRowHeaderLayer(rowHeaderDataLayer, summaryComposite, bodyLayerStack.getSelectionLayer());
		((FixedSummaryRowHeaderLayer) rowHeaderLayer).setSummaryRowLabel("");

		// build the corner layer
		IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		ILayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

		// build the grid layer
		GridLayer gridLayer = new GridLayer(summaryComposite, sortHeaderLayer, rowHeaderLayer, cornerLayer);

		// ensure the body data layer uses a layer painter with correct configured clipping
		bodyLayerStack.getBodyDataLayer().setLayerPainter(new GridLineCellLayerPainter(false, true));

		// set the group by header on top of the grid
		CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
		GroupByHeaderLayer groupByHeaderLayer = new GroupByHeaderLayer(bodyLayerStack.getGroupByModel(), gridLayer, columnHeaderDataProvider,
				columnHeaderLayer);
		compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, groupByHeaderLayer, 0, 0);
		compositeGridLayer.setChildLayer("Grid", gridLayer, 0, 1);

		SelectionLayer selectionLayer = bodyLayerStack.getSelectionLayer();
//		IRowDataProvider<Object> bodyDataProvider = (IRowDataProvider<Object>) bodyLayerStack.getBodyDataProvider();
//		selectionLayer
//				.setSelectionModel(new RowSelectionModel<>(selectionLayer, bodyDataProvider, new IRowIdAccessor<>() {
//					@Override
//					public Serializable getRowId(Object rowObject) {
//						if (rowObject instanceof Row) {
//							return ((Row) rowObject).hashCode();
//						} else if (rowObject instanceof GroupByObject) {
//							return ((GroupByObject) rowObject).hashCode();
//						}
//						return rowObject.toString();
//					}
//				}));

		selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());
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
		natTable.addConfiguration(new TreeLayerExpandCollapseKeyBindings(bodyLayerStack.getTreeLayer(), bodyLayerStack.getSelectionLayer()));

		configureSummary(form);

		natTable.configure();
		// Hier können wir das Theme setzen
//		ThemeConfiguration darkThemeConfig = new DarkNatTableThemeConfiguration();
//		natTable.setTheme(darkThemeConfig);
		natTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		return natTable;

	}

	private void configureSummary(Form form) {
		final IDataProvider summaryDataProvider = new ListDataProvider<>(bodyLayerStack.getSortedList(), columnPropertyAccessor);

		// add summary configuration
		natTable.addConfiguration(new AbstractRegistryConfiguration() {

			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				int i = 0;
				for (aero.minova.rcp.form.model.xsd.Column column : form.getIndexView().getColumn()) {
					ISummaryProvider summaryProvider = null;

					if (column.getAggregate() != null) {
						AggregateOption agg = AggregateOption.valueOf(column.getAggregate());
						switch (agg) {
						case AVERAGE:
							summaryProvider = new AverageSummaryProvider(summaryDataProvider);
							break;
						case COUNT:
							summaryProvider = new CountSummaryProvider(summaryDataProvider);
							break;
						case MAX:
							summaryProvider = new MaxSummaryProvider(summaryDataProvider);
							break;
						case MIN:
							summaryProvider = new MinSummaryProvider(summaryDataProvider);
							break;
						case SUM:
							summaryProvider = new SummationSummaryProvider(summaryDataProvider, false);
							break;
						default:
							break;
						}
					}

					// Summe ("total" in .xml)
					if (column.isTotal() != null && column.isTotal()) {
						summaryProvider = new SummationSummaryProvider(summaryDataProvider, false);
					}

					if (summaryProvider != null) {
						configRegistry.registerConfigAttribute(SummaryRowConfigAttributes.SUMMARY_PROVIDER, summaryProvider, DisplayMode.NORMAL,
								SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + i);
					}

					i++;
				}
			}
		});
	}

	/**
	 * Always encapsulate the body layer stack in an AbstractLayerTransform to ensure that the index transformations are performed in later commands.
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

		private GlazedListsEventLayer glazedListsEventLayer;

		private ViewportLayer viewportLayer;

		public BodyLayerStack(List<T> values, IColumnPropertyAccessor<T> columnPropertyAccessor, ConfigRegistry configRegistry) {
			eventList = GlazedLists.eventList(values);
			TransformedList<T, T> rowObjectsGlazedList = GlazedLists.threadSafeList(eventList);

			// use the SortedList constructor with 'null' for the Comparator
			// because the Comparator
			// will be set by configuration
			this.sortedList = new SortedList<>(rowObjectsGlazedList, null);

			bodyDataLayer = new GroupByDataLayer<>(getGroupByModel(), this.sortedList, columnPropertyAccessor, configRegistry);

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
			glazedListsEventLayer = new GlazedListsEventLayer<>(bodyDataLayer, this.sortedList);

			ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(glazedListsEventLayer);
			this.selectionLayer = new SelectionLayer(columnReorderLayer);

			selectionLayer.addLayerListener(new ILayerListener() {

				@Override
				public void handleLayerEvent(ILayerEvent event) {
					List c = SelectionUtils.getSelectedRowObjects(selectionLayer, (IRowDataProvider<T>) bodyDataProvider, false);
					List collection = (List) c.stream().filter(p -> (p instanceof Row)).collect(Collectors.toList());
					if (!collection.isEmpty()) {
						context.set(Constants.BROKER_ACTIVEROWS, collection);
					}
				}
			});

			treeLayer = new TreeLayer(this.selectionLayer, bodyDataLayer.getTreeRowModel());

			viewportLayer = new ViewportLayer(treeLayer);

			setUnderlyingLayer(viewportLayer);
		}

		public GlazedListsEventLayer getGlazedListsEventLayer() {
			return glazedListsEventLayer;
		}

		public SelectionLayer getSelectionLayer() {
			return this.selectionLayer;
		}

		public ViewportLayer getViewportLayer() {
			return this.viewportLayer;
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

	class CountSummaryProvider implements ISummaryProvider {

		private IDataProvider dataProvider;

		public CountSummaryProvider(IDataProvider dataProvider) {
			this.dataProvider = dataProvider;
		}

		@Override
		public Object summarize(int columnIndex) {
			int rowCount = this.dataProvider.getRowCount();
			int valueRows = 0;

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
				// this check is necessary because of the GroupByObject
				if (dataValue instanceof Number) {
					valueRows++;
				}
			}
			return valueRows;
		}
	}

	class AverageSummaryProvider implements ISummaryProvider {

		private IDataProvider dataProvider;

		public AverageSummaryProvider(IDataProvider dataProvider) {
			this.dataProvider = dataProvider;
		}

		@Override
		public Object summarize(int columnIndex) {
			int rowCount = this.dataProvider.getRowCount();
			int valueRows = 0;
			double total = 0;

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
				// this check is necessary because of the GroupByObject
				if (dataValue instanceof Number) {
					valueRows++;
					total += ((Number) dataValue).doubleValue();
				}
			}
			if (valueRows == 0)
				return 0;
			return total / valueRows;
		}
	}

	class MinSummaryProvider implements ISummaryProvider {

		private IDataProvider dataProvider;

		public MinSummaryProvider(IDataProvider dataProvider) {
			this.dataProvider = dataProvider;
		}

		@Override
		public Object summarize(int columnIndex) {
			int rowCount = this.dataProvider.getRowCount();
			double min = Double.MAX_VALUE;

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
				// this check is necessary because of the GroupByObject
				if (dataValue instanceof Number) {
					if (((Number) dataValue).doubleValue() < min)
						min = ((Number) dataValue).doubleValue();
				}
			}
			if (min == Double.MAX_VALUE)
				return 0;
			return min;
		}
	}

	class MaxSummaryProvider implements ISummaryProvider {

		private IDataProvider dataProvider;

		public MaxSummaryProvider(IDataProvider dataProvider) {
			this.dataProvider = dataProvider;
		}

		@Override
		public Object summarize(int columnIndex) {
			int rowCount = this.dataProvider.getRowCount();
			double max = Double.MIN_VALUE;

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
				// this check is necessary because of the GroupByObject
				if (dataValue instanceof Number) {
					if (((Number) dataValue).doubleValue() > max)
						max = ((Number) dataValue).doubleValue();
				}
			}
			if (max == Double.MIN_VALUE)
				return 0;
			return max;
		}
	}

	public void updateData(List<Row> list) {
		bodyLayerStack.getSortedList().clear();
		bodyLayerStack.getSortedList().addAll(list);
	}

}
