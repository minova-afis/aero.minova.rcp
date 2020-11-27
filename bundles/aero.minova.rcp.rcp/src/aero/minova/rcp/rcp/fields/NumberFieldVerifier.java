package aero.minova.rcp.rcp.fields;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

public class NumberFieldVerifier implements VerifyListener {

	private boolean verificationActive = false;

	@Override
	public void verifyText(VerifyEvent e) {
		if (verificationActive) return; // Wir setzen gerade den Wert

		Text field = (Text) e.getSource();
		int decimals = (int) field.getData(FieldUtil.FIELD_DECIMALS);
		Locale locale = (Locale) field.getData(FieldUtil.TRANSLATE_LOCALE);
		String insertion = e.text;
		int caretPosition = field.getCaretPosition();
		int start = e.start;
		int end = e.end;
		int keyCode = e.keyCode;
		String textBefore = field.getText();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);

		if (!textBefore.isEmpty() && textBefore.charAt(caretPosition) == dfs.getDecimalSeparator() && keyCode == 127) {
			e.doit = false;
		} else if (!textBefore.isEmpty() && textBefore.charAt(caretPosition - 1) == dfs.getDecimalSeparator()
				&& keyCode == 8) {
			e.doit = false;
		}

		verificationActive = true;
		field.setText(newText);
		field.setSelection(newCaretPosition);
		field.setData(FieldUtil.FIELD_VALUE, newValue);
		verificationActive = false;
		e.doit = false;
	}

	protected Double getNewValue(String newText, DecimalFormatSymbols dfs) {
		Double newValue;
		if (newText.isEmpty()) {
			newValue = null;
		} else {
			newText = newText.replaceAll("[" + dfs.getGroupingSeparator() + "]", "");
			newText = newText.replaceAll("[" + dfs.getDecimalSeparator() + "]", ".");
			newValue = Double.parseDouble(newText);
		}
		return newValue;
	}

	protected int getNewCaretPosition(String textBefore, String insertion, String newText, DecimalFormatSymbols dfs, int caretPosition, int keyCode) {
		int newCaretPosition;
		if (keyCode == 8) {
			if (newText.length() <= 1) {
				newCaretPosition = caretPosition;
			} else {
				newCaretPosition = newText.length() - 3;
			}
		} else if (keyCode == 127) {
			newCaretPosition = caretPosition;
		} else if (dfs.getDecimalSeparator() == insertion.charAt(0)) {
			newCaretPosition = newText.length() - 3 + 1;
		} else if (textBefore.equals("0" + dfs.getDecimalSeparator() + "00")) {
			newCaretPosition = insertion.length();
		} else if (insertion.equals("")) {
			newCaretPosition = caretPosition;
		} else {
			newCaretPosition = newText.length() - 3;
		}

		return newCaretPosition;
	}

	protected String getNewText(int decimals, Locale locale, String textBefore, int caretPosition, int start, int end, String insertion,
			DecimalFormatSymbols dfs) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		numberFormat.setMaximumFractionDigits(decimals);
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setGroupingUsed(true);
		String newText;

		if ("".equals(insertion)) {
			newText = textBefore.substring(0, start) + textBefore.substring(end);
		} else if (dfs.getDecimalSeparator() == insertion.charAt(0)) {
			newText = textBefore.substring(0, start) + textBefore.substring(end);
		} else {
			newText = textBefore.substring(0, caretPosition) + insertion + textBefore.substring(caretPosition);
		}
		if (!newText.isEmpty()) newText = numberFormat.format(getNewValue(newText, dfs));

		return newText;
	}

}
