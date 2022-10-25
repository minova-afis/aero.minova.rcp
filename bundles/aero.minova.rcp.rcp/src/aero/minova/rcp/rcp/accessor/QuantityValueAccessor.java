package aero.minova.rcp.rcp.accessor;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MQuantityField;
import aero.minova.rcp.rcp.fields.FieldUtil;

public class QuantityValueAccessor extends AbstractValueAccessor implements VerifyListener {

	public static class Result {
		String text;
		int caretPosition;
		Value value;
	}

	public QuantityValueAccessor(MQuantityField field, Control control) {
		super(field, control);
	}


	@Override
	protected void updateControlFromValue(Control control, Value value) {
		// we see this control disposed in our unit tests
		if (control.isDisposed()) {
			return;
		}
		if (value == null) {
			((Text) control).setText("");
		} else {
			int decimals = field.getDecimals();
			Locale locale = (Locale) control.getData(FieldUtil.TRANSLATE_LOCALE);

			NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
			numberFormat.setMaximumFractionDigits(decimals); // wir wollen genau so viele Nachkommastellen
			numberFormat.setMinimumFractionDigits(decimals); // dito
			
			Pattern p = Pattern.compile("\\p{Alpha}");
			Matcher m = p.matcher(value.getStringValue());
			
			value = new Value(value.getStringValue().substring(value.getStringValue().indexOf(m.start())), field.getDataType());

			if (value.getType().equals(DataType.DOUBLE)) {
				((Text) control).setText(numberFormat.format(value.getDoubleValue()));
			} else if (value.getType().equals(DataType.BIGDECIMAL)) {
				((Text) control).setText(numberFormat.format(value.getBigDecimalValue()));
			} else {
				((Text) control).setText(numberFormat.format(value.getIntegerValue()));
			}
		}
	}

	@Override
	public void verifyText(VerifyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
