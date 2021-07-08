package aero.minova.rcp.rcp.widgets;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
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
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellPainterMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Button;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.model.KeyType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.rcp.accessor.GridAccessor;
import aero.minova.rcp.rcp.nattable.MinovaGridConfiguration;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.util.ImageUtil;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;

public class SectionGrid {

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
	private MPerspective perspective;
	@Inject
	private Form form;

	private NatTable natTable;
	private Table dataTable;
	private Grid grid;
	private Composite composite;
	private Section section;

	private SortedList<Row> sortedList;
	private SelectionLayer selectionLayer;
	private ColumnHideShowLayer columnHideShowLayer;

	private LocalResourceManager resManager;

	private ToolItem deleteToolItem;
	private ToolItem insertToolItem;

	private GridAccessor gridAccessor;

	private List<Row> rowsToInsert;
	private List<Row> rowsToUpdate;
	private List<Row> rowsToDelete;

	private static final int BUFFER = 31;
	private static final int DEFAULT_WIDTH = WFCDetailPart.SECTION_WIDTH - BUFFER;
	private static final int DEFAULT_HEIGHT = COLUMN_HEIGHT * 3;

	public SectionGrid(Composite composite, Section section, Grid grid) {
		this.section = section;
		this.grid = grid;
		this.composite = composite;
		resManager = new LocalResourceManager(JFaceResources.getResources(), composite);

		rowsToInsert = new ArrayList<>();
		rowsToUpdate = new ArrayList<>();
		rowsToDelete = new ArrayList<>();
	}

	public void createGrid() {
		createButton();
		dataTable = dataFormService.getTableFromGrid(grid);
		createNatTable();
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
			btnInsert.setText(translationService.translate("@Action.New", null));
			btnInsert.setEnabled(false);
			insertToolItem = createButton(bar, btnInsert);
		}

		if (grid.isButtonDeleteVisible()) {
			Button btnDel = new Button();
			btnDel.setId(Constants.CONTROL_GRID_BUTTON_DELETE);
			btnDel.setIcon("DeleteRecord.Command");
			btnDel.setText(translationService.translate("@Action.DeleteLine", null));
			btnDel.setEnabled(false);
			deleteToolItem = createButton(bar, btnDel);
		}

		// hier müssen die in der Maske definierten Buttons erstellt werden
		for (Button btn : grid.getButton()) {
			createButton(bar, btn);
		}

		// Standard
		Button btnOptimizeHigh = new Button();
		btnOptimizeHigh.setId(Constants.CONTROL_GRID_BUTTON_OPTIMIZEHEIGHT);
		btnOptimizeHigh.setIcon("ExpandSectionVertical.Command");
		btnOptimizeHigh.setText(translationService.translate("@Action.OptimizeHeight", null));
		btnOptimizeHigh.setEnabled(true);
		createButton(bar, btnOptimizeHigh);

		Button btnOptimizeWidth = new Button();
		btnOptimizeWidth.setId(Constants.CONTROL_GRID_BUTTON_OPTIMIZEWIDTH);
		btnOptimizeWidth.setIcon("ExpandSectionHorizontal.Command");
		btnOptimizeWidth.setText(translationService.translate("@Action.OptimizeWidth", null));
		btnOptimizeWidth.setEnabled(true);
		createButton(bar, btnOptimizeWidth);

		section.setTextClient(bar);
	}

	public ToolItem createButton(ToolBar bar, Button btn) {
		return createButton(bar, btn, "aero.minova.rcp.rcp.command.gridbuttoncommand");
	}

	public ToolItem createButton(ToolBar bar, Button btn, String commandName) {
		final ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setData(btn);
		item.setEnabled(btn.isEnabled());
		if (btn.getText() != null) {
			item.setToolTipText(translationService.translate(btn.getText(), null));
		}

		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO: Andere procedures/bindings/instances auswerten
				Map<String, String> parameter = new HashMap<>();
				parameter.put(Constants.CONTROL_GRID_BUTTON_ID, btn.getId());
				parameter.put(Constants.CONTROL_GRID_PROCEDURE_SUFFIX, grid.getProcedureSuffix());
				ParameterizedCommand command = commandService.createCommand(commandName, parameter);
				handlerService.executeHandler(command);
			}
		});

		if (btn.getIcon() != null && btn.getIcon().trim().length() > 0) {
			final ImageDescriptor buttonImageDescriptor = ImageUtil.getImageDescriptorFromImagesBundle(btn.getIcon());
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
		MinovaColumnPropertyAccessor columnPropertyAccessor = new MinovaColumnPropertyAccessor(dataTable, grid);
		columnPropertyAccessor.initPropertyNames(translationService);

		IDataProvider bodyDataProvider = new ListDataProvider<>(sortedList, columnPropertyAccessor);
		DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
		bodyDataLayer.setConfigLabelAccumulator(new ColumnLabelAccumulator());

		bodyDataLayer.unregisterCommandHandler(UpdateDataCommand.class);
		bodyDataLayer.registerCommandHandler(new UpdateDataCommandHandler(bodyDataLayer) {
			@Override
			protected boolean doCommand(UpdateDataCommand command) {
				if (super.doCommand(command)) {
					gridAccessor.getMGrid().dataTableChanged();
					Row r = sortedList.get(command.getRowPosition());
					if (!rowsToUpdate.contains(r) && !rowsToInsert.contains(r)) {
						rowsToUpdate.add(r);
					}
					return true;
				}
				return false;
			}
		});

		GlazedListsEventLayer<Row> eventLayer = new GlazedListsEventLayer<>(bodyDataLayer, sortedList);

		ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(eventLayer);
		columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
		selectionLayer = new SelectionLayer(columnHideShowLayer);

		// Delete Button updaten (nur aktiviert, wenn eine ganze Zeile gewählt ist)
		selectionLayer.addLayerListener(event -> deleteToolItem.setEnabled(selectionLayer.getFullySelectedRowPositions().length > 0));

		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
		viewportLayer.setRegionName(GridRegion.BODY);

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(columnPropertyAccessor.getPropertyNames(),
				columnPropertyAccessor.getTableHeadersMap());
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);

		// build the row header layer
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
		DataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);

		// build the corner layer
		IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		ILayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

		// build the grid layer
		GridLayer gridLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		setNatTable(new NatTable(composite, gridLayer, false));

		// as the autoconfiguration of the NatTable is turned off, we have to add the DefaultNatTableStyleConfiguration and the ConfigRegistry manually
		getNatTable().setConfigRegistry(configRegistry);
		getNatTable().addConfiguration(new DefaultNatTableStyleConfiguration());
		getNatTable().addConfiguration(new SingleClickSortConfiguration());
		MinovaGridConfiguration mgc = new MinovaGridConfiguration(dataTable.getColumns(), grid, dataService);
		getNatTable().addConfiguration(mgc);
		columnHideShowLayer.hideColumnPositions(mgc.getHiddenColumns());

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

		FormData fd = new FormData();
		fd.width = DEFAULT_WIDTH;
		fd.height = DEFAULT_HEIGHT;
		getNatTable().setLayoutData(fd);

		getNatTable().configure();
		return getNatTable();
	}

	public Table getDataTable() {
		return dataTable;
	}

	public void setDataTable(Table dataTable) {
		// Da die dataTable von SectionGrid und dem zugehörigen MGrid die selben sind können wir sie nicht einfach ersetzen
		this.dataTable.getRows().clear();
		for (Row r : dataTable.getRows()) {
			this.dataTable.addRow(r);
		}
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

		int newHeight;
		if (fd.height == DEFAULT_HEIGHT) {
			// Maximal 10 Zeilen anzeigen
			newHeight = Math.min(natTable.getRowHeightByPosition(0) * 11, natTable.getPreferredHeight());
			// Minimal 2 Zeilen anzeigen
			newHeight = Math.max(natTable.getRowHeightByPosition(0) * 3, newHeight);
		} else {
			newHeight = DEFAULT_HEIGHT;
		}

		fd.height = newHeight;
		natTable.requestLayout();
	}

	public void adjustWidth() {
		FormData fd = (FormData) natTable.getLayoutData();

		// TODO: Mit ausgeblendeten Spalten ist die neue Tabelle noch zu Breit
		int optimalWidth = natTable.getPreferredWidth();
		for (int i : columnHideShowLayer.getHiddenColumnIndexes()) {
			optimalWidth -= natTable.getColumnWidthByPosition(i);
		}

		// Toggel zwischen Default-Breite und kompletter Nattable
		int newWidth = fd.width == DEFAULT_WIDTH ? optimalWidth : DEFAULT_WIDTH;

		fd.width = newWidth;
		natTable.requestLayout();

		RowData rd = (RowData) section.getLayoutData();
		// Section soll nicht kleiner als Default sein
		rd.width = Math.max(newWidth, DEFAULT_WIDTH) + BUFFER;
		section.requestLayout();
	}

	public void addNewRow() {
		Row newRow = dataTable.addRow();
		preallocatePrimaryKeys(newRow);
		rowsToInsert.add(newRow);
		updateNatTable();
		gridAccessor.getMGrid().dataTableChanged();
	}

	private void preallocatePrimaryKeys(Row r) {
		List<Column> indexColumns = form.getIndexView().getColumn();
		Row indexRow = ((List<Row>) perspective.getContext().get(Constants.BROKER_ACTIVEROWS)).get(0);
		for (Field f : grid.getField()) {
			if (KeyType.PRIMARY.toString().equalsIgnoreCase(f.getKeyType())) {
				int index = grid.getField().indexOf(f);

				// Entsprechenden Wert im Index finden
				boolean found = false;
				for (int i = 0; i < form.getIndexView().getColumn().size(); i++) {
					// Name muss übereinstimmen oder Feld muss SQL-Index 0 haben und Column ist KeyLong
					if (indexColumns.get(i).getName().equals(f.getName())
							|| (f.getSqlIndex().intValue() == 0 && indexColumns.get(i).getName().equals("KeyLong"))) {
						found = true;
						r.setValue(new Value(indexRow.getValue(i).getValue()), index);
					}
				}
			}
		}
	}

	public void clearGrid() {
		dataTable.getRows().clear();
		updateNatTable();
		clearDataChanges();
	}

	public void deleteCurrentRows() {
		for (int i : selectionLayer.getFullySelectedRowPositions()) {
			dataTable.deleteRow(sortedList.get(i));
			rowsToDelete.add(sortedList.get(i));
		}
		updateNatTable();
	}

	public void setGridAccessor(GridAccessor gridAccessor) {
		this.gridAccessor = gridAccessor;
	}

	public void closeEditor() {
		natTable.commitAndCloseActiveCellEditor();
	}

	public void enableInsert(boolean enable) {
		insertToolItem.setEnabled(enable);
	}
}
