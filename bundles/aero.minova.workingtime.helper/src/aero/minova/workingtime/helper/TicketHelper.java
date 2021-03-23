package aero.minova.workingtime.helper;

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
			lookupField.setWrittenText(null);
			Value value = new Value(writtenText.replaceAll("[^0-9]", "").trim(), DataType.STRING);
			workingTimeHelper.postEvent(value);
		}
	}

}
