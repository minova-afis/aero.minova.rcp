package aero.minova.rcp.rcp.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
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
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.ITraversalStrategy;
import org.eclipse.nebula.widgets.nattable.selection.MoveCellSelectionCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.selection.event.ISelectionEvent;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.summaryrow.FixedSummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellPainterMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.constants.GridChangeType;
import aero.minova.rcp.css.widgets.MinovaSection;
import aero.minova.rcp.css.widgets.MinovaSectionData;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.ImageUtil;
import aero.minova.rcp.form.model.xsd.Button;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.KeyType;
import aero.minova.rcp.model.ReferenceValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.event.GridChangeEvent;
import aero.minova.rcp.model.form.IButtonAccessor;
import aero.minova.rcp.model.form.IGridValidator;
import aero.minova.rcp.model.form.MButton;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.ButtonAccessor;
import aero.minova.rcp.rcp.accessor.DetailAccessor;
import aero.minova.rcp.rcp.accessor.GridAccessor;
import aero.minova.rcp.rcp.fields.FieldUtil;
import aero.minova.rcp.rcp.gridvalidation.CrossValidationConfiguration;
import aero.minova.rcp.rcp.gridvalidation.CrossValidationLabelAccumulator;
import aero.minova.rcp.rcp.nattable.MinovaGridConfiguration;
import aero.minova.rcp.rcp.nattable.TriStateCheckBoxPainter;
import aero.minova.rcp.rcp.util.CustomComparator;
import aero.minova.rcp.rcp.util.NattableSummaryUtil;
import aero.minova.rcp.rcp.util.StaticXBSValueUtil;
import aero.minova.rcp.util.OSUtil;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;

public class SectionGrid {

	IEclipsePreferences prefsDetailSections = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_DETAILSECTIONS);

	@Inject
	private TranslationService translationService;
	@Inject
	private IDataFormService dataFormService;
	@Inject
	private ECommandService commandService;
	@Inject
	private EHandlerService handlerService;
	@Inject
	private IDataService dataService;

	@Inject
	private Form form;

	@Inject
	private IEventBroker broker;
	@Inject
	Logger logger;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.GRID_TAB_NAVIGATION)
	boolean gridTabNavigation;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	public String timezone;

	private NatTable natTable;
	private Table dataTable;
	private Grid grid;
	private Composite composite;
	private MinovaSection section;
	private MDetail mDetail;

	private SortedList<Row> sortedList;
	private SelectionLayer selectionLayer;

	private LocalResourceManager resManager;

	private IButtonAccessor deleteToolItemAccessor;

	private GridAccessor gridAccessor;

	/**
	 * Zuordnung von Name in Grid zu Name in Detail, aus .xbs
	 */
	private Map<String, String> fieldnameToValue;

	private List<Row> rowsToInsert;
	private List<Row> rowsToUpdate;
	private List<Row> rowsToDelete;

	private Map<Integer, Boolean> originalReadOnlyColumns;
	private Map<Integer, Boolean> originalRequiredColumns;

	private int prevHeight;
	private static final int BUFFER = 31;
	private int defaultHeight;

	private ColumnReorderLayer columnReorderLayer;

	private DataLayer bodyDataLayer;

	private SortHeaderLayer sortHeaderLayer;

	private MinovaGridConfiguration gridConfiguration;

	private ViewportLayer viewportLayer;

	private GlazedListsEventLayer eventLayer;

	private MinovaColumnPropertyAccessor columnPropertyAccessor;

	private ColumnHeaderLayer columnHeaderLayer;

	public SectionGrid(Composite composite, MinovaSection section, Grid grid, MDetail mDetail) {
		this.section = section;
		this.grid = grid;
		this.composite = composite;
		this.mDetail = mDetail;
		resManager = new LocalResourceManager(JFaceResources.getResources(), composite);

		rowsToInsert = new ArrayList<>();
		rowsToUpdate = new ArrayList<>();
		rowsToDelete = new ArrayList<>();

		originalReadOnlyColumns = new HashMap<>();
		originalRequiredColumns = new HashMap<>();

		setFieldnameToValue(new HashMap<>());
	}

	public void createGrid() {
		createButton();
		dataTable = dataFormService.getTableFromGrid(grid);
		createNatTable();
		loadState();

		for (int i = 0; i < dataTable.getColumnCount(); i++) {
			originalReadOnlyColumns.put(i, dataTable.getColumns().get(i).isReadOnly());
			originalRequiredColumns.put(i, dataTable.getColumns().get(i).isRequired());
		}
	}

	/**
	 * Erstellt die Button für das Grid, abhängig von der Konfiguration des Grid-Knotens.
	 *
	 * @param grid
	 */
	private void createButton() {
		final ToolBar bar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT | SWT.NO_FOCUS);

		if (grid.isButtonInsertVisible()) {
			Button btnInsert = new Button();
			btnInsert.setId(Constants.CONTROL_GRID_BUTTON_INSERT);
			btnInsert.setIcon("NewRecord.Command");
			btnInsert.setText("@Action.New");
			btnInsert.setEnabled(true);
			createToolItem(bar, btnInsert, grid.getId() + "." + btnInsert.getId());
		}

		if (grid.isButtonDeleteVisible()) {
			Button btnDel = new Button();
			btnDel.setId(Constants.CONTROL_GRID_BUTTON_DELETE);
			btnDel.setIcon("DeleteRecord.Command");
			btnDel.setText("@Action.DeleteLine");
			btnDel.setEnabled(true);
			createToolItem(bar, btnDel, grid.getId() + "." + btnDel.getId());
		}

		// hier müssen die in der Maske definierten Buttons erstellt werden
		for (Button btn : grid.getButton()) {
			createToolItem(bar, btn, btn.getId());
		}

		// Standard
		Button btnOptimizeHigh = new Button();
		btnOptimizeHigh.setId(Constants.CONTROL_GRID_BUTTON_OPTIMIZEHEIGHT);
		btnOptimizeHigh.setIcon("ExpandSectionVertical.Command");
		btnOptimizeHigh.setText("@Action.Grid.OptimizeHeight");
		btnOptimizeHigh.setEnabled(true);
		createToolItem(bar, btnOptimizeHigh, grid.getId() + "." + btnOptimizeHigh.getId());

		Button btnOptimizeWidth = new Button();
		btnOptimizeWidth.setId(Constants.CONTROL_GRID_BUTTON_OPTIMIZEWIDTH);
		btnOptimizeWidth.setIcon("ExpandSectionHorizontal.Command");
		btnOptimizeWidth.setText("@Action.Grid.OptimizeWidth");
		btnOptimizeWidth.setEnabled(true);
		createToolItem(bar, btnOptimizeWidth, grid.getId() + "." + btnOptimizeWidth.getId());

		Button btnHorizontalFill = new Button();
		btnHorizontalFill.setId(Constants.CONTROL_GRID_BUTTON_HORIZONTALFILL);
		btnHorizontalFill.setIcon("ResizeLayout.Command");
		btnHorizontalFill.setText("@Action.Grid.HorizontalFill");
		btnHorizontalFill.setEnabled(true);
		createToolItem(bar, btnHorizontalFill, grid.getId() + "." + btnHorizontalFill.getId());

		section.setTextClient(bar);
	}

	public ToolItem createToolItem(ToolBar bar, Button btn, String buttonID) {
		return createToolItem(bar, btn, Constants.AERO_MINOVA_RCP_RCP_COMMAND_GRIDBUTTONCOMMAND, buttonID);
	}

	public ToolItem createToolItem(ToolBar bar, Button btn, String commandName, String buttonID) {
		final ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setData(btn);
		item.setData("org.eclipse.swtbot.widget.key", btn.getId());
		item.setEnabled(btn.isEnabled());
		if (btn.getText() != null) {
			item.setToolTipText(translationService.translate(btn.getText(), null));
			item.setData(FieldUtil.TRANSLATE_PROPERTY, btn.getText());
		}

		MButton mButton = new MButton(buttonID);
		mButton.setIcon(btn.getIcon());
		mButton.setText(btn.getText());
		ButtonAccessor bA = new ButtonAccessor(item);
		mButton.setButtonAccessor(bA);
		mDetail.putButton(mButton);

		if (btn.getId().equals(Constants.CONTROL_GRID_BUTTON_DELETE)) {
			deleteToolItemAccessor = bA;
		}

		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO: Andere procedures/bindings/instances auswerten
				Map<String, String> parameter = new HashMap<>();
				parameter.put(Constants.CONTROL_GRID_BUTTON_ID, btn.getId());
				parameter.put(Constants.CONTROL_GRID_ID, grid.getId());
				ParameterizedCommand command = commandService.createCommand(commandName, parameter);
				handlerService.executeHandler(command);
			}
		});

		if (btn.getIcon() != null && btn.getIcon().trim().length() > 0) {
			final ImageDescriptor buttonImageDescriptor = ImageUtil.getImageDescriptor(btn.getIcon(), false);
			Image buttonImage = resManager.createImage(buttonImageDescriptor);
			item.setImage(buttonImage);
		}

		return item;
	}

	public NatTable createNatTable() {

		// Datenmodel für die Eingaben
		ConfigRegistry configRegistry = new ConfigRegistry();

		// create the body stack
		EventList<Row> eventList = GlazedLists.eventList(dataTable.getRows());
		sortedList = new SortedList<>(eventList, null);
		columnPropertyAccessor = new MinovaColumnPropertyAccessor(dataTable, grid);
		columnPropertyAccessor.initPropertyNames(translationService);

		IDataProvider bodyDataProvider = new ListDataProvider<>(sortedList, columnPropertyAccessor);
		bodyDataLayer = new DataLayer(bodyDataProvider);
		bodyDataLayer.setConfigLabelAccumulator(new ColumnLabelAccumulator());

		bodyDataLayer.unregisterCommandHandler(UpdateDataCommand.class);
		bodyDataLayer.registerCommandHandler(new UpdateDataCommandHandler(bodyDataLayer) {
			@Override
			protected boolean doCommand(UpdateDataCommand command) {
				Row r = sortedList.get(command.getRowPosition());
				int col = command.getColumnPosition();
				int row = dataTable.getRows().indexOf(r);

				Value oldVal = dataTable.getRows().get(row).getValue(col);

				if (super.doCommand(command)) {
					Value newVal = dataTable.getRows().get(row).getValue(col);
					fireChange(new GridChangeEvent(gridAccessor.getMGrid(), dataTable.getRows().get(row), col, row, oldVal, newVal, true));

					if (!rowsToUpdate.contains(r) && !rowsToInsert.contains(r)) {
						rowsToUpdate.add(r);
					}
					natTable.refresh(false); // Damit Summary-Row updated
					return true;
				}
				return false;
			}
		});

		eventLayer = new GlazedListsEventLayer<>(bodyDataLayer, sortedList);

		columnReorderLayer = new ColumnReorderLayer(eventLayer);
		ColumnHideShowLayer columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
		selectionLayer = new SelectionLayer(columnHideShowLayer);

		// Delete Button updaten (nur aktiviert, wenn eine Zelle gewählt ist)
		selectionLayer.addLayerListener(event -> {
			if (deleteToolItemAccessor != null && event instanceof ISelectionEvent) {
				deleteToolItemAccessor.setCanBeEnabled(selectionLayer.getSelectedCellPositions().length > 0);
				deleteToolItemAccessor.updateEnabled();
			}
		});

		viewportLayer = new ViewportLayer(selectionLayer);
		viewportLayer.setRegionName(GridRegion.BODY);
		viewportLayer.registerCommandHandler(new MoveCellSelectionCommandHandler(selectionLayer, ITraversalStrategy.TABLE_TRAVERSAL_STRATEGY));

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(columnPropertyAccessor.getPropertyNames(),
				columnPropertyAccessor.getTableHeadersMap());
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);
		sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
				new GlazedListsSortModel<>(sortedList, columnPropertyAccessor, configRegistry, columnHeaderDataLayer), false);
		// Eigenen Sort-Comparator auf alle Spalten registrieren (Verhindert Fehler bei Datumsspalten)
		ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
		columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);
		for (int i = 0; i < columnHeaderDataLayer.getColumnCount(); i++) {
			labelAccumulator.registerColumnOverrides(i, Constants.COMPARATOR_LABEL);
		}
		configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, new CustomComparator(), DisplayMode.NORMAL, Constants.COMPARATOR_LABEL);

		// build the row header layer
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
		DataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);

		ILayer bodyLayer;
		ILayer rowHeaderLayer;
		if (NattableSummaryUtil.needsSummary(grid)) {
			// build the Summary Row
			FixedSummaryRowLayer summaryRowLayer = new FixedSummaryRowLayer(eventLayer, viewportLayer, configRegistry, false);
			summaryRowLayer.setHorizontalCompositeDependency(false);
			CompositeLayer summaryComposite = new CompositeLayer(1, 2);
			summaryComposite.setChildLayer("SUMMARY", summaryRowLayer, 0, 0);
			summaryComposite.setChildLayer(GridRegion.BODY, viewportLayer, 0, 1);
			bodyLayer = summaryComposite;

			rowHeaderLayer = new FixedSummaryRowHeaderLayer(rowHeaderDataLayer, summaryComposite, selectionLayer);
			((FixedSummaryRowHeaderLayer) rowHeaderLayer).setSummaryRowLabel("");
		} else {
			rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);
			bodyLayer = viewportLayer;
		}

		// build the corner layer
		IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		ILayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

		// build the grid layer
		GridLayer gridLayer = new GridLayer(bodyLayer, sortHeaderLayer, rowHeaderLayer, cornerLayer);

		setNatTable(new NatTable(composite, gridLayer, false));

		// as the autoconfiguration of the NatTable is turned off, we have to add the DefaultNatTableStyleConfiguration and the ConfigRegistry manually
		getNatTable().setConfigRegistry(configRegistry);
		getNatTable().addConfiguration(new DefaultNatTableStyleConfiguration());
		getNatTable().addConfiguration(new SingleClickSortConfiguration());
		gridConfiguration = new MinovaGridConfiguration(dataTable.getColumns(), grid, dataService, translationService);
		getNatTable().addConfiguration(gridConfiguration);
		columnHideShowLayer.hideColumnPositions(gridConfiguration.getHiddenColumns());

		// Hinzufügen von BindingActions, damit in der TriStateCheckBoxPainter der Mouselistener anschlägt!
		getNatTable().addConfiguration(new DefaultEditBindings() {
			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				MouseEditAction mouseEditAction = new MouseEditAction();
				super.configureUiBindings(uiBindingRegistry);
				uiBindingRegistry.registerFirstSingleClickBinding(
						new CellPainterMouseEventMatcher(GridRegion.BODY, MouseEventMatcher.LEFT_BUTTON, TriStateCheckBoxPainter.class), mouseEditAction);
			}
		});

		getNatTable().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (((NatTable) e.getSource()).getChildren().length <= 0) {
					((DetailAccessor) mDetail.getDetailAccessor()).setSelectedControl(null);
					selectionLayer.clear();
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (selectionLayer.getSelectedCells().isEmpty() && getNatTable().getActiveCellEditor() == null
						&& ((DetailAccessor) mDetail.getDetailAccessor()).getSelectedControl() != getNatTable()) {
					getNatTable()
							.doCommand(new SelectCellCommand(selectionLayer, selectionLayer.getColumnPositionByIndex(viewportLayer.getColumnIndexByPosition(0)),
									selectionLayer.getRowPositionByIndex(viewportLayer.getRowIndexByPosition(0)), false, false));
					((DetailAccessor) mDetail.getDetailAccessor()).setSelectedControl(getNatTable());
				}
			}
		});

		getNatTable().addTraverseListener(e -> {

			switch (e.detail) {
			case SWT.TRAVERSE_TAB_NEXT:
				if ((selectionLayer.getSelectionAnchor().columnPosition == selectionLayer.getColumnCount() - 1
						&& selectionLayer.getSelectionAnchor().rowPosition == selectionLayer.getRowCount() - 1) || !gridTabNavigation || sortedList.isEmpty()) {
					selectionLayer.clear();
					e.doit = true;
				}
				break;
			case SWT.TRAVERSE_TAB_PREVIOUS:
				if ((selectionLayer.getSelectionAnchor().columnPosition == 0 && selectionLayer.getSelectionAnchor().rowPosition == 0) || !gridTabNavigation
						|| sortedList.isEmpty()) {
					selectionLayer.clear();
					e.doit = true;
				}
				break;
			default:
				break;
			}
		});

		if (NattableSummaryUtil.needsSummary(grid)) {
			NattableSummaryUtil.configureSummary(grid, natTable, sortedList, columnPropertyAccessor);
		}

		FormData fd = new FormData();

		// Nattable immer über die gesamte Breite des Parent-Composites zeichnen
		fd.left = new FormAttachment(0);
		fd.right = new FormAttachment(100);

		// Höhe wiederherstellen
		defaultHeight = natTable.getRowHeightByPosition(0) * 5;
		String prefsHeightKey = form.getTitle() + "." + section.getData(FieldUtil.TRANSLATE_PROPERTY) + ".height";
		String heightString = prefsDetailSections.get(prefsHeightKey, null);
		fd.height = heightString != null ? Integer.parseInt(heightString) : defaultHeight;
		prevHeight = fd.height;

		getNatTable().setLayoutData(fd);

		getNatTable().configure();
		getNatTable().getUiBindingRegistry().registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, 'n'), (nt, event) -> {
			String commandName = Constants.AERO_MINOVA_RCP_RCP_COMMAND_GRIDBUTTONCOMMAND;
			execButtonHandler(Constants.CONTROL_GRID_BUTTON_INSERT, commandName);
		});
		getNatTable().getUiBindingRegistry().registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, 'd'), (nt, event) -> {
			String commandName = Constants.AERO_MINOVA_RCP_RCP_COMMAND_GRIDBUTTONCOMMAND;
			execButtonHandler(Constants.CONTROL_GRID_BUTTON_DELETE, commandName);
		});
		getNatTable().getUiBindingRegistry().registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, 'h'), (nt, event) -> {
			String commandName = Constants.AERO_MINOVA_RCP_RCP_COMMAND_GRIDBUTTONCOMMAND;
			execButtonHandler(Constants.CONTROL_GRID_BUTTON_OPTIMIZEWIDTH, commandName);
		});
		getNatTable().getUiBindingRegistry().registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, 'v'), (nt, event) -> {
			String commandName = Constants.AERO_MINOVA_RCP_RCP_COMMAND_GRIDBUTTONCOMMAND;
			execButtonHandler(Constants.CONTROL_GRID_BUTTON_OPTIMIZEHEIGHT, commandName);
		});
		getNatTable().getUiBindingRegistry().registerKeyBinding(new KeyEventMatcher(SWT.CR), (matchedTable, event) -> {
			Map<String, String> parameter = new HashMap<>();
			ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.traverseenter", parameter);
			handlerService.executeHandler(command);
		});

		getNatTable().setData(Constants.GRID_DATA_SECTION, section);
		getNatTable().setData(Constants.GRID_DATA_SELECTIONLAYER, selectionLayer);
		getNatTable().setData(Constants.GRID_DATA_DATATABLE, dataTable);
		getNatTable().setData("EHandlerService", handlerService);
		getNatTable().setData("ECommandService", commandService);
		return getNatTable();
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

		if (gridConfiguration != null) {
			gridConfiguration.translateLookups();
		}
	}

	public void execButtonHandler(String btnId, String commandName) {
		Map<String, String> parameter = new HashMap<>();
		parameter.put(Constants.CONTROL_GRID_BUTTON_ID, btnId);
		parameter.put(Constants.CONTROL_GRID_ID, grid.getId());
		ParameterizedCommand command = commandService.createCommand(commandName, parameter);
		handlerService.executeHandler(command);
	}

	public Table getDataTable() {
		return dataTable;
	}

	public Table setDataTable(Table newDataTable) {
		this.dataTable.getRows().clear();
		addRowsFromTable(newDataTable);
		fireChange(new GridChangeEvent(gridAccessor.getMGrid(), false));
		return dataTable.copy();
	}

	public void addRows(Table t) {

		List<Row> originalRows = new ArrayList<>();
		originalRows.addAll(dataTable.getRows());

		addRowsFromTable(t);

		for (Row r : dataTable.getRows()) {
			if (!originalRows.contains(r)) {
				rowsToInsert.add(r);
				fireChange(new GridChangeEvent(gridAccessor.getMGrid(), r, dataTable.getRows().indexOf(r), false, GridChangeType.INSERT));
			}
		}

		// Wenn eine Zeile mit gleichen Keys wieder hinzugefügt wird soll sie nicht gelöscht werden
		rowsToDelete = rowsToDelete.stream().filter(row -> !dataTable.containsRowByKeys(row)).collect(Collectors.toList());
	}

	private void addRowsFromTable(Table rowsToAdd) {
		this.dataTable.addRowsFromTable(rowsToAdd);
		updateNatTable();
	}

	public NatTable getNatTable() {
		return natTable;
	}

	public List<Row> getRowsToInsert() {
		return rowsToInsert;
	}

	public List<Row> getRowsToUpdate() {
		return rowsToUpdate;
	}

	public List<Row> getRowsToDelete() {
		return rowsToDelete;
	}

	public void clearDataChanges() {
		rowsToInsert.clear();
		rowsToUpdate.clear();
		rowsToDelete.clear();
	}

	public void setNatTable(NatTable natTable) {
		this.natTable = natTable;
	}

	public int getNatTableHigh() {
		return natTable.getPreferredHeight();
	}

	public void updateNatTable() {
		sortedList.clear();
		sortedList.addAll(dataTable.getRows());
		natTable.refresh(false); // Damit Summary-Row richtig aktualisiert wird
	}

	public int getSectionHigh() {
		return this.section.getBounds().height;
	}

	public void adjustHeight() {
		FormData fd = (FormData) natTable.getLayoutData();

		// Maximal 15 Zeilen anzeigen
		int optimalHeight = Math.min(natTable.getRowHeightByPosition(0) * 16, natTable.getPreferredHeight());
		// Minimal 2 Zeilen anzeigen
		optimalHeight = Math.max(natTable.getRowHeightByPosition(0) * 3, optimalHeight);

		if (optimalHeight == prevHeight) {
			optimalHeight = defaultHeight;
		}

		prevHeight = optimalHeight;
		fd.height = optimalHeight;
		natTable.requestLayout();

		// Height Speicher, damit beim Neuladen wieder hergestellt wird
		String key = form.getTitle() + "." + section.getData(FieldUtil.TRANSLATE_PROPERTY) + ".height";
		prefsDetailSections.put(key, fd.height + "");
		try {
			prefsDetailSections.flush();
		} catch (BackingStoreException e) {
			logger.error(e);
		}
	}

	public void fillHorizontal() {
		MinovaSectionData rd = (MinovaSectionData) section.getLayoutData();
		rd.setHorizontalFill(!rd.isHorizontalFill());
		section.requestLayout();

		// Zustand speichern, damit wiederhergestellt werden kann
		String key = form.getTitle() + "." + section.getData(FieldUtil.TRANSLATE_PROPERTY) + ".horizontalFill";
		prefsDetailSections.put(key, rd.isHorizontalFill() + "");
	}

	public Row addNewRow() {
		Row newRow = dataTable.addRow();
		setValuesAccordingToXBS(newRow);
		rowsToInsert.add(newRow);
		fireChange(new GridChangeEvent(gridAccessor.getMGrid(), newRow, dataTable.getRows().size() - 1, true, GridChangeType.INSERT));
		updateNatTable();
		return newRow;
	}

	public Map<String, String> getFieldnameToValue() {
		return fieldnameToValue;
	}

	public void setFieldnameToValue(Map<String, String> fieldnameToValue) {
		this.fieldnameToValue = fieldnameToValue;
	}

	public void setPrimaryKeys(Map<String, Value> primaryKeys) {

		for (Row r : dataTable.getRows()) {

			if (!getFieldnameToValue().isEmpty()) { // Zuordnung aus .xbs nutzen

				for (Field f : grid.getField()) {
					if (getFieldnameToValue().containsKey(f.getName())) {
						setReferenceOrMainValue(r, primaryKeys, getFieldnameToValue().get(f.getName()), grid.getField().indexOf(f));
					}
				}

			} else { // Default: Name stimmt überein oder erstes Primary-Feld bekommt Wert von KeyLong in Hauptmaske
				boolean firstPrimary = true;
				for (Field f : grid.getField()) {
					if (KeyType.PRIMARY.toString().equalsIgnoreCase(f.getKeyType())) {
						int index = grid.getField().indexOf(f);

						if (primaryKeys != null && primaryKeys.containsKey(f.getName())) { // Übereinstimmende Namen nutzen
							setReferenceOrMainValue(r, primaryKeys, f.getName(), index);
						} else if (firstPrimary) { // Default: erstes Primary-Feld bekommt Wert von KeyLong
							setReferenceOrMainValue(r, primaryKeys, Constants.TABLE_KEYLONG, index);
						}
						firstPrimary = false;
					}
				}
			}
		}
	}

	private void setReferenceOrMainValue(Row r, Map<String, Value> primaryKeys, String mainFieldName, int indexInRow) {
		if (mainFieldName.startsWith(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL)) {
			// Statischer Wert, Keys müssen nicht geändert werden
		} else if (primaryKeys == null) { // Bei Insert ReferenceValue
			ReferenceValue v = new ReferenceValue(Constants.TRANSACTION_PARENT, mainFieldName);
			r.setValue(v, indexInRow);
		} else { // Bei Update Wert aus der Hauptmaske
			Value v = mDetail.getField(mainFieldName).getValue();
			r.setValue(v, indexInRow);
		}
	}

	/**
	 * Setzt die Values, wie sie in der XBS gegeben sind. Entweder einen statischen Wert, oder den Wert eines Feldes aus der Hauptmaske.
	 * 
	 * @param r
	 */
	private void setValuesAccordingToXBS(Row r) {
		for (Entry<String, String> e : getFieldnameToValue().entrySet()) {
			int indexInRow = dataTable.getColumnIndex(e.getKey());
			if (e.getValue().startsWith(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL)) {
				Column c = dataTable.getColumns().get(indexInRow);
				try {
					Value v = StaticXBSValueUtil.stringToValue(e.getValue().substring(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL.length()), c.getType(),
							c.getDateTimeType(), translationService, timezone);
					r.setValue(v, indexInRow);
				} catch (Exception exception) {
					NoSuchFieldException error = new NoSuchFieldException(
							"String \"" + e.getValue().substring(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL.length()) + "\" can't be parsed to Type \""
									+ c.getType() + "\" of Column \"" + c.getName() + "\"! (As defined in .xbs)");
					logger.error(error);
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", error.getMessage());
				}
			} else {
				Value v = mDetail.getField(e.getValue()).getValue();
				r.setValue(v, indexInRow);
			}
		}
	}

	public void clearGrid() {
		dataTable.getRows().clear();
		updateNatTable();
		clearDataChanges();
		fireChange(new GridChangeEvent(gridAccessor.getMGrid(), false));
	}

	public void deleteCurrentRows() {
		closeEditor();
		for (Range r : selectionLayer.getSelectedRowPositions()) {
			for (int i = r.start; i < r.end; i++) {
				dataTable.deleteRow(sortedList.get(i));
				rowsToDelete.add(sortedList.get(i));
				rowsToUpdate.remove(sortedList.get(i));
				rowsToInsert.remove(sortedList.get(i));
				fireChange(new GridChangeEvent(gridAccessor.getMGrid(), sortedList.get(i), i, true, GridChangeType.DELETE));
			}
		}
		updateNatTable();
	}

	private void fireChange(GridChangeEvent event) {
		gridAccessor.getMGrid().dataTableChanged(event);
	}

	public void setGridAccessor(GridAccessor gridAccessor) {
		this.gridAccessor = gridAccessor;
	}

	public void closeEditor() {
		natTable.commitAndCloseActiveCellEditor();
	}

	public void saveState() {

		String key = form.getTitle() + "." + section.getData(FieldUtil.TRANSLATE_PROPERTY);

		// Spaltenanordung und -breite
		String size = "";
		for (int i : columnReorderLayer.getColumnIndexOrder()) {
			size += i + "," + bodyDataLayer.getColumnWidthByPosition(i) + ";";

		}
		prefsDetailSections.put(key + ".size", size);

		// Sortierung
		StringBuilder sort = new StringBuilder();
		for (int i : sortHeaderLayer.getSortModel().getSortedColumnIndexes()) {
			sort.append(i + "," + sortHeaderLayer.getSortModel().getSortDirection(i) + ";");
		}
		prefsDetailSections.put(key + ".sortby", sort.toString());

		try {
			prefsDetailSections.flush();
		} catch (BackingStoreException e) {
			logger.error(e);
		}

	}

	public void loadState() {

		String key = form.getTitle() + "." + section.getData(FieldUtil.TRANSLATE_PROPERTY);

		// Spaltenanordung und Breite
		String string = prefsDetailSections.get(key + ".size", null);
		if (string != null && !string.equals("")) {
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
				bodyDataLayer.setColumnWidthByPosition(position, width);
			}
			// Änderungen in der Maske beachten (neue Spalten, Spalten gelöscht)
			if (columnReorderLayer.getColumnIndexOrder().size() < order.size()) {
				ArrayList<Integer> toDelete = new ArrayList<>();
				for (int i : order) {
					if (!columnReorderLayer.getColumnIndexOrder().contains(i)) {
						toDelete.add(i);
					}
				}
				order.removeAll(toDelete);
			}

			columnReorderLayer.resetReorder();
			int i = 0;
			for (int index : order) {
				columnReorderLayer.reorderColumnPosition(columnReorderLayer.getColumnIndexOrder().indexOf(index), i, true);
				i++;
			}
		}

		// Sortierung
		string = prefsDetailSections.get(key + ".sortby", null);
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

	}

	public Table getSelectedRows() {
		TableBuilder tb = TableBuilder.newTable("");
		for (Column c : dataTable.getColumns()) {
			tb.withColumn(c.getName(), c.getType());
		}
		Table selected = tb.create();

		for (Range r : selectionLayer.getSelectedRowPositions()) {
			for (int i = r.start; i < r.end; i++) {
				selected.addRow(sortedList.get(i));
			}
		}
		return selected;
	}

	public void addSelectionListener(ILayerListener listener) {
		selectionLayer.addLayerListener(listener);
	}

	public void removeSelectionListener(ILayerListener listener) {
		selectionLayer.removeLayerListener(listener);
	}

	public void resetReadOnlyAndRequiredColumns() {
		for (Entry<Integer, Boolean> e : originalReadOnlyColumns.entrySet()) {
			setColumnReadOnly(e.getKey(), e.getValue());
		}

		for (Entry<Integer, Boolean> e : originalRequiredColumns.entrySet()) {
			setColumnRequired(e.getKey(), e.getValue());
		}
	}

	public void setColumnRequired(int columnIndex, boolean required) {
		dataTable.getColumns().get(columnIndex).setRequired(required);
		gridConfiguration.setColumnRequired(columnIndex, required);

		broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, Constants.SAVE_DETAIL_BUTTON);
	}

	public void setGridRequired(boolean required) {
		for (int i = 0; i < dataTable.getColumnCount(); i++) {
			if (!gridConfiguration.getHiddenColumns().contains(i)) {
				setColumnRequired(i, required);
			}
		}
	}

	public void setColumnReadOnly(int columnIndex, boolean readOnly) {
		dataTable.getColumns().get(columnIndex).setReadOnly(readOnly);
		gridConfiguration.setColumnReadOnly(columnIndex, readOnly);
	}

	public void setGridReadOnly(boolean readOnly) {
		for (int i = 0; i < dataTable.getColumnCount(); i++) {
			if (!gridConfiguration.getHiddenColumns().contains(i)) {
				setColumnReadOnly(i, readOnly);
			}
		}
	}

	public void addValidation(IGridValidator validator, List<Integer> columnsToValidate) {
		// Für Rot-Zeichnen der Zellen
		this.bodyDataLayer.setConfigLabelAccumulator(new CrossValidationLabelAccumulator(bodyDataLayer, validator, columnsToValidate, sortedList, dataTable));

		// Anzeigen der Fehlermeldung
		this.natTable.addConfiguration(new CrossValidationConfiguration(validator, broker));
		this.natTable.configure();
	}

	public void updateGridLookupValues() {
		gridConfiguration.updateContentProvider();
	}

}
