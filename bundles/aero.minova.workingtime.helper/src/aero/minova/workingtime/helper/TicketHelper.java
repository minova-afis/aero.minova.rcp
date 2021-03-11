package aero.minova.workingtime.helper;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MLookupField;

public class TicketHelper implements ValueChangeListener {

	WorkingTimeHelper workingTimeHelper;

	public TicketHelper(WorkingTimeHelper workingTimeHelper) {
		this.workingTimeHelper = workingTimeHelper;
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		MLookupField lookupField = (MLookupField) evt.getField();
		String writtenText = lookupField.getWrittenText();
		if (writtenText != null && writtenText.startsWith("#")) {
			System.out.println("gelesener Text aus dem Lookup:" + lookupField.getWrittenText());
			Value value = new Value(writtenText.replace("#", ""), DataType.STRING);
			workingTimeHelper.postEvent(value);
		}
	}

}
