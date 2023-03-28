package aero.minova.rcp.rcp.widgets;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
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
import org.eclipse.nebula.widgets.nattable.painter.layer.GridLineCellLayerPainter;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.action.SortColumnAction;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.sort.event.ColumnHeaderClickEventMatcher;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.summaryrow.FixedSummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.tree.config.TreeLayerExpandCollapseKeyBindings;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.rcp.nattable.MinovaIndexConfiguration;
import aero.minova.rcp.rcp.util.CustomComparator;
import aero.minova.rcp.rcp.util.NattableSummaryUtil;
import aero.minova.rcp.util.OSUtil;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;

public class MinovaWizardIndexUtil {

	@Inject
	TranslationService translationService;

	@Inject
	IDataService dataService;

	@Inject
	protected IDataFormService dataFormService;

	private String formName;

	private Table table;

	private BodyLayerStack<Row> bodyLayerStack;

	/**
	 * Kann genutzt werden, um in einer Wizard-Page eine Tabelle aufzubauen. <br>
	 * Anwendungsbeispiel: <br>
	 * 
	 * <pre>
	 * MinovaWizardIndexUtil indexUtil = new MinovaWizardIndexUtil("AutoInvoiceContracts.xml");
	 * ContextInjectionFactory.inject(indexUtil, context); // Wichig, damit Übersetzungen, ... funktionieren
	 * NatTable natTable = indexUtil.createNattable(container);
	 * </pre>
	 * 
	 * @param formName
	 */
	public MinovaWizardIndexUtil(String formName) {
		this.formName = formName;
	}

	public NatTable createNattable(Composite parent) {

		NatTable natTable;

		Form form = dataFormService.getForm(formName);

		table = dataFormService.getTableFromFormIndex(form);

		// Datenmodel für die Eingaben
		ConfigRegistry configRegistry = new ConfigRegistry();
		MinovaColumnPropertyAccessor columnPropertyAccessor = new MinovaColumnPropertyAccessor(getTable(), form);
		columnPropertyAccessor.initPropertyNames(translationService);

		// create the body stack
		bodyLayerStack = new BodyLayerStack<>(getTable().getRows(), columnPropertyAccessor, configRegistry);
		bodyLayerStack.getBodyDataLayer().setConfigLabelAccumulator(new ColumnLabelAccumulator());

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(columnPropertyAccessor.getPropertyNames(),
				columnPropertyAccessor.getTableHeadersMap());
		DefaultColumnHeaderDataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayerStack, bodyLayerStack.getSelectionLayer());

		SortHeaderLayer<Row> sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
				new GlazedListsSortModel<>(bodyLayerStack.getSortedList(), columnPropertyAccessor, configRegistry, columnHeaderDataLayer), false);
		// Eigenen Sort-Comparator auf alle Spalten registrieren (Verhindert Fehler bei Zeilen, die keinen von- oder bis-Wert haben)
		ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
		columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);
		for (int i = 0; i < columnHeaderDataLayer.getColumnCount(); i++) {
			labelAccumulator.registerColumnOverrides(i, Constants.COMPARATOR_LABEL);
		}
		configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, new CustomComparator(), DisplayMode.NORMAL, Constants.COMPARATOR_LABEL);

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
		selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());

		CopyDataCommandHandler copyHandler = new CopyDataCommandHandler(selectionLayer, columnHeaderDataLayer, rowHeaderDataLayer);
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
				if (OSUtil.isLinux()) {
					keyMask |= SWT.MOD2;
				}
				uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(keyMask), new SortColumnAction(true));
			}
		});

		MinovaIndexConfiguration mic = new MinovaIndexConfiguration(getTable().getColumns(), form);
		natTable.addConfiguration(mic);
		bodyLayerStack.columnHideShowLayer.hideColumnPositions(mic.getHiddenColumns());

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
				}, (natTable1, event) -> {
					int groupByColumnIndex = groupByHeaderLayer.getGroupByColumnIndexAtXY(event.x, event.y);
					natTable1.doCommand(new UngroupByColumnIndexCommand(groupByColumnIndex));
				});
			}
		});

		NattableSummaryUtil.configureSummary(form, natTable, bodyLayerStack.getSortedList(), columnPropertyAccessor);

		natTable.configure();
		natTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		return natTable;

	}

	public Table getTable() {
		return table;
	}

	public SortedList<Row> getSortedList() {
		return bodyLayerStack.getSortedList();
	}

	public class BodyLayerStack<T> extends AbstractLayerTransform {

		private final SortedList<T> sortedList;

		private final IDataProvider bodyDataProvider;

		private final SelectionLayer selectionLayer;

		private final GroupByModel groupByModel = new GroupByModel();

		private EventList<T> eventList;

		private GroupByDataLayer<T> bodyDataLayer;

		private TreeLayer treeLayer;

		private GlazedListsEventLayer<T> glazedListsEventLayer;

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
			this.bodyDataProvider = bodyDataLayer.getDataProvider();

			// layer for event handling of GlazedLists and PropertyChanges
			glazedListsEventLayer = new GlazedListsEventLayer<>(bodyDataLayer, this.sortedList);

			columnReorderLayer = new ColumnReorderLayer(glazedListsEventLayer);
			columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
			this.selectionLayer = new SelectionLayer(getColumnHideShowLayer());

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

		public IDataProvider getBodyDataProvider() {
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

}
