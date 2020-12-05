package aero.minova.rcp.rcp.accessor;

import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.fields.FieldUtil;

public class NumberValueAccessor extends AbstractValueAccessor {

	public NumberValueAccessor(MField field, Control control) {
		super(field, control);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		if (value == null) {
			((Text) control).setText("");
		} else {
			int decimals = (int) field.getDecimals();
			Locale locale = (Locale) control.getData(FieldUtil.TRANSLATE_LOCALE);

			NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
			numberFormat.setMaximumFractionDigits(decimals); // wir wollen genau so viele Nachkommastellen
			numberFormat.setMinimumFractionDigits(decimals); // dito

			if (value.getType().equals(DataType.DOUBLE)) {
				((Text) control).setText(numberFormat.format(value.getDoubleValue()));
			} else {
				((Text) control).setText(numberFormat.format(value.getIntegerValue()));
			}
		}
	}

}
