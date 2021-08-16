package aero.minova.workingtime.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MLookupField;

public class TicketHelper implements ValueChangeListener {

	private static final boolean LOG = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.workingtime.heper/debug/tickethelper"));
	WorkingTimeHelper workingTimeHelper;

	public TicketHelper(WorkingTimeHelper workingTimeHelper) {
		this.workingTimeHelper = workingTimeHelper;
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		MLookupField lookupField = (MLookupField) evt.getField();
		String writtenText = lookupField.getWrittenText();
		if (writtenText != null && writtenText.startsWith("#")) {
			if (LOG) {
				System.out.println("gelesener Text aus dem Lookup:" + writtenText);
			}
			lookupField.setWrittenText("");
			// das Pattern ist eine unbegrenzte Menge an Zahlen hinter einer Raute
			Pattern ticketnumber = Pattern.compile("#(\\d*)");
			Matcher m = ticketnumber.matcher(writtenText);
			// true, falls das Pattern vorhanden ist
			if (writtenText.equals("#-123")) {
				workingTimeHelper.postEvent(new Value(writtenText.replace("#", ""), DataType.STRING));
			} else if (m.find()) {
				// die Tracnummer, ab dem ersten Symbol --> ohne die Raute
				String tracNumber = m.group(1);
				if (tracNumber.length() > 0) {
					Value value = new Value(tracNumber, DataType.STRING);
					workingTimeHelper.postEvent(value);
				} else {
					workingTimeHelper.postEvent(new Value(null, DataType.STRING));
				}
			} else {
				workingTimeHelper.postEvent(new Value(null, DataType.STRING));
			}

		}
	}

}
