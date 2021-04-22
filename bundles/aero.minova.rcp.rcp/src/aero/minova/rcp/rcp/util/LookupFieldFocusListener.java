package aero.minova.rcp.rcp.util;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.OutputType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.rcp.widgets.Lookup;

public class LookupFieldFocusListener implements FocusListener {

	protected UISynchronize sync;

	private IDataService dataService;

	private IEventBroker broker;

	public LookupFieldFocusListener(IEventBroker broker, IDataService dataService, UISynchronize sync) {
		this.broker = broker;
		this.dataService = dataService;
		this.sync = sync;
	}

	/**
	 * Wir versenden eine Anfrage an den CAS, welche die Ticketnummer enthält. Mit der Erhaltenen Antwort füllen wir sämltiche LookupFields sowie das
	 * DescriptionField
	 *
	 * @param lc
	 */
	private void getTicketFromCAS(Lookup lc) {
		Table ticketTable = new Table();
		// das Pattern ist eine unbegrenzte Menge an Zahlen hinter einer Raute
		Pattern ticketnumber = Pattern.compile("#(\\d*)");
		Matcher m = ticketnumber.matcher(lc.getText());
		String tracNumber = "";
		// true, falls das Pattern vorhanden ist
		if (m.find()) {
			// die Tracnummmer, ab dem ersten Symbol --> ohne die Raute
			tracNumber = m.group(1);
		}
		ticketTable.setName("Ticket");
		ticketTable.addColumn(new Column(Constants.TABLE_TICKETNUMBER, DataType.INTEGER, OutputType.OUTPUT));
		Row row = new Row();
		row.addValue(new Value(tracNumber, DataType.STRING));
		ticketTable.addRow(row);

		CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(ticketTable.getName(), ticketTable);
		tableFuture.thenAccept(t -> sync.asyncExec(() -> {
			if (t.getResultSet() != null) {
				broker.post(Constants.RECEIVED_TICKET, t.getResultSet());
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
		Lookup lc = (Lookup) t.getParent();
		if (lc.getText().startsWith("#")) {
			getTicketFromCAS(lc);
		} else {
			// clearColumn(lc);
		}

	}

}
