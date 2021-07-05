package aero.minova.rcp.rcp.widgets;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.form.model.xsd.Button;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.rcp.nattable.MinovaGridConfiguration;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.util.ImageUtil;
import aero.minova.rcp.rcp.util.NatTableUtil;
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

	private NatTable natTable;
	private Table dataTable;
	private Grid grid;
	private Composite composite;
	private Section section;

	private SortedList<Row> sortedList;
	private SelectionLayer selectionLayer;
	private MinovaColumnPropertyAccessor columnPropertyAccessor;
	private ColumnHeaderLayer columnHeaderLayer;
	private ColumnReorderLayer columnReorderLayer;
	private DataLayer bodyDataLayer;

	private LocalResourceManager resManager;

	public SectionGrid(Composite composite, Section section, Grid grid) {
		this.section = section;
		this.grid = grid;
		this.composite = composite;
		resManager = new LocalResourceManager(JFaceResources.getResources(), composite);

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
		// create, delete, optimizeWith, optimizeHigh
		if (grid.isButtonInsertVisible()) {
			Button btnInsert = new Button();
			btnInsert.setIcon("NewRecord.Command");
			btnInsert.setEnabled(true);
			btnInsert.setId("Insert");
			createButton(bar, btnInsert);
		}

		if (grid.isButtonDeleteVisible()) {
			Button btnDel = new Button();
			btnDel.setId("Delete");
			btnDel.setIcon("DeleteRecord.Command");
			btnDel.setEnabled(false);
			createButton(bar, btnDel);
		}

		// hier muss
		for (Button btn : grid.getButton()) {
			createButton(bar, btn);
		}

		// Standard
		Button btnOptimizeHigh = new Button();
		btnOptimizeHigh.setIcon("ExpandSectionVertical.Command");
		btnOptimizeHigh.setEnabled(true);
		btnOptimizeHigh.setId("OptimizeHigh");
		createButton(bar, btnOptimizeHigh);

		Button btnOptimizeWidth = new Button();
		btnOptimizeWidth.setIcon("ExpandSectionHorizontal.Command");
		btnOptimizeWidth.setEnabled(true);
		btnOptimizeWidth.setId("OptimizeWidth");
		createButton(bar, btnOptimizeWidth);

		section.setTextClient(bar);

	}

	public void createButton(ToolBar bar, Button btn) {
		createButton(bar, btn, "aero.minova.rcp.rcp.command.gridbuttoncommand");
	}

	public void createButton(ToolBar bar, Button btn, String commandName) {
		final ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setData(btn);
		item.setEnabled(btn.isEnabled());
		if (btn.getText() != null) {
			item.setText(translationService.translate(btn.getText(), null));
			item.setToolTipText(translationService.translate(btn.getText(), null));
		}

		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO: Andere procedures/bindings/instances auswerten
				System.out.println("Button pushed: " + item.getText());

				Map<String, String> parameter = new HashMap<>();
				parameter.put(Constants.CONTROL_BUTTON, btn.getId());
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

		GlazedListsEventLayer<Row> eventLayer = new GlazedListsEventLayer<>(bodyDataLayer, sortedList);

		columnReorderLayer = new ColumnReorderLayer(eventLayer);
		ColumnHideShowLayer columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
		selectionLayer = new SelectionLayer(columnHideShowLayer);
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		// as the selection mouse bindings are registered for the region label GridRegion.BODY we need to set that region label to the viewport so the selection
		// via mouse is working correctly
		viewportLayer.setRegionName(GridRegion.BODY);

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(columnPropertyAccessor.getPropertyNames(),
				columnPropertyAccessor.getTableHeadersMap());
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);

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

		getNatTable().addConfiguration(new MinovaGridConfiguration(dataTable.getColumns(), translationService, grid));

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
		fd.width = WFCDetailPart.SECTION_WIDTH;
		fd.height = COLUMN_HEIGHT * 3;

		getNatTable().setLayoutData(fd);
		getNatTable().configure();
		return getNatTable();
	}

	public Table getDataTable() {
		return dataTable;
	}

	public void setDataTable(Table dataTable) {
		this.dataTable = dataTable;
	}

	public NatTable getNatTable() {
		return natTable;
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

	public void AjustCorrectHigh() {
		int natTableHigh = getNatTableHigh();
		int sectionhigh = getSectionHigh();
		Rectangle bounds = section.getBounds();
		bounds.height = natTableHigh + 100;
		section.setBounds(bounds);
		section.redraw();
		section.requestLayout();
		section.layout();
		NatTableUtil.refresh(natTable);

	}

	public void addNewRow() {
		Table dummy = dataTable;
		dummy.addRow();
		// Datentablle muss angepasst weden, weil die beiden Listen sonst divergieren
		sortedList.add(dummy.getRows().get(dummy.getRows().size() - 1));
	}

}
