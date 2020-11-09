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
		Text field = (Text) e.getSource();
		int decimals = (int) field.getData(FieldUtil.FIELD_DECIMALS);
		Locale locale = (Locale) field.getData(FieldUtil.TRANSLATE_LOCALE);
		int caretPosition = field.getCaretPosition();
		int start = e.start;
		int end = e.end;
		String textBefore = field.getText();
		String insertion = e.text;
		
		String newText = getNewText(decimals, locale, textBefore, caretPosition, start, end, insertion);
		int newCaretPosition = getNewCaretPosition(decimals, locale, textBefore, caretPosition, start, end, insertion, newText);
		
			}

	
//	{
//		if (verificationActive) return; // Wir setzen gerade den Wert
//		if (e.widget != field) return; // ist nicht unser Feld
//
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
//		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
//		numberFormat.setMaximumFractionDigits(decimals);
//		numberFormat.setMinimumFractionDigits(decimals);
//		numberFormat.setGroupingUsed(true);
//		String insertion = e.text;
//		String text = field.getText();
//		if (e.text.equals("" + dfs.getDecimalSeparator()) && text.indexOf(dfs.getDecimalSeparator()) >= 0) {
//			field.setSelection(text.indexOf(dfs.getDecimalSeparator()) + 1);
//			e.doit = false;
//			return;
//		}
//
//		int caretPosition = field.getCaretPosition();
//		String newText;
//		if ("".equals(e.text)) {
//			newText = text.substring(0, e.start) + text.substring(e.end);
//		} else {
//			newText = text.substring(0, caretPosition) + insertion + text.substring(caretPosition);
//		}
//		newText = newText.replaceAll("[" + dfs.getGroupingSeparator() + "]", "");
//		newText = newText.replaceAll("[" + dfs.getDecimalSeparator() + "]", ".");
//		Double newValue;
//		if (newText.isEmpty()) {
//			newValue = null;
//		} else {
//			newValue = Double.parseDouble(newText);
//			newText = numberFormat.format(newValue);
//			verificationActive = true;
//			field.setText(newText);
//			field.setSelection(caretPosition + insertion.length());
//			verificationActive = false;
//		}
//		field.setData(FieldUtil.FIELD_VALUE, newValue);
//		e.doit = false;
//
//	}
	
	protected int getNewCaretPosition(int decimals, Locale locale, String textBefore, int caretPosition, int start,
			int end, String insertion, String newText) {
		// TODO Auto-generated method stub
		return 0;
	}

	protected String getNewText(int decimals, Locale locale, String textBefore, int caretPosition, int start, int end,
			String insertion) {
		// TODO Auto-generated method stub
		return "9,00";
	}

}
