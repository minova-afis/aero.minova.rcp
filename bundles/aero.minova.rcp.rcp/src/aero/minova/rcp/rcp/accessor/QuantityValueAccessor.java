package aero.minova.rcp.rcp.accessor;

import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.model.QuantityValue;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.util.NumberFormatUtil;
import aero.minova.rcp.rcp.fields.FieldUtil;

public class QuantityValueAccessor extends AbstractValueAccessor {

	public QuantityValueAccessor(MField field, Control control) {
		super(field, control);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		int decimals = field.getDecimals();
		Locale locale = (Locale) control.getData(FieldUtil.TRANSLATE_LOCALE);

		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		numberFormat.setMaximumFractionDigits(decimals); // wir wollen genau so viele Nachkommastellen
		numberFormat.setMinimumFractionDigits(decimals); // dito

		// we see this control disposed in our unit tests
		if (control.isDisposed()) {
			return;
		}

		if (value == null) {
			setText(control, "");
		} else {

			if (value instanceof QuantityValue) {
				String numberString = NumberFormatUtil.getValueString(numberFormat, field.getDataType(), value);
				setText(control, numberString);

			} else {
				String valueString = NumberFormatUtil.getValueString(numberFormat, field.getDataType(), value);

				String[] numberAndUnit = NumberFormatUtil.splitNumberUnitEntry(valueString);
				String number = numberAndUnit[0];

				setText(control, number);
			}
		}
	}
}
