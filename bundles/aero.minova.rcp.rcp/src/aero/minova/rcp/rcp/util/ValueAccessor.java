package aero.minova.rcp.rcp.util;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;

public class ValueAccessor {

	private Control control;
	private int index;
	private Field field;
	private Value value;

	public ValueAccessor(int index, Field field, Control control) {
		this.index = index;
		this.field = field;
		this.control = control;
	}

	public void setValue(Row row) {
		try {
		value = row.getValue(index);
		Text text = (Text) control;
		text.setText(ValueBuilder.value(value, field).getText());
	} catch (NullPointerException e) {
		System.out.println("Error ");
	}
	}

	public Value getValue() {
		return value;
	}
}
