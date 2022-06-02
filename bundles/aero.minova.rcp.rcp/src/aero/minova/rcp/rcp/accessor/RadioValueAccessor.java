package aero.minova.rcp.rcp.accessor;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MRadioField;

public class RadioValueAccessor extends AbstractValueAccessor {

	public RadioValueAccessor(MField field, Label label) {
		super(field, label);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		String fieldName = "";
		if (value != null) {
			fieldName = value.getStringValue();
		}

		for (MBooleanField b : ((MRadioField) field).getRadiobuttons()) {
			if (b.getName().equals(fieldName)) {
				b.setValue(new Value(true), false);
			} else {
				b.setValue(new Value(false), false);
			}
		}
	}

}
