package aero.minova.rcp.rcp.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.plugin1.model.Column;
import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.plugin1.model.Row;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.Value;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.rcp.util.PersistTableSelection;

public class XMLIndexPart {

	@Inject
	@Preference
	IEclipsePreferences prefs;

	@Inject
	private IMinovaJsonService mjs;

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private EModelService model;

	private Table data;

	@Inject
	IEventBroker broker;

	@Inject
	private IDataService dataService;

	@Inject
	private MPart mPart;

	@Inject
	private MPerspective mPerspective;

	private NatTable natTable;

	@PostConstruct
	public void createComposite(Composite parent) {

		Form form = dataFormService.getForm();
		String tableName = form.getIndexView().getSource();

		String string = prefs.get(tableName, null);
		data = dataFormService.getTableFromFormIndex(form);
		data.addRow();
		if (string != null) {
			data = mjs.json2Table(string);
		}

		parent.setLayout(new GridLayout());
		natTable = NatTableUtil.createNatTable(parent, form, data, true);
		natTable.addLayerListener(new ILayerListener() {
			@Override
			public void handleLayerEvent(ILayerEvent arg0) {
				if (arg0 instanceof CellSelectionEvent) {
					changeSelectedEntry(
							natTable.getLayer().getRowIndexByPosition(((CellSelectionEvent) arg0).getRowPosition()));
				}
			}
		});
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
	public void load(@UIEventTopic("PLAPLA") Table table) {
		data.getRows().clear();
		for (Row r : table.getRows()) {
			data.addRow(r);
		}
		natTable.refresh(false);
		natTable.requestLayout();
	}

	public void changeSelectedEntry(int selectedRowIndex) {
		int keylong = (int) natTable.getLayer().getDataValueByPosition(1, selectedRowIndex + 1);
		System.out.println(keylong);
		//
		Table rowIndexTable = new Table();
		rowIndexTable.setName("spReadWorkingTime");
		rowIndexTable.addColumn(new Column("KeyLong", DataType.INTEGER));
		rowIndexTable.addColumn(new Column("EmployeeKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("OrderReceiverKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("ServiceContractKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("ServiceObjectKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("ServiceKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("BookingDate", DataType.ZONED));
		rowIndexTable.addColumn(new Column("StartDate", DataType.ZONED));
		rowIndexTable.addColumn(new Column("EndDate", DataType.ZONED));
		rowIndexTable.addColumn(new Column("RenderedQuantity", DataType.DOUBLE));
		rowIndexTable.addColumn(new Column("ChargedQuantity", DataType.DOUBLE));
		rowIndexTable.addColumn(new Column("Description", DataType.STRING));
		rowIndexTable.addColumn(new Column("Spelling", DataType.STRING));
		Row r = new Row();
		r.addValue(new Value(keylong));
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		rowIndexTable.addRow(r);
		CompletableFuture<Table> tableFuture = dataService.getDataAsync(rowIndexTable.getName(), rowIndexTable);
		tableFuture.thenAccept(t -> broker.post("CAS_request_selected_entry", t));
	}
}
