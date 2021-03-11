package aero.minova.workingtime.helper;

import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MLookupField;

public class TicketHelper implements ValueChangeListener {

	@Override
	public void valueChange(ValueChangeEvent evt) {
		MLookupField lookupField = (MLookupField) evt.getField();
		LookupValue value = (LookupValue) lookupField.getValue();
		System.out.println(value);
	}

}
