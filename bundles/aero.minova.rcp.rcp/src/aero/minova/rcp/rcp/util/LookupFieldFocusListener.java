package aero.minova.rcp.rcp.util;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.OutputType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class LookupFieldFocusListener implements FocusListener {

	protected UISynchronize sync;

	@Inject
	private IDataService dataService;

	@Inject
	private IEventBroker broker;

	public LookupFieldFocusListener(IEventBroker broker, IDataService dataService) {
		this.broker = broker;
		this.dataService = dataService;
	}

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
		Table ticketTable = new Table();
		String ticketNumber = lc.getText().replace("#", "");
		ticketTable.setName("Ticket");
		ticketTable.addColumn(new Column(Constants.TABLE_TICKETNUMBER, DataType.INTEGER, OutputType.OUTPUT));
		Row row = new Row();
		row.addValue(new Value(ticketNumber, DataType.STRING));
		ticketTable.addRow(row);

		CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(ticketTable.getName(),
				ticketTable);
		tableFuture.thenAccept(t -> sync.asyncExec(() -> {
			System.out.println("returnwert");
			if (t.getResultSet() != null) {
				broker.post("receivedTicket", t);
			} else {
				lc.setText("");
			}
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
			// clearColumn(lc);
		}

	}

}
