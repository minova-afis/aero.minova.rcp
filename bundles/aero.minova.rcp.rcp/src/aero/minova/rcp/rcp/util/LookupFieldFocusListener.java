package aero.minova.rcp.rcp.util;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class LookupFieldFocusListener implements FocusListener {

	protected UISynchronize sync;

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private IDataService dataService;

	@Inject
	private IEventBroker broker;

	/**
	 * Wenn der Keylong nicht gesetzt wurde, so wird das Feld bereinigt
	 */
	private void clearColumn(LookupControl lc) {
		if (lc.getData(Constants.CONTROL_KEYLONG) == null) {
			lc.setText("");
		} else {
			Table t = (Table) lc.getData(Constants.CONTROL_OPTIONS);
			if (t != null) {
				for (Row r : t.getRows()) {
					if (r.getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)).getIntegerValue() == lc
							.getData(Constants.CONTROL_KEYLONG)) {
						lc.setText(r.getValue(t.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue());
					}
				}
			}
		}
	}

	/**
	 * Wir versenden eine Anfrage an den CAS, welche die Ticketnummer enthält. Mit
	 * der Erhaltenen Antwort füllen wir sämltiche LookupFields sowie das
	 * DescriptionField
	 * 
	 * @param lc
	 */
	private void getTicketFromCAS(LookupControl lc) {
		Form form = dataFormService.getForm();
		Table rowIndexTable = dataFormService.getTableFromFormDetail(form, "Read");
		Row row = new Row();
		row.addValue(new Value(lc.getText(), DataType.STRING));
		rowIndexTable.addRow(row);

		CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(rowIndexTable.getName(),
				rowIndexTable);
		tableFuture.thenAccept(t -> sync.asyncExec(() -> {
			broker.post("receivedTicket", t);
		}));

	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusLost(FocusEvent e) {
		Text t = (Text) e.getSource();
		LookupControl lc = (LookupControl) t.getParent();
		if (lc.getText().startsWith("#")) {
			getTicketFromCAS(lc);
		} else {
			clearColumn(lc);
		}

	}

}
