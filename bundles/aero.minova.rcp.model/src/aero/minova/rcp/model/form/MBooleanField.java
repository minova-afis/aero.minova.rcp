package aero.minova.rcp.model.form;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;

public class MBooleanField extends MField {
	public MBooleanField() {
		super(DataType.BOOLEAN);
		// Boolean Felder dürfen keinen null-Value haben, weil dies nicht dargestellt werden kann
		this.setValue(new Value(false), false);
	}

	@Override
	public void setValue(Value value, boolean user) {
		// Boolean Felder dürfen keinen null-Value haben, weil dies nicht dargestellt werden kann
		if (value == null) {
			value = new Value(false);
		}
		super.setValue(value, user);
	}
}