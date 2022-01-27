package aero.minova.rcp.rcp.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
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
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.command.UngroupByColumnIndexCommand;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.event.RowStructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.painter.layer.GridLineCellLayerPainter;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionUtils;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.action.SortColumnAction;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.sort.event.ColumnHeaderClickEventMatcher;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeCollapseAllCommand;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeExpandAllCommand;
import org.eclipse.nebula.widgets.nattable.tree.config.TreeLayerExpandCollapseKeyBindings;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.form.setup.util.XBSUtil;
import aero.minova.rcp.form.setup.xbs.Map.Entry;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.nattable.MinovaStatisticConfiguration;
import aero.minova.rcp.rcp.util.LoadTableSelection;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.rcp.util.PersistTableSelection;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;

public class WFCStatisticIndexPart {

	private static final String STATISTIC = "Statistic";

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TABLE_SELECTION_BUFFER_MS)
	int tableSelectionBuffer;

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Inject
	TranslationService translationService;

	@Inject
	MPart mpart;

	@Inject
	IEventBroker broker;

	@Inject
	MApplication mApplication;

	private Table data;

	private NatTable natTable;

	private BodyLayerStack<Row> bodyLayerStack;

	private SortHeaderLayer<Row> sortHeaderLayer;

	private GroupByHeaderLayer groupByHeaderLayer;

	private SelectionThread selectionThread;

	private boolean expandGroups;

	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout());
		createStatisticDataFromXBS();
		natTable = createNatTable(parent, data);
		loadPrefs(Constants.LAST_STATE);
	}

	/**
	 * Diese Methode erstellt aus der ausgelesenen XBS die Statistsic Einträge und speichert sie in das Table Objekt.
	 *
	 * @param mApplication
	 */
	private void createStatisticDataFromXBS() {
		String name = translationService.translate("@Name", null);
		String type = translationService.translate("@Type", null);
		String description = translationService.translate("@Description", null);

		data = TableBuilder.newTable("statistic").withColumn("MatchCode", DataType.STRING).withColumn("name", DataType.STRING)
				.withColumn("type", DataType.STRING).withColumn("description", DataType.STRING).create();
		data.getColumns().get(1).setLabel(name);
		data.getColumns().get(2).setLabel(type);
		data.getColumns().get(3).setLabel(description);

		Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);
		Node statisticNode = XBSUtil.getNodeWithName(preferences, "Statistic");
		for (Node n : statisticNode.getNode()) {
			Row row = RowBuilder.newRow().withValue("").withValue("").withValue("").withValue("").create();
			row.setValue(new Value(n.getName()), 0);
			for (Entry e : n.getMap().getEntry()) {
				switch (e.getKey().toLowerCase()) {
				case "":
					row.setValue(new Value(translationService.translate(e.getValue(), null)), 1);
					break;
				case "group":
					row.setValue(new Value(translationService.translate(e.getValue(), null)), 2);
					break;
				case "description":
					row.setValue(new Value(translationService.translate(e.getValue(), null)), 3);
					break;
				default:
					break;
				}
			}
			data.addRow(row);
		}
	}

	public NatTable createNatTable(Composite parent, Table table) {

		// Datenmodel für die Eingaben
		ConfigRegistry configRegistry = new ConfigRegistry();
		MinovaColumnPropertyAccessor columnPropertyAccessor = new MinovaColumnPropertyAccessor(table);
		columnPropertyAccessor.initPropertyNames(translationService);

		// create the body stack
		bodyLayerStack = new BodyLayerStack<>(table.getRows(), columnPropertyAccessor, configRegistry);
		bodyLayerStack.getBodyDataLayer().setConfigLabelAccumulator(new ColumnLabelAccumulator());

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(columnPropertyAccessor.getPropertyNames(),
				columnPropertyAccessor.getTableHeadersMap());
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayerStack, bodyLayerStack.getSelectionLayer());

		sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
				new GlazedListsSortModel<>(bodyLayerStack.getSortedList(), columnPropertyAccessor, configRegistry, columnHeaderDataLayer), false);

		// connect sortModel to GroupByDataLayer to support sorting by group by summary values
		bodyLayerStack.getBodyDataLayer().initializeTreeComparator(sortHeaderLayer.getSortModel(), bodyLayerStack.getTreeLayer(), true);

		// build the row header layer
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyLayerStack.getBodyDataProvider());
		DataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, bodyLayerStack, bodyLayerStack.getSelectionLayer());

		// build the corner layer
		IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		ILayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

		// build the grid layer
		GridLayer gridLayer = new GridLayer(bodyLayerStack, sortHeaderLayer, rowHeaderLayer, cornerLayer);

		// ensure the body data layer uses a layer painter with correct configured clipping
		bodyLayerStack.getBodyDataLayer().setLayerPainter(new GridLineCellLayerPainter(false, true));

		// set the group by header on top of the grid
		CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
		groupByHeaderLayer = new GroupByHeaderLayer(bodyLayerStack.getGroupByModel(), gridLayer, columnHeaderDataProvider, columnHeaderLayer);
		compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, groupByHeaderLayer, 0, 0);
		compositeGridLayer.setChildLayer("Grid", gridLayer, 0, 1);

		SelectionLayer selectionLayer = bodyLayerStack.getSelectionLayer();
		selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());

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

		MinovaStatisticConfiguration mic = new MinovaStatisticConfiguration(table.getColumns());
		natTable.addConfiguration(mic);

		// Wir brauchen die erste Spalte mit dem Namen der Statistik nicht für den Anwender sondern nur Intern!
		bodyLayerStack.columnHideShowLayer.hideColumnPositions(0);

		// add group by configuration
		natTable.addConfiguration(new GroupByHeaderMenuConfiguration(natTable, groupByHeaderLayer));
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
				}, (eventNatTable, event) -> {
					int groupByColumnIndex = groupByHeaderLayer.getGroupByColumnIndexAtXY(event.x, event.y);
					eventNatTable.doCommand(new UngroupByColumnIndexCommand(groupByColumnIndex));
				});
			}
		});

		natTable.configure();
		natTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		return natTable;
	}

	public SelectionLayer getSelectionLayer() {
		return bodyLayerStack.getSelectionLayer();
	}

	public BodyLayerStack<Row> getBodyLayerStack() {
		return this.bodyLayerStack;
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

	@PersistState
	public void persistState() {
		savePrefs(true, Constants.LAST_STATE);
	}

	@PersistTableSelection
	public void savePrefs(@Named("SaveRowConfig") boolean saveRowConfig, @Named("ConfigName") String name) {
		if (!saveRowConfig) {
			return;
		}

		String tableName = STATISTIC;

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
		for (int i : groupByHeaderLayer.getGroupByModel().getGroupByColumnIndexes()) {
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
	public void loadPrefs(String name) {
		// Spaltenanordung und -breite
		String tableName = STATISTIC;
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
			groupByHeaderLayer.getGroupByModel().clearGroupByColumnIndexes();
			for (String s : Arrays.copyOfRange(fields, 1, fields.length)) {
				int index = Integer.parseInt(s);
				groupByHeaderLayer.getGroupByModel().addGroupByColumnIndex(index);
			}
			if (fields[0].equals("0")) {
				collapseGroups("");
			} else {
				expandGroups("");
			}
		}
	}

	/**
	 * Always encapsulate the body layer stack in an AbstractLayerTransform to ensure that the index transformations are performed in later commands.
	 *
	 * @param <T>
	 */
	public class BodyLayerStack<T> extends AbstractLayerTransform {

		private final SortedList<T> sortedList;

		private final IRowDataProvider<T> bodyDataProvider;

		private final SelectionLayer selectionLayer;

		private final GroupByModel groupByModel = new GroupByModel();

		private EventList<T> eventList;

		private GroupByDataLayer<T> bodyDataLayer;

		private TreeLayer treeLayer;

		private GlazedListsEventLayer<T> glazedListsEventLayer;

		private ViewportLayer viewportLayer;

		private ColumnReorderLayer columnReorderLayer;

		private ColumnHideShowLayer columnHideShowLayer;

		@SuppressWarnings("unchecked")
		public BodyLayerStack(List<T> values, IColumnPropertyAccessor<T> columnPropertyAccessor, ConfigRegistry configRegistry) {
			eventList = GlazedLists.eventList(values);
			TransformedList<T, T> rowObjectsGlazedList = GlazedLists.threadSafeList(eventList);

			// use the SortedList constructor with 'null' for the Comparator because the Comparator will be set by configuration
			this.sortedList = new SortedList<>(rowObjectsGlazedList, null);

			bodyDataLayer = new GroupByDataLayer<>(getGroupByModel(), this.sortedList, columnPropertyAccessor, configRegistry);

			// get the IDataProvider that was created by the GroupByDataLayer
			this.bodyDataProvider = (IRowDataProvider<T>) bodyDataLayer.getDataProvider();

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

		public GlazedListsEventLayer<T> getGlazedListsEventLayer() {
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

		public IRowDataProvider<T> getBodyDataProvider() {
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

			List<Row> c = SelectionUtils.getSelectedRowObjects(getSelectionLayer(), getBodyLayerStack().getBodyDataProvider(), false);
			broker.post(Constants.BROKER_SELECTSTATISTIC, c.get(0));
		}
	}

}
