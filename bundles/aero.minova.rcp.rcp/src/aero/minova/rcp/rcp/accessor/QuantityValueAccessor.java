package aero.minova.rcp.rcp.accessor;

import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.QuantityValue;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.fields.FieldUtil;
import aero.minova.rcp.rcp.util.NumberFormatUtil;

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
				QuantityValue qV = (QuantityValue) value;
				String numberString = numberFormat.format(qV.number);
				setText(control, numberString);

			} else {
				String valueString = NumberFormatUtil.getValueString(numberFormat, field.getDataType(), value);

				String[] numberAndUnit = NumberFormatUtil.splitNumberUnitEntry(valueString);
				String number = numberAndUnit[0];

				setText(control, number);
			}
		}
	}

	private void setText(Control control, String text) {
		if (control instanceof TextAssist) {
			((TextAssist) control).setText(text);
		} else if (control instanceof Text) {
			((Text) control).setText(text);
		}
	}
}
