package aero.minova.rcp.rcp.fields;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

public class NumberFieldVerifier implements VerifyListener {

	private boolean verificationActive = false;

	public NumberFieldVerifier() {
	}

	@Override
	public void verifyText(VerifyEvent e) {
		if (verificationActive)
			return; // Wir setzen gerade den Wert

		Text field = (Text) e.getSource();
		int decimals = (int) field.getData(FieldUtil.FIELD_DECIMALS);
		Locale locale = (Locale) field.getData(FieldUtil.TRANSLATE_LOCALE);
		String insertion = e.text;
		int caretPosition = field.getCaretPosition();
		int start = e.start;
		int end = e.end;
		String textBefore = field.getText();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
		int newCaretPosition = getNewCaretPosition(insertion, dfs, caretPosition);

		String newText = getNewText(decimals, locale, textBefore, newCaretPosition, start, end, insertion, dfs);
		Double newValue = getNewValue(newText, dfs);

		verificationActive = true;
		field.setText(newText);
		field.setSelection(caretPosition + insertion.length());
		field.setData(FieldUtil.FIELD_VALUE, newValue);
		verificationActive = false;
		e.doit = false;
	}

	protected Double getNewValue(String newText, DecimalFormatSymbols dfs) {
		Double newValue;
		if (newText.isEmpty()) {
			newValue = null;
		} else {
			newText = newText.replaceAll("[" + dfs.getDecimalSeparator() + "]", ".");
			newValue = Double.parseDouble(newText);
		}
		return newValue;
	}

	protected int getNewCaretPosition(String insertion, DecimalFormatSymbols dfs, int caretPosition) {
		int newCaretPosition = caretPosition;
		if (!insertion.equals("") && dfs.getDecimalSeparator() == insertion.charAt(0))
			newCaretPosition = 1;

		return newCaretPosition;
	}

	protected String getNewText(int decimals, Locale locale, String textBefore, int caretPosition, int start, int end,
			String insertion, DecimalFormatSymbols dfs) {
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
		newText = newText.replaceAll("[" + dfs.getGroupingSeparator() + "]", "");
		newText = newText.replaceAll("[" + dfs.getDecimalSeparator() + "]", ".");
		if (!newText.isEmpty())
			newText = numberFormat.format(getNewValue(newText, dfs));

		return newText;
	}

}
