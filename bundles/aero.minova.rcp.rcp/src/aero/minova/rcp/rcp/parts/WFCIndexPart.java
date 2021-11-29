package aero.minova.rcp.rcp.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataCommandHandler;
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
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.command.UngroupByColumnIndexCommand;
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
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.event.RowStructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.painter.layer.GridLineCellLayerPainter;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionUtils;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.action.SortColumnAction;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.sort.event.ColumnHeaderClickEventMatcher;
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
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.constants.AggregateOption;
import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.nattable.MinovaIndexConfiguration;
import aero.minova.rcp.rcp.util.LoadTableSelection;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.rcp.util.PersistTableSelection;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;

public class WFCIndexPart extends WFCFormPart {

	@Inject
	private ESelectionService selectionService;

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.AUTO_LOAD_INDEX)
	boolean autoLoadIndex;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TABLE_SELECTION_BUFFER_MS)
	int tableSelectionBuffer;

	private Table data;

	private NatTable natTable;
	private BodyLayerStack<Row> bodyLayerStack;
	private IEclipseContext context;

	@Inject
	TranslationService translationService;

	private MinovaColumnPropertyAccessor columnPropertyAccessor;

	private ColumnHeaderLayer columnHeaderLayer;

	@Inject
	MPart mpart;

	private SortHeaderLayer sortHeaderLayer;

	private GroupByHeaderLayer groupByHeaderLayer;

	private boolean expandGroups = false;

	private FixedSummaryRowLayer summaryRowLayer;

	private DefaultColumnHeaderDataLayer columnHeaderDataLayer;

	private SelectionThread selectionThread;

	@PostConstruct
	public void createComposite(Composite parent, EModelService modelService) {
		new FormToolkit(parent.getDisplay());
		getForm();
		data = dataFormService.getTableFromFormIndex(form);

		parent.setLayout(new GridLayout());

		natTable = createNatTable(parent, form, getData(), selectionService, mPerspective.getContext());
		loadPrefs(Constants.LAST_STATE, autoLoadIndex);
	}

	@PersistState
	public void persistState() {
		savePrefs(true, Constants.LAST_STATE);
	}

	/**
	 * xxx.index.size (index,breite(int)); -> Speichert auch Reihenfolge der Spalte <br>
	 * xxx.index.sortby (index,sortDirection(ASC|DESC);index2....); -> Sortierung <br>
	 * xxx.index.groupby (expand[0,1];index;index2...); -> Gruppierung <br>
	 * Ähnlich im SearchPart
	 * 
	 * @param saveRowConfig
	 * @param name
	 */
	@PersistTableSelection
	public void savePrefs(@Named("SaveRowConfig") boolean saveRowConfig, @Named("ConfigName") String name) {
		if (!saveRowConfig) {
			return;
		}

		String tableName = form.getIndexView().getSource();

		// Spaltenanordung und -breite
		String size = "";
		for (int i : bodyLayerStack.getColumnReorderLayer().getColumnIndexOrder()) {
			size += i + "," + bodyLayerStack.getBodyDataLayer().getColumnWidthByPosition(i) + ";";

		}
		prefs.put(tableName + "." + name + ".index.size", size);

		// Sortierung
		String sort = "";
		for (int i : sortHeaderLayer.getSortModel().getSortedColumnIndexes()) {
			sort += i + "," + sortHeaderLayer.getSortModel().getSortDirection(i) + ";";
		}
		prefs.put(tableName + "." + name + ".index.sortby", sort);

		// Gruppierung
		String group = "";
		if (expandGroups) {
			group += 1 + ";";
		} else {
			group += 0 + ";";
		}
		for (int i : getGroupByHeaderLayer().getGroupByModel().getGroupByColumnIndexes()) {
			group += i + ";";
		}
		prefs.put(tableName + "." + name + ".index.groupby", group);

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

	}

	@LoadTableSelection
	public void loadPrefs(@Named("ConfigName") String name) {
		loadPrefs(name, true);
	}

	public void loadPrefs(String name, boolean loadIndex) {
		// Spaltenanordung und -breite
		String tableName = form.getIndexView().getSource();
		String string = prefs.get(tableName + "." + name + ".index.size", null);
		if (string != null && !string.equals("")) {

			String[] fields = string.split(";");
			ArrayList<Integer> order = new ArrayList<>();
			for (String s : fields) {
				String[] keyValue = s.split(",");
				int position = Integer.parseInt(keyValue[0].trim());
				int width = Integer.parseInt(keyValue[1].trim());
				order.add(position);
				bodyLayerStack.getBodyDataLayer().setColumnWidthByPosition(position, width);
			}
			// Änderungen in der Maske beachten (neue Spalten, Spalten gelöscht)
			if (bodyLayerStack.getColumnReorderLayer().getColumnIndexOrder().size() < order.size()) {
				ArrayList<Integer> toDelete = new ArrayList<>();
				for (int i : order) {
					if (!bodyLayerStack.getColumnReorderLayer().getColumnIndexOrder().contains(i)) {
						toDelete.add(i);
					}
				}
				order.removeAll(toDelete);
			}
			bodyLayerStack.getColumnReorderLayer().getColumnIndexOrder().removeAll(order);
			bodyLayerStack.getColumnReorderLayer().getColumnIndexOrder().addAll(0, order);
			bodyLayerStack.getColumnReorderLayer().reorderColumnPosition(0, 0); // Damit erzwingen wir einen redraw
		}

		// Sortierung
		string = prefs.get(tableName + "." + name + ".index.sortby", null);
		if (string != null && !string.equals("")) {
			String[] fields = string.split(";");
			sortHeaderLayer.getSortModel().clear();
			for (String s : fields) {
				String[] keyValue = s.split(",");
				int index = Integer.parseInt(keyValue[0].trim());
				SortDirectionEnum direction = SortDirectionEnum.valueOf(keyValue[1].trim());
				sortHeaderLayer.getSortModel().sort(index, direction, true);
			}
		}

		// Gruppierung
		string = prefs.get(tableName + "." + name + ".index.groupby", null);
		if (string != null && !string.equals("")) {
			String[] fields = string.split(";");
			getGroupByHeaderLayer().getGroupByModel().clearGroupByColumnIndexes();
			for (String s : Arrays.copyOfRange(fields, 1, fields.length)) {
				int index = Integer.parseInt(s);
				getGroupByHeaderLayer().getGroupByModel().addGroupByColumnIndex(index);
			}
			if (fields[0].equals("0")) {
				collapseGroups("");
			} else {
				expandGroups("");
			}
		}

		if (loadIndex) {
			ParameterizedCommand cmd = commandService.createCommand("aero.minova.rcp.rcp.command.loadindex", null);
			handlerService.executeHandler(cmd);
		}
	}

	/**
	 * Setzt die größe der Spalten aus dem sichtbaren Bereiches im Index-Bereich auf die Maximale Breite des Inhalts.
	 *
	 * @param mPart
	 */
	@Inject
	@Optional
	public void resize(@UIEventTopic(Constants.BROKER_RESIZETABLE) MPart mPart) {
		if (!mPart.equals(this.mpart)) {
			return;
		}
		NatTableUtil.resize(natTable);
	}

	@Inject
	@Optional
	public void clearSelection(@UIEventTopic(Constants.BROKER_CLEARSELECTION) MPerspective perspective) {
		if (!perspective.equals(this.mPerspective)) {
			return;
		}
		bodyLayerStack.getSelectionLayer().clear(false);
	}

	/**
	 * Diese Methode ließt die Index-Spalten aus und erstellet daraus eine Table, diese wird dann an den CAS als Anfrage übergeben.
	 */
	@Inject
	@Optional
	public void load(@UIEventTopic(Constants.BROKER_LOADINDEXTABLE) Map<MPerspective, Table> map) {
		if (map.get(mPerspective) != null) {
			// clear the group by summary cache so the new summary calculation gets triggered
			bodyLayerStack.getBodyDataLayer().clearCache();
			Table table = map.get(mPerspective);
			updateData(table.getRows());

			if (table.getRows().isEmpty()) {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), translationService.translate("@Information", null),
						translationService.translate("@msg.NoRecordsLoaded", null));
			}
		}
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		if (columnPropertyAccessor != null) {
			columnPropertyAccessor.translate(translationService);
			String[] propertyNames = columnPropertyAccessor.getPropertyNames();
			for (int i = 0; i < columnPropertyAccessor.getColumnCount(); i++) {
				columnHeaderLayer.renameColumnIndex(i, columnPropertyAccessor.getTableHeadersMap().get(propertyNames[i]));
			}
		}
	}

	@Inject
	@Optional
	private void collapseGroups(@UIEventTopic(Constants.BROKER_COLLAPSEINDEX) String s) {
		natTable.doCommand(new TreeCollapseAllCommand());
		expandGroups = false;
	}

	@Inject
	@Optional
	private void expandGroups(@UIEventTopic(Constants.BROKER_EXPANDINDEX) String s) {
		natTable.doCommand(new TreeExpandAllCommand());
		expandGroups = true;
	}

	public NatTable createNatTable(Composite parent, Form form, Table table, ESelectionService selectionService, IEclipseContext context) {

		this.context = context;

		// Datenmodel für die Eingaben
		ConfigRegistry configRegistry = new ConfigRegistry();
		columnPropertyAccessor = new MinovaColumnPropertyAccessor(table, form);
		columnPropertyAccessor.initPropertyNames(translationService);

		// create the body stack
		bodyLayerStack = new BodyLayerStack<>(table.getRows(), columnPropertyAccessor, configRegistry);
		bodyLayerStack.getBodyDataLayer().setConfigLabelAccumulator(new ColumnLabelAccumulator());

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(columnPropertyAccessor.getPropertyNames(),
				columnPropertyAccessor.getTableHeadersMap());
		columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		columnHeaderLayer = new ColumnHeaderLayer(getColumnHeaderDataLayer(), bodyLayerStack, bodyLayerStack.getSelectionLayer());

		sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
				new GlazedListsSortModel<>(bodyLayerStack.getSortedList(), columnPropertyAccessor, configRegistry, getColumnHeaderDataLayer()), false);
		// Eigenen Sort-Comparator auf alle Spalten registrieren (Verhindert Fehler bei Zeilen, die keinen von- oder bis-Wert haben)
		ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(getColumnHeaderDataLayer());
		getColumnHeaderDataLayer().setConfigLabelAccumulator(labelAccumulator);
		for (int i = 0; i < getColumnHeaderDataLayer().getColumnCount(); i++) {
			labelAccumulator.registerColumnOverrides(i, Constants.COMPARATOR_LABEL);
		}
		configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, new CustomComparator(), DisplayMode.NORMAL, Constants.COMPARATOR_LABEL);

		// connect sortModel to GroupByDataLayer to support sorting by group by summary values
		bodyLayerStack.getBodyDataLayer().initializeTreeComparator(sortHeaderLayer.getSortModel(), bodyLayerStack.getTreeLayer(), true);

		// build the Summary Row
		summaryRowLayer = new FixedSummaryRowLayer(bodyLayerStack.getGlazedListsEventLayer(), bodyLayerStack.getViewportLayer(), configRegistry, false);
		getSummaryRowLayer().setHorizontalCompositeDependency(false);
		CompositeLayer summaryComposite = new CompositeLayer(1, 2);
		summaryComposite.setChildLayer("SUMMARY", getSummaryRowLayer(), 0, 0);
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
		groupByHeaderLayer = new GroupByHeaderLayer(bodyLayerStack.getGroupByModel(), gridLayer, columnHeaderDataProvider, columnHeaderLayer);
		compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, getGroupByHeaderLayer(), 0, 0);
		compositeGridLayer.setChildLayer("Grid", gridLayer, 0, 1);

		SelectionLayer selectionLayer = bodyLayerStack.getSelectionLayer();
		selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());

		CopyDataCommandHandler copyHandler = new CopyDataCommandHandler(selectionLayer, getColumnHeaderDataLayer(), rowHeaderDataLayer);
		copyHandler.setCopyFormattedText(true);
		gridLayer.registerCommandHandler(copyHandler);

		natTable = new NatTable(parent, compositeGridLayer, false);
		// as the autoconfiguration of the NatTable is turned off, we have to add the DefaultNatTableStyleConfiguration and the ConfigRegistry manually
		natTable.setConfigRegistry(configRegistry);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());

		natTable.addConfiguration(new SingleClickSortConfiguration() {
			@Override
			public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
				// normal
				uiBindingRegistry.registerFirstSingleClickBinding(new ColumnHeaderClickEventMatcher(SWT.NONE, 1), new SortColumnAction(false));

				// multi
				int keyMask = SWT.MOD3;
				// für Linux andere Tastenkombi definieren
				if (System.getProperty("os.name").startsWith("Linux")) {
					keyMask |= SWT.MOD2;
				}
				uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(keyMask), new SortColumnAction(true));
			}
		});

		MinovaIndexConfiguration mic = new MinovaIndexConfiguration(table.getColumns(), form);
		natTable.addConfiguration(mic);
		bodyLayerStack.columnHideShowLayer.hideColumnPositions(mic.getHiddenColumns());

		// add group by configuration
		natTable.addConfiguration(new GroupByHeaderMenuConfiguration(natTable, getGroupByHeaderLayer()));
		// adds the key bindings that allow space bar to be pressed to expand/collapse tree nodes
		natTable.addConfiguration(new TreeLayerExpandCollapseKeyBindings(bodyLayerStack.getTreeLayer(), bodyLayerStack.getSelectionLayer()));

		// Bei Doppelklick auf ein gruppiertes Element diese Gruppierung entfernen
		natTable.addConfiguration(new AbstractUiBindingConfiguration() {
			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				uiBindingRegistry.registerDoubleClickBinding(new MouseEventMatcher(SWT.NONE, GroupByHeaderLayer.GROUP_BY_REGION) {
					@Override
					public boolean matches(NatTable natTable, MouseEvent event, LabelStack regionLabels) {
						if (super.matches(natTable, event, regionLabels)) {
							return groupByHeaderLayer.getGroupByColumnIndexAtXY(event.x, event.y) >= 0;
						}
						return false;
					}
				}, new IMouseAction() {
					@Override
					public void run(NatTable natTable, MouseEvent event) {
						int groupByColumnIndex = groupByHeaderLayer.getGroupByColumnIndexAtXY(event.x, event.y);
						natTable.doCommand(new UngroupByColumnIndexCommand(groupByColumnIndex));
					}
				});
			}
		});

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

	public NatTable getNattable() {
		return natTable;
	}

	public SelectionLayer getSelectionLayer() {
		return bodyLayerStack.getSelectionLayer();
	}

	public BodyLayerStack getBodyLayerStack() {
		return this.bodyLayerStack;
	}

	/**
	 * Always encapsulate the body layer stack in an AbstractLayerTransform to ensure that the index transformations are performed in later commands.
	 *
	 * @param <T>
	 */
	public class BodyLayerStack<T> extends AbstractLayerTransform {

		private final SortedList<T> sortedList;

		private final IRowDataProvider<Row> bodyDataProvider;

		private final SelectionLayer selectionLayer;

		private final GroupByModel groupByModel = new GroupByModel();

		private EventList<T> eventList;

		private GroupByDataLayer<T> bodyDataLayer;

		private TreeLayer treeLayer;

		private GlazedListsEventLayer glazedListsEventLayer;

		private ViewportLayer viewportLayer;

		private ColumnReorderLayer columnReorderLayer;

		private ColumnHideShowLayer columnHideShowLayer;

		public BodyLayerStack(List<T> values, IColumnPropertyAccessor<T> columnPropertyAccessor, ConfigRegistry configRegistry) {
			eventList = GlazedLists.eventList(values);
			TransformedList<T, T> rowObjectsGlazedList = GlazedLists.threadSafeList(eventList);

			// use the SortedList constructor with 'null' for the Comparator because the Comparator will be set by configuration
			this.sortedList = new SortedList<>(rowObjectsGlazedList, null);

			bodyDataLayer = new GroupByDataLayer<>(getGroupByModel(), this.sortedList, columnPropertyAccessor, configRegistry);

			// get the IDataProvider that was created by the GroupByDataLayer
			this.bodyDataProvider = (IRowDataProvider<Row>) bodyDataLayer.getDataProvider();

			// layer for event handling of GlazedLists and PropertyChanges
			glazedListsEventLayer = new GlazedListsEventLayer<>(bodyDataLayer, this.sortedList);

			columnReorderLayer = new ColumnReorderLayer(glazedListsEventLayer);
			columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
			this.selectionLayer = new SelectionLayer(getColumnHideShowLayer());

			selectionLayer.addLayerListener(event -> {
				if (event instanceof RowSelectionEvent) {
					if (selectionThread != null) {
						selectionThread.interrupt();
					}
					selectionThread = new SelectionThread(tableSelectionBuffer);
					selectionThread.start();
				} else if (event instanceof RowStructuralRefreshEvent) {
					NatTableUtil.resizeRows(natTable);
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

		public IRowDataProvider<Row> getBodyDataProvider() {
			return this.bodyDataProvider;
		}

		public GroupByModel getGroupByModel() {
			return this.groupByModel;
		}

		public EventList<T> getList() {
			return eventList;
		}

		public ColumnReorderLayer getColumnReorderLayer() {
			return columnReorderLayer;
		}

		public ColumnHideShowLayer getColumnHideShowLayer() {
			return columnHideShowLayer;
		}
	}

	class SelectionThread extends Thread {
		private int sleepMillis;

		/*
		 * Thread, der für sleepMillis Millisekunden schläft und danach die Daten ins Detail lädt, wenn er nicht unterbrochen wurde
		 */
		public SelectionThread(int sleepMillis) {
			this.sleepMillis = sleepMillis;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(sleepMillis);
			} catch (InterruptedException e) {
				return;
			}

			// Ausgewählten Zeilen müssen gefiltert werden, um Gruppen-Zeilen zu entfernen
			List c = SelectionUtils.getSelectedRowObjects(getSelectionLayer(), getBodyLayerStack().getBodyDataProvider(), false);
			List<Row> collection = (List<Row>) c.stream().filter(p -> (p instanceof Row)).collect(Collectors.toList());

			Table t = dataFormService.getTableFromFormIndex(form);
			for (Row r : collection) {
				t.addRow(r);
			}
			if (!collection.isEmpty()) {
				context.set(Constants.BROKER_ACTIVEROWS, t);
			}
		}
	}

	static class CountSummaryProvider implements ISummaryProvider {

		private IDataProvider dataProvider;

		public CountSummaryProvider(IDataProvider dataProvider) {
			this.dataProvider = dataProvider;
		}

		@Override
		public Object summarize(int columnIndex) {
			return this.dataProvider.getRowCount();
		}
	}

	static class AverageSummaryProvider implements ISummaryProvider {

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
			if (valueRows == 0) {
				return 0;
			}
			return total / valueRows;
		}
	}

	static class MinSummaryProvider implements ISummaryProvider {

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
					if (((Number) dataValue).doubleValue() < min) {
						min = ((Number) dataValue).doubleValue();
					}
				}
			}
			if (min == Double.MAX_VALUE) {
				return 0;
			}
			return min;
		}
	}

	static class MaxSummaryProvider implements ISummaryProvider {

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
					if (((Number) dataValue).doubleValue() > max) {
						max = ((Number) dataValue).doubleValue();
					}
				}
			}
			if (max == Double.MIN_VALUE) {
				return 0;
			}
			return max;
		}
	}

	private class CustomComparator implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				} else {
					return -1;
				}
			} else if (o2 == null) {
				return 1;
			} else if (o1 instanceof Comparable && o2 instanceof Comparable && o1.getClass().equals(o2.getClass())) { // Auch überprüfen, ob die Objekte die
																														// gleiche Klasse haben
				return ((Comparable) o1).compareTo(o2);
			} else {
				return o1.toString().compareTo(o2.toString());
			}
		}
	}

	public void updateData(List<Row> list) {
		bodyLayerStack.getSortedList().clear();
		bodyLayerStack.getSortedList().addAll(list);
		natTable.refresh(false); // Damit Summary-Row richtig aktualisiert wird
	}

	public SortedList<Row> getSortedList() {
		return bodyLayerStack.getSortedList();
	}

	public ColumnHeaderLayer getColumnHeaderLayer() {
		return columnHeaderLayer;
	}

	public Table getData() {
		return data;
	}

	public GroupByHeaderLayer getGroupByHeaderLayer() {
		return groupByHeaderLayer;
	}

	public FixedSummaryRowLayer getSummaryRowLayer() {
		return summaryRowLayer;
	}

	public DefaultColumnHeaderDataLayer getColumnHeaderDataLayer() {
		return columnHeaderDataLayer;
	}
}
