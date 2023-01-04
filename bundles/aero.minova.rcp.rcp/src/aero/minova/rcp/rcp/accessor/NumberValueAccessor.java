package aero.minova.rcp.rcp.accessor;

import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.model.util.NumberFormatUtil;
import aero.minova.rcp.rcp.fields.FieldUtil;

public class NumberValueAccessor extends AbstractValueAccessor {

	public NumberValueAccessor(MNumberField field, Control control) {
		super(field, control);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		// we see this control disposed in our unit tests
		if (control.isDisposed()) {
			return;
		}
		if (value == null) {
			setText(control, "");
		} else {
			int decimals = field.getDecimals();
			Locale locale = (Locale) control.getData(FieldUtil.TRANSLATE_LOCALE);

			NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
			numberFormat.setMaximumFractionDigits(decimals); // wir wollen genau so viele Nachkommastellen
			numberFormat.setMinimumFractionDigits(decimals); // dito
			
			String valueString = NumberFormatUtil.getValueString(numberFormat, field.getDataType(), value);

			setText(control, valueString);
		}
	}
	
	private void setText(Control control, String text) {
		if (control instanceof TextAssist) {
			((TextAssist) control).setText(text);
		} else if (control instanceof Text) {
			((Text) control).setText(text);
		}
	}

//	@Override
//	public void verifyText(VerifyEvent e) {
//		if (!isFocussed()) {
//			return; // Wir sind aktiv, wenn der Control den Focus hat
//		}
//		if (verificationActive) {
//			return; // diese Methode setzt einen neuen Wert
//		}
//		if (!e.doit) {
//			return; // anscheinend hat schon jemand reagiert
//		}
//
//		// Werte vom Event
//		String insertion = e.text;
//		int start = e.start;
//		int end = e.end;
//		int keyCode = e.keyCode;
//
//		// Werte aus dem Model
//		int decimals = field.getDecimals();
//
//		// Werte vom Text-Widget
//		Text control = (Text) e.getSource();
//		Locale locale = (Locale) control.getData(FieldUtil.TRANSLATE_LOCALE);
//		boolean rangeSelected = false;
//
//		int caretPosition = control.getCaretPosition();
//		if (control.getSelection() != null) {
//			caretPosition = control.getSelection().x;
//			rangeSelected = true;
//		}
//
//		String textBefore = control.getText();
//
//		// allegmeine Variablen
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
//		if (insertion.matches("([0-9]*)|([\\" + dfs.getGroupingSeparator() + dfs.getDecimalSeparator() + "]*)")) {
//			aero.minova.rcp.rcp.util.NumberFormatUtil.Result r = new Result();
//
//			try {
//				r = NumberFormatUtil.processInput(field, insertion, start, end, keyCode, decimals, locale, caretPosition, textBefore, dfs, rangeSelected);
//			} catch (Exception exception) {
//				NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
//				r.value = new Value(0.0, field.getDataType());
//				if (field.getDataType().equals(DataType.BIGDECIMAL)) {
//					r.text = numberFormat.format(r.value.getBigDecimalValue());
//				} else {
//					r.text = numberFormat.format(r.value.getDoubleValue());
//				}
//				r.caretPosition = 1;
//			}
//
//			verificationActive = true;
//			field.setValue(r.value, true);
//			control.setText(r.text);
//			control.setSelection(r.caretPosition);
//		}
//		e.doit = false;
//		verificationActive = false;
//	}

}
