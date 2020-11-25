package aero.minova.rcp.rcp.util;

import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;

public class ValueAccessor {

	private Control control;
	private int index;
	private Field field;
	private DataType type;
	private Value value;

	public ValueAccessor(int index, Field field, Control control, DataType type) {
		this.index = index;
		this.field = field;
		this.control = control;
		this.type = type;
	}

	@Override
	public String toString() {
		return "ValueAccessor [value=" + value + "]";
	}

	public void setValue(Value value) {
		try {
			this.value = value;
			if (control instanceof Text) {
				Text text = (Text) control;
				text.setText(ValueBuilder.value(value, field).getText());
			} else if (control instanceof TextAssist) {
				TextAssist textAssist = (TextAssist) control;
				textAssist.setText(ValueBuilder.value(value, field).getText());
			}
		} catch (NullPointerException e) {
			System.out.println("Error ");
		}
	}

	public void setValue(Row row) {
		setValue(row.getValue(index));
	}

	public Value getValue() {
		String input = null;
		if (control instanceof Text) {
			Text text = (Text) control;
			input = text.getText();
		} else if (control instanceof TextAssist) {
			TextAssist textAssist = (TextAssist) control;
			input = textAssist.getText();
		}
		// TODO
		// Umwandlung des Strings in einen Value mit dem Korrekten Datentypen
		// 08:00
		// 14.11.2020
		// 5.7
		return value;
	}
}
