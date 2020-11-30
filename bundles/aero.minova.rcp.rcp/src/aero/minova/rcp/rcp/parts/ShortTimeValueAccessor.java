package aero.minova.rcp.rcp.parts;

import org.eclipse.nebula.widgets.opal.textassist.TextAssist;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.ValueAccessor;

public class ShortTimeValueAccessor implements ValueAccessor {

	MField field;
	TextAssist text;

	public ShortTimeValueAccessor(MField field, TextAssist text) {
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
