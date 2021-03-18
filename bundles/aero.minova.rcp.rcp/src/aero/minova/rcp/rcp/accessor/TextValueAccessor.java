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
		String newText = value == null ? "" : value.getStringValue();
		String currentText = ((Text) control).getText();

		if (newText.equals(currentText))
			return; // ist ja schon gesetzt
		else if (((Text) control).getEditable())
			((Text) control).setText(newText);
	}

	public void setText(String text) {
		((Text) control).setText(text);
	}

	public void setColor(Color color) {
		((Text) control).setForeground(color);
	}

}
