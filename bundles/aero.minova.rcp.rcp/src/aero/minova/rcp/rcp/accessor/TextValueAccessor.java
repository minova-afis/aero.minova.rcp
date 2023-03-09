package aero.minova.rcp.rcp.accessor;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;

public class TextValueAccessor extends AbstractValueAccessor {

	public TextValueAccessor(MField field, Text text) {
		super(field, text);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		// we see this control disposed in our unit tests
		if (control.isDisposed()) {
			return;
		}
		String newText = value == null ? "" : value.getStringValue();
		String currentText = ((Text) control).getText();

		if (!newText.equals(currentText)) {
			((Text) control).setText(newText);
		}
	}

	public void setText(String text) {
		setText(control, text);
	}

	public void setColor(Color color) {
		((Text) control).setForeground(color);
	}
}
