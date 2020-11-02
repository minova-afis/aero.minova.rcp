package aero.minova.rcp.rcp.fields;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import static aero.minova.rcp.rcp.fields.ValueChangedEvent.SourceType;

public class NumberFieldVerifier implements VerifyListener {

	private Text field;
	private int decimals;
	private boolean verificationActive = false;

	public NumberFieldVerifier(Text text) {
		field = text;
		decimals = (int) field.getData(FieldUtil.FIELD_DECIMALS);
	}

	@Override
	public void verifyText(VerifyEvent e) {
		if (verificationActive) return; // Wir setzen gerade den Wert
		if (e.widget != field) return; // ist nicht unser Feld

		Locale locale = (Locale) field.getData(FieldUtil.TRANSLATE_LOCALE);
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		numberFormat.setMaximumFractionDigits(decimals);
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setGroupingUsed(true);
		String insertion = e.text;
		String text = field.getText();
		// String selection = text.substring(e.start, e.end);
		if (e.text.equals("" + dfs.getDecimalSeparator())) {
			if (text.indexOf(dfs.getDecimalSeparator()) > 0) {
				field.setSelection(text.indexOf(dfs.getDecimalSeparator()) + 1);
				e.doit = false;
				return;
			}
		}

		int caretPosition = field.getCaretPosition();
		String newText;
		if ("".equals(e.text)) {
			newText = text.substring(0, e.start) + text.substring(e.end);
		} else {
			newText = text.substring(0, caretPosition) + insertion + text.substring(caretPosition);
		}
		newText = newText.replaceAll("[" + dfs.getGroupingSeparator() + "]", "");
		newText = newText.replaceAll("[" + dfs.getDecimalSeparator() + "]", ".");
		double newValue = Double.parseDouble(newText);
		Double oldValue = (Double) field.getData(FieldUtil.FIELD_VALUE);
		newText = numberFormat.format(newValue);
		verificationActive = true;
		field.setText(newText);
		field.setData(FieldUtil.FIELD_VALUE, newValue);
		field.notifyListeners(SWT.NONE, new ValueChangedEvent(field, oldValue, newValue, SourceType.USER));
		field.setSelection(caretPosition + insertion.length());
		verificationActive = false;
		e.doit = false;
	}

}
