package aero.minova.rcp.rcp.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
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
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.layer.GridLineCellLayerPainter;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.action.SortColumnAction;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.sort.event.ColumnHeaderClickEventMatcher;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.summaryrow.FixedSummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeCollapseAllCommand;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeExpandAllCommand;
import org.eclipse.nebula.widgets.nattable.tree.config.TreeLayerExpandCollapseKeyBindings;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.rcp.nattable.MinovaColumnConfiguration;
import aero.minova.rcp.rcp.util.CustomComparator;
import aero.minova.rcp.rcp.util.LoadTableSelection;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.rcp.util.NattableSummaryUtil;
import aero.minova.rcp.rcp.util.PersistTableSelection;
import aero.minova.rcp.util.OSUtil;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;

public abstract class WFCNattablePart extends WFCFormPart {

	@Inject
	@Preference
	protected IEclipsePreferences prefs;

	@Inject
	protected ESelectionService selectionService;

	@Inject
	protected ECommandService commandService;

	@Inject
	protected EHandlerService handlerService;

	@Inject
	protected EModelService modelService;

	@Inject
	private IMinovaJsonService mjs;

	@Inject
	protected TranslationService translationService;

	@Inject
	protected MPart mPart;

	@Inject
	protected Logger logger;

	protected IEclipseContext context;

	protected NatTable natTable;
	protected MinovaColumnPropertyAccessor columnPropertyAccessor;
	protected ColumnHeaderLayer columnHeaderLayer;
	protected GroupByHeaderLayer groupByHeaderLayer;
	protected BodyLayerStack<Row> bodyLayerStack;
	protected SortHeaderLayer<Object> sortHeaderLayer;
	protected FixedSummaryRowLayer summaryRowLayer;
	protected DefaultColumnHeaderDataLayer columnHeaderDataLayer;
	protected MinovaColumnConfiguration mcc;
	protected GridLayer gridLayer;
	protected DefaultRowHeaderDataLayer rowHeaderDataLayer;

	protected Table data;

	protected boolean expandGroups = false;

	/**
	 * Layout des Composites setzten, muss {@link #createNatTable(Composite, Form, Table) createNatTable} aufrufen
	 * 
	 * @param parent
	 */
	protected abstract void createComposite(Composite parent);

	/**
	 * Hier können weitere Konfigurationen zur Nattable hinzugefügt werden. Z.B. der Selection Mode oder Listener auf Events
	 * 
	 * @param natTable
	 */
	protected abstract void addNattableConfiguration(NatTable natTable);

	/**
	 * Hier muss die {@link MinovaColumnConfiguration} zurückgegeben werden, die für die Nattable genutzt werden soll
	 * 
	 * @param table
	 * @return
	 */
	public abstract MinovaColumnConfiguration createColumnConfiguration(Table table);

	/**
	 * Sollen Spalten Gruppiert werden können?
	 * 
	 * @return
	 */
	protected abstract boolean useGroupBy();

	/**
	 * Sollen Spalten Sortiert werden können?
	 * 
	 * @return
	 */
	protected abstract boolean useSortingHeader();

	/**
	 * Sollen es eine Zusammenfassungs-Zeile geben?
	 * 
	 * @return
	 */
	protected abstract boolean useSummaryRow();

	//////////////////////////////
	// Erstellen der Tabelle /////
	//////////////////////////////

	@PostConstruct
	public void init(Composite parent) {
		context = mPerspective.getContext();
		createComposite(parent);
	}

	public NatTable createNatTable(Composite parent, Form form, Table table) {

		// Datenmodel für die Eingaben
		ConfigRegistry configRegistry = new ConfigRegistry();
		columnPropertyAccessor = form == null ? new MinovaColumnPropertyAccessor(table) : new MinovaColumnPropertyAccessor(table, form);
		columnPropertyAccessor.initPropertyNames(translationService);

		// create the body stack
		bodyLayerStack = new BodyLayerStack<>(table.getRows(), columnPropertyAccessor, configRegistry);
		bodyLayerStack.getBodyDataLayer().setConfigLabelAccumulator(new ColumnLabelAccumulator());

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(columnPropertyAccessor.getPropertyNames(),
				columnPropertyAccessor.getTableHeadersMap());
		columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		columnHeaderLayer = new ColumnHeaderLayer(getColumnHeaderDataLayer(), bodyLayerStack, bodyLayerStack.getSelectionLayer());

		ILayer headerLayer = columnHeaderLayer;
		if (useSortingHeader()) {
			sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
					new GlazedListsSortModel<>(bodyLayerStack.getSortedList(), columnPropertyAccessor, configRegistry, getColumnHeaderDataLayer()), false);
			// Eigenen Sort-Comparator auf alle Spalten registrieren (Verhindert Fehler bei Zeilen, die keinen von- oder bis-Wert haben)
			ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(getColumnHeaderDataLayer());
			getColumnHeaderDataLayer().setConfigLabelAccumulator(labelAccumulator);
			for (int i = 0; i < getColumnHeaderDataLayer().getColumnCount(); i++) {
				labelAccumulator.registerColumnOverrides(i, Constants.COMPARATOR_LABEL);
			}
			configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, new CustomComparator(), DisplayMode.NORMAL,
					Constants.COMPARATOR_LABEL);

			if (useSummaryRow()) {
				// connect sortModel to GroupByDataLayer to support sorting by group by summary values
				((GroupByDataLayer) bodyLayerStack.getBodyDataLayer()).initializeTreeComparator(sortHeaderLayer.getSortModel(), bodyLayerStack.getTreeLayer(),
						true);
			}

			headerLayer = sortHeaderLayer;
		}

		// build the row header layer
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyLayerStack.getBodyDataProvider());
		rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);

		ILayer bodyLayer;
		ILayer rowHeaderLayer;
		if (useSummaryRow()) {
			// build the Summary Row
			summaryRowLayer = new FixedSummaryRowLayer(bodyLayerStack.getGlazedListsEventLayer(), bodyLayerStack.getViewportLayer(), configRegistry, false);
			getSummaryRowLayer().setHorizontalCompositeDependency(false);
			CompositeLayer summaryComposite = new CompositeLayer(1, 2);
			summaryComposite.setChildLayer("SUMMARY", getSummaryRowLayer(), 0, 0);
			summaryComposite.setChildLayer(GridRegion.BODY, bodyLayerStack.getViewportLayer(), 0, 1);
			bodyLayer = summaryComposite;

			rowHeaderLayer = new FixedSummaryRowHeaderLayer(rowHeaderDataLayer, summaryComposite, bodyLayerStack.getSelectionLayer());
			((FixedSummaryRowHeaderLayer) rowHeaderLayer).setSummaryRowLabel("");
		} else {
			rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, bodyLayerStack.getViewportLayer(), bodyLayerStack.getSelectionLayer());
			bodyLayer = bodyLayerStack.getViewportLayer();
		}

		// build the corner layer
		IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		ILayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

		gridLayer = new GridLayer(bodyLayer, headerLayer, rowHeaderLayer, cornerLayer);

		// ensure the body data layer uses a layer painter with correct configured clipping
		bodyLayerStack.getBodyDataLayer().setLayerPainter(new GridLineCellLayerPainter(false, true));

		ILayer completeTable = gridLayer;
		if (useGroupBy()) {
			// set the group by header on top of the grid
			CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
			groupByHeaderLayer = new GroupByHeaderLayer(bodyLayerStack.getGroupByModel(), gridLayer, columnHeaderDataProvider, columnHeaderLayer);
			compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, getGroupByHeaderLayer(), 0, 0);
			compositeGridLayer.setChildLayer("Grid", gridLayer, 0, 1);
			completeTable = compositeGridLayer;
		}

		natTable = new NatTable(parent, completeTable, false);

		// as the autoconfiguration of the NatTable is turned off, we have to add the DefaultNatTableStyleConfiguration and the ConfigRegistry manually
		natTable.setConfigRegistry(configRegistry);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());

		if (useSortingHeader()) {
			natTable.addConfiguration(getSortConfig());
		}

		mcc = createColumnConfiguration(table);
		natTable.addConfiguration(mcc);
		bodyLayerStack.columnHideShowLayer.hideColumnPositions(mcc.getHiddenColumns());

		addNattableConfiguration(natTable);

		if (useSummaryRow()) {
			NattableSummaryUtil.configureSummary(form, natTable, bodyLayerStack.getSortedList(), columnPropertyAccessor);
		}

		if (useGroupBy()) {
			// add group by configuration
			natTable.addConfiguration(new GroupByHeaderMenuConfiguration(natTable, getGroupByHeaderLayer()));
			// adds the key bindings that allow space bar to be pressed to expand/collapse tree nodes
			natTable.addConfiguration(new TreeLayerExpandCollapseKeyBindings(bodyLayerStack.getTreeLayer(), bodyLayerStack.getSelectionLayer()));

			// Bei Doppelklick auf ein gruppiertes Element diese Gruppierung entfernen
			natTable.addConfiguration(getDoubleClickConfig());
		}

		natTable.configure();
		natTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		return natTable;
	}

	private AbstractUiBindingConfiguration getDoubleClickConfig() {
		return new AbstractUiBindingConfiguration() {
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
				}, (natTableDummy, event) -> {
					int groupByColumnIndex = groupByHeaderLayer.getGroupByColumnIndexAtXY(event.x, event.y);
					natTableDummy.doCommand(new UngroupByColumnIndexCommand(groupByColumnIndex));
				});
			}
		};
	}

	private SingleClickSortConfiguration getSortConfig() {
		return new SingleClickSortConfiguration() {
			@Override
			public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
				// normal
				uiBindingRegistry.registerFirstSingleClickBinding(new ColumnHeaderClickEventMatcher(SWT.NONE, 1), new SortColumnAction(false));

				// multi
				int keyMask = SWT.MOD3;
				// für Linux andere Tastenkombi definieren
				if (OSUtil.isLinux()) {
					keyMask |= SWT.MOD2;
				}
				uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(keyMask), new SortColumnAction(true));
			}
		};
	}

	/**
	 * Always encapsulate the body layer stack in an AbstractLayerTransform to ensure that the index transformations are performed in later commands.
	 *
	 * @param <T>
	 */
	public class BodyLayerStack<T> extends AbstractLayerTransform {

		protected final SortedList<T> sortedList;

		protected final IDataProvider bodyDataProvider;

		protected final SelectionLayer selectionLayer;

		protected final GroupByModel groupByModel = new GroupByModel();

		protected EventList<T> eventList;

		protected DataLayer bodyDataLayer;

		protected TreeLayer treeLayer;

		protected GlazedListsEventLayer<T> glazedListsEventLayer;

		protected ViewportLayer viewportLayer;

		protected ColumnReorderLayer columnReorderLayer;

		protected ColumnHideShowLayer columnHideShowLayer;

		public BodyLayerStack(List<T> values, IColumnPropertyAccessor<T> columnPropertyAccessor, ConfigRegistry configRegistry) {
			eventList = GlazedLists.eventList(values);
			TransformedList<T, T> rowObjectsGlazedList = GlazedLists.threadSafeList(eventList);

			// use the SortedList constructor with 'null' for the Comparator because the Comparator will be set by configuration
			this.sortedList = new SortedList<>(rowObjectsGlazedList, null);

			if (useGroupBy()) {
				bodyDataLayer = new GroupByDataLayer<>(getGroupByModel(), this.sortedList, columnPropertyAccessor, configRegistry);
				// get the IDataProvider that was created by the GroupByDataLayer
				this.bodyDataProvider = bodyDataLayer.getDataProvider();
			} else {
				bodyDataProvider = new ListDataProvider<>(sortedList, columnPropertyAccessor);
				bodyDataLayer = new DataLayer(bodyDataProvider);
			}

			// layer for event handling of GlazedLists and PropertyChanges
			glazedListsEventLayer = new GlazedListsEventLayer<>(bodyDataLayer, this.sortedList);

			columnReorderLayer = new ColumnReorderLayer(glazedListsEventLayer);
			columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
			this.selectionLayer = new SelectionLayer(getColumnHideShowLayer());

			if (useGroupBy()) {
				treeLayer = new TreeLayer(this.selectionLayer, ((GroupByDataLayer) bodyDataLayer).getTreeRowModel());
				viewportLayer = new ViewportLayer(treeLayer);
			} else {
				viewportLayer = new ViewportLayer(selectionLayer);
			}

			// as the selection mouse bindings are registered for the region label GridRegion.BODY we need to set that region label to the viewport so the
			// selection via mouse is working correctly
			viewportLayer.setRegionName(GridRegion.BODY);

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

		public DataLayer getBodyDataLayer() {
			return this.bodyDataLayer;
		}

		public TreeLayer getTreeLayer() {
			return this.treeLayer;
		}

		public IRowDataProvider<Row> getBodyDataProvider() {
			return (IRowDataProvider<Row>) this.bodyDataProvider;
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

	//////////////////////////////
	// Persistieren //////////////
	//////////////////////////////

	@PersistState
	public void persistState() {
		savePrefs(true, Constants.LAST_STATE);
	}

	/**
	 * tableName.configName.table -> Inhalt der Tabelle <br>
	 * tableName.configName.nattableName.size (index,breite(int)); -> Speichert auch Reihenfolge der Spalte <br>
	 * tableName.configName.nattableName.sortby (index,sortDirection(ASC|DESC);index2....); -> Sortierung <br>
	 * tableName.configName.nattableName.groupby (expand[0,1];index;index2...); -> Gruppierung <br>
	 * tableName.configName.nattableName.hidden ([index;index2...]); -> Unsichtbare Spalten <br>
	 *
	 * @param saveRowConfig
	 * @param name
	 */
	@PersistTableSelection
	public void savePrefs(@Named("SaveRowConfig") boolean saveRowConfig, @Named("ConfigName") String name) {
		saveNattable();

		// Eingabe in Suche Speichern
		if (this instanceof WFCSearchPart) {
			prefs.put(form.getIndexView().getSource() + "." + name + ".table", mjs.table2Json(getData(), true));
		}

		// UI Speichern
		if (saveRowConfig) {
			String prefix = getPersistPrefix(name, form);
			saveUI(prefix);
		}

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			logger.error(e);
		}
	}

	private String getPersistPrefix(String name, Form form) {
		String nattableName = "nattable";
		String tableName = "table";
		if (this instanceof WFCSearchPart) {
			nattableName = "search";
			tableName = form.getIndexView().getSource();
		} else if (this instanceof WFCStatisticIndexPart) {
			nattableName = "index";
			tableName = "Statistic";
		} else if (this instanceof WFCIndexPart) {
			nattableName = "index";
			tableName = form.getIndexView().getSource();
		}

		return tableName + "." + name + "." + nattableName + ".";
	}

	private void saveUI(String prefix) {
		// Spaltenanordung und -breite
		StringBuilder size = new StringBuilder();
		for (int i : bodyLayerStack.getColumnReorderLayer().getColumnIndexOrder()) {
			size.append(i + "," + bodyLayerStack.getBodyDataLayer().getColumnWidthByPosition(i) + ";");
		}
		prefs.put(prefix + "size", size.toString());

		// Sichtbarkeit
		prefs.put(prefix + "hidden", bodyLayerStack.columnHideShowLayer.getHiddenColumnIndexes().toString());

		// Sortierung
		if (useSortingHeader()) {
			StringBuilder sort = new StringBuilder();
			for (int i : sortHeaderLayer.getSortModel().getSortedColumnIndexes()) {
				sort.append(i + "," + sortHeaderLayer.getSortModel().getSortDirection(i) + ";");
			}
			prefs.put(prefix + "sortby", sort.toString());
		}

		// Gruppierung
		if (useGroupBy()) {
			StringBuilder group = new StringBuilder();
			if (expandGroups) {
				group.append(1 + ";");
			} else {
				group.append(0 + ";");
			}
			for (int i : getGroupByHeaderLayer().getGroupByModel().getGroupByColumnIndexes()) {
				group.append(i + ";");
			}
			prefs.put(prefix + "groupby", group.toString());
		}
	}

	@LoadTableSelection
	public void restorePrefs(@Named("ConfigName") String name) {

		saveNattable();

		// Inhalt der Suchtabelle wiederherstellen
		if (this instanceof WFCSearchPart searchPart) {
			String string = prefs.get(form.getIndexView().getSource() + "." + name + ".table", null);
			if (string != null && !string.isBlank()) {
				Table prefTable = mjs.json2Table(string, true);
				getData().getRows().clear();
				getData().addRowsFromTable(prefTable);
				searchPart.updateUserInput();
			}
		}

		String prefix = getPersistPrefix(name, form);

		// Spaltenanordung und -breite
		restoreOrderAndSize(prefix);

		// Sichtbarkeit
		restoreHidden(prefix);

		// Sortierung
		restoreSorting(prefix);

		// Gruppierung
		restoreGroupBy(prefix);
	}

	protected void restoreGroupBy(String prefix) {
		String string = prefs.get(prefix + "groupby", null);
		if (string == null || string.isBlank()) {
			return;
		}

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

	protected void restoreSorting(String prefix) {
		String string = prefs.get(prefix + "sortby", null);
		if (string == null || string.isBlank()) {
			return;
		}

		String[] fields = string.split(";");
		sortHeaderLayer.getSortModel().clear();
		for (String s : fields) {
			String[] keyValue = s.split(",");
			int index = Integer.parseInt(keyValue[0].trim());
			SortDirectionEnum direction = SortDirectionEnum.valueOf(keyValue[1].trim());
			sortHeaderLayer.getSortModel().sort(index, direction, true);
		}

	}

	protected void restoreHidden(String prefix) {
		String string = prefs.get(prefix + "hidden", null);
		if (string == null || string.isBlank()) {
			return;
		}

		String replace = string.replace("[", "").replace("]", "");
		replace = replace.replace(", ", ",").trim();
		List<String> stringIndices = new ArrayList<>(Arrays.asList(replace.split(",")));

		for (int i = 0; i < data.getColumnCount(); i++) {
			boolean hidden = stringIndices.contains(i + "");
			data.getColumns().get(i).setVisible(!hidden);
		}
		updateColumns(""); // UI updaten
	}

	protected void restoreOrderAndSize(String prefix) {
		String string = prefs.get(prefix + "size", null);
		if (string == null || string.isBlank()) {
			return;
		}

		String[] fields = string.split(";");
		ArrayList<Integer> order = new ArrayList<>();
		for (String s : fields) {
			String[] keyValue = s.split(",");
			int position = Integer.parseInt(keyValue[0].trim());
			int width = Integer.parseInt(keyValue[1].trim());
			order.add(position);
			if (width < 0) {
				continue;
			}

			if (OSUtil.isMac()) {
				// Unter Mac werden die Spalten sonst kleiner beim Laden
				width += Math.round(width / 3.03);
			}
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

		bodyLayerStack.getColumnReorderLayer().resetReorder();
		int i = 0;
		for (int index : order) {
			bodyLayerStack.getColumnReorderLayer().reorderColumnPosition(bodyLayerStack.getColumnReorderLayer().getColumnIndexOrder().indexOf(index), i, true);
			i++;
		}
	}

	//////////////////////////////
	// Weitere Table Funktionalitäten
	//////////////////////////////

	/**
	 * Setzt die größe der Spalten aus dem sichtbaren Bereiches im Index-Bereich auf die Maximale Breite des Inhalts.
	 *
	 * @param mPart
	 */
	@Inject
	@Optional
	public void resize(@UIEventTopic(Constants.BROKER_RESIZETABLE) MPart mPart) {
		if (!mPart.equals(this.mPart)) {
			return;
		}
		natTable.commitAndCloseActiveCellEditor();
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

	@Inject
	@Optional
	public void updateColumns(@UIEventTopic(Constants.BROKER_UPDATECOLUMNS) String s) {
		bodyLayerStack.columnHideShowLayer.showAllColumns();
		List<Integer> positions = new ArrayList<>();
		for (Integer i : mcc.getHiddenColumns()) {
			positions.add(bodyLayerStack.columnHideShowLayer.getColumnPositionByIndex(i));
		}
		bodyLayerStack.columnHideShowLayer.hideColumnPositions(positions);
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

	//////////////////////////////
	// Getter ////////////////////
	//////////////////////////////

	public Table getData() {
		return data;
	}

	public SortedList<Row> getSortedList() {
		return bodyLayerStack.getSortedList();
	}

	public ColumnHeaderLayer getColumnHeaderLayer() {
		return columnHeaderLayer;
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

	public NatTable getNattable() {
		return natTable;
	}

	public SelectionLayer getSelectionLayer() {
		return bodyLayerStack.getSelectionLayer();
	}

	public BodyLayerStack getBodyLayerStack() {
		return this.bodyLayerStack;
	}

	public void refreshNatTable() {
		NatTableUtil.refresh(natTable);
	}

	public void saveNattable() {
		natTable.commitAndCloseActiveCellEditor();
	}

}
