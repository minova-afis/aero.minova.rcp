package aero.minova.rcp.rcp.accessor;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;

public class NumberValueAccessor extends AbstractValueAccessor {

	public NumberValueAccessor(MField field, Control control) {
		super(field, control);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		if (value == null) {
			((Text) control).setText("");
		} else {
			if (value.getType().equals(DataType.DOUBLE)) {
				((Text) control).setText(value.getDoubleValue().toString());
			} else {
				((Text) control).setText(value.getIntegerValue().toString());
			}
		}
	}

}
