package aero.minova.rcp.rcp.accessor;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;

public class BooleanValueAccessor extends AbstractValueAccessor {

	public BooleanValueAccessor(MField field, Control control) {
		super(field, control);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		if (value == null) {
			((Button) control).setSelection(false);
		} else {
			((Button) control).setSelection(value.getBooleanValue());
		}
	}
}
