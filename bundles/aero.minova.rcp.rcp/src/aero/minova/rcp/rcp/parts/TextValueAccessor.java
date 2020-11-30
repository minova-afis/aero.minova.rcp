package aero.minova.rcp.rcp.parts;

import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.ValueAccessor;

public class TextValueAccessor implements ValueAccessor {

	MField field;
	Text text;

	public TextValueAccessor(MField field, Text text) {
		this.field = field;
		this.text = text;
	}

	@Override
	public void setValue(Value value, boolean user) {
		field.setValue(value, user);
	}

	@Override
	public void setValue(Row row) {
		Value value = row.getValue(field.getSqlIndex());
		field.setValue(value, false);
	}

}
