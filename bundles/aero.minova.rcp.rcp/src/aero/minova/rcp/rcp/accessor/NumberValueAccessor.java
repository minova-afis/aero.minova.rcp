package aero.minova.rcp.rcp.accessor;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.rcp.fields.FieldUtil;

public class NumberValueAccessor extends AbstractValueAccessor implements VerifyListener {

	public class Result {
		String text;
		int caretPosition;
		Value value;
	}

	private boolean verificationActive = false;

	public NumberValueAccessor(MNumberField field, Control control) {
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

	@Override
	public void verifyText(VerifyEvent e) {
		if (!isFocussed()) return; // Wir sind aktiv, wenn der Control den Focus hat
		if (verificationActive) return; // diese Methode setzt einen neuen Wert
		if (!e.doit) return; // anscheinend hat schon jemand reagiert

		// Werte vom Event
		String insertion = e.text;
		int start = e.start;
		int end = e.end;
		int keyCode = e.keyCode;

		// Werte aus dem Model
		int decimals = field.getDecimals();

		// Werte vom Text-Widget
		Text control = (Text) e.getSource();
		Locale locale = (Locale) control.getData(FieldUtil.TRANSLATE_LOCALE);
		int caretPosition = control.getCaretPosition();
		String textBefore = control.getText();

		// allegmeine Variablen
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);

		Result r = processInput(insertion, start, end, keyCode, decimals, locale, caretPosition, textBefore, dfs);

		verificationActive = true;
		field.setValue(r.value, true);
		control.setText(r.text);
		control.setSelection(r.caretPosition);
		e.doit = false;
		verificationActive = false;
	}

	/**
	 * <p>
	 * Diese Methode ermittelt den neu darzustellenden Text.
	 * </p>
	 * 
	 * @param insertion
	 *            die Benutzereingabe als Zeichenkette (darstellbare Zeichen) ({@link VerifyEvent#text})
	 * @param start
	 *            die 1. Position der selektierten Zeichenkette im {@code textBefore} ({@link VerifyEvent#start})
	 * @param end
	 *            die letzte Position der selektierten Zeichenkette im {@code textBefore} ({@link VerifyEvent#end})
	 * @param keyCode
	 *            der keyCode der Eingabe. Dies kann ein darstellbares Zeichen sein. Es kann aber auch ein nicht darstellbares Zeichen sein. Wenn die #insertion
	 *            mehr als ein Zeichen enthält, ist dieser Wert 0 ({@link Event#keyCode})
	 * @param decimals
	 *            die Anzahl der Nachkommastellen, die der Entwickler für das Feld definiert hat.
	 * @param locale
	 *            die aktuellen Spracheinstellungen
	 * @param caretPosition
	 *            die Position, an der sich der Cursor befindet ({@link Text#getCaretPosition()})
	 * @param textBefore
	 *            der Text, der aktuell (vor Verarbeitung dieses Events) im Feld steht ({@link Text#getText()})
	 * @param decimalFormatSymbols
	 *            {@link DecimalFormatSymbols} des aktuellen locale
	 * @return
	 */
	public Result processInput(String insertion, int start, int end, int keyCode, int decimals, Locale locale, int caretPosition, String textBefore,
			DecimalFormatSymbols decimalFormatSymbols) {
		Result result = new Result();
		String text;
		Boolean doit;
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		numberFormat.setMaximumFractionDigits(decimals);
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setGroupingUsed(true);
		StringBuilder sb = new StringBuilder();

		if (!textBefore.isEmpty() && keyCode == 127 && caretPosition > 0) {
			if (textBefore.charAt(caretPosition) == decimalFormatSymbols.getDecimalSeparator()
					|| textBefore.charAt(caretPosition) == decimalFormatSymbols.getGroupingSeparator()) {
				doit = false;
			} else {
				doit = true;
			}
		} else if (!textBefore.isEmpty() && keyCode == 8 && caretPosition > 0) {
			if (textBefore.charAt(caretPosition - 1) == decimalFormatSymbols.getDecimalSeparator()
					|| textBefore.charAt(caretPosition - 1) == decimalFormatSymbols.getGroupingSeparator()) {
				doit = false;
			} else {
				doit = true;
			}
		} else if (!textBefore.isEmpty() && !insertion.isEmpty()) {
			if (decimalFormatSymbols.getDecimalSeparator() == insertion.charAt(0)) { // Fall, dass die eingabe ein dezimal Trennzeichen ist.
				doit = false;
			} else {
				doit = true;
			}
		} else {
			doit = true;
		}

		if (doit == true) {

			// textBefore von überflüssigen Zeichen befreien
			int position = 0;
			for (char c : textBefore.toCharArray()) {
				if (c >= '0' && c <= '9') sb.append(c);
				else if (c == decimalFormatSymbols.getDecimalSeparator()) sb.append(c);
				else {
					// wir entfernen das Zeichen
//					if (caretPosition >= position) caretPosition--; // damit stehen wir auch ein Zeichen weiter vorne
					if (start > position) start--;
					if (end > position) end--;
					position--; // wird am Ende der Schleife wieder hochgezählt
				}
				position++;
			}
			textBefore = sb.toString();

			// insertion von überflüssigen Zeichen befreien
			position = 0;
			sb = new StringBuilder();
			for (char c : insertion.toCharArray()) {
				if (c >= '0' && c <= '9') sb.append(c);
				else if (c == decimalFormatSymbols.getDecimalSeparator()) sb.append(c);
				else position--; // wir entfernen das Zeichen; wird am Ende der Schleife wieder hochgezählt
				position++;
			}
			insertion = sb.toString();

			if (start != end) {
				// wir müssen etwas herausschneiden
				text = textBefore.substring(0, start) + textBefore.substring(end);
			} else text = textBefore;

			if (insertion.length() > 0) {
				// wir müssen etwas einfügen
				text = text.substring(0, start) + insertion + text.substring(start);
			}

			if (text.contains("" + decimalFormatSymbols.getDecimalSeparator())) {
				int decimalOverLength = text.substring(text.lastIndexOf(decimalFormatSymbols.getDecimalSeparator()) + 1).length() - decimals;
				// schneidet den dezimal Bereich auf die angebene dezimal Länge
				if (!textBefore.isEmpty() && 0 < decimalOverLength) text = text.substring(0, text.length() - decimalOverLength);
			}

		} else {
			int position = 0;
			for (char c : textBefore.toCharArray()) {
				if (c >= '0' && c <= '9') sb.append(c);
				else if (c == decimalFormatSymbols.getDecimalSeparator()) sb.append(c);
				else {
					// wir entfernen das Zeichen
					position--; // wird am Ende der Schleife wieder hochgezählt
				}
				position++;
			}
			textBefore = sb.toString();

			text = textBefore;
		}

		try {
			result.value = new Value(Double.parseDouble(text.replace(decimalFormatSymbols.getDecimalSeparator(), '.')));
			result.text = numberFormat.format(result.value.getDoubleValue());
			result.caretPosition = getNewCaretPosition(result.text, textBefore, insertion, keyCode, start, end, decimals, caretPosition, decimalFormatSymbols,
					numberFormat);
		} catch (NumberFormatException e) {
			result.value = new Value(0.0);
			result.text = numberFormat.format(result.value.getDoubleValue());
			result.caretPosition = 1;
		}

		return result;
	}

	/**
	 * <p>
	 * Diese Methode ermittelt die neue Caret Position.
	 * </p>
	 * <p>
	 * Die neue Caret Position wird ermittelt anhand des neuen formatierten Textes, des vorherigen Textes, der Eingabe und der vorherigen Caret Position. Wenn
	 * die Eingabe ein dezimal Trennzeichen ist wird die Caret Position hinter das Trennzeichen gesetzt. Beim Löschen oder Entfernen bleibt die Caret Position
	 * gleich.
	 * </p>
	 * 
	 * @param text
	 *            der Text, der sich aus insertion und textBefore zusammen setzt und formatiert wurde ({@link Text#getText()})
	 * @param textBefore
	 *            der Text, der aktuell (vor Verarbeitung dieses Events) im Feld steht ({@link Text#getText()})
	 * @param insertion
	 *            die Benutzereingabe als Zeichenkette (darstellbare Zeichen) ({@link VerifyEvent#text})
	 * @param keyCode
	 *            der keyCode der Eingabe. Dies kann ein darstellbares Zeichen sein. Es kann aber auch ein nicht darstellbares Zeichen sein. Wenn die #insertion
	 *            mehr als ein Zeichen enthält, ist dieser Wert 0 ({@link Event#keyCode})
	 * @param caretPosition
	 *            die Position, an der sich der Cursor befindet ({@link Text#getCaretPosition()})
	 * @param decimalFormatSymbols
	 *            {@link DecimalFormatSymbols} des aktuellen locale
	 * @return
	 */
	public int getNewCaretPosition(String text, String textBefore, String insertion, int keyCode, int start, int end, int decimals, int caretPosition,
			DecimalFormatSymbols decimalFormatSymbols, NumberFormat numberFormat) {

		if (!"".equals(textBefore) && null != textBefore)
			textBefore = numberFormat.format(Double.parseDouble(textBefore.replace(decimalFormatSymbols.getDecimalSeparator(), '.')));

		int newCaretPosition;
		String formatted0 = numberFormat.format(0); // stellt die formattierte Zahl 0 mit den jeweiligen dezimal Stellen dar
		int decimalCaretPostion = textBefore.length() - decimals; // ermittelt die Caret Postion nach dem dezimal Trennzeichen
		int lengthDifference = (text.length() - (textBefore.length() + insertion.length()));

		if (keyCode == 8) { // Fall, dass etwas mit backspace gelöscht wird
			if (decimalCaretPostion <= caretPosition) {
				newCaretPosition = caretPosition - 1;
			} else if (textBefore.charAt(caretPosition - 1) == decimalFormatSymbols.getGroupingSeparator()) {
				newCaretPosition = caretPosition - 1;
			} else {
				newCaretPosition = caretPosition + lengthDifference;
			}
		} else if (keyCode == 127) { // Fall, dass etwas mit ENTF entfernt wird
			if (formatted0.equals(text) || decimalCaretPostion <= caretPosition) {
				newCaretPosition = caretPosition + 1;
			} else if (textBefore.charAt(caretPosition) == decimalFormatSymbols.getGroupingSeparator()) {
				newCaretPosition = caretPosition + 1;
			} else {
				newCaretPosition = caretPosition + lengthDifference;
			}
		} else if (insertion.charAt(0) == decimalFormatSymbols.getDecimalSeparator()) { // Fall, dass die Engabe ein dezimal Trennzeich ist
			newCaretPosition = decimalCaretPostion;
		} else if (formatted0.equals(textBefore)) {
			if (caretPosition >= 1) {
				newCaretPosition = caretPosition + insertion.length() - 1;
			} else {
				newCaretPosition = caretPosition + insertion.length();
			}
		} else if ("".equals(textBefore)) {
			newCaretPosition = insertion.length();
		} else if (decimalCaretPostion <= caretPosition) {
			newCaretPosition = caretPosition + insertion.length();
			if (newCaretPosition >= text.length()) newCaretPosition = newCaretPosition - (newCaretPosition - text.length());
		} else {
			if (text.length() == textBefore.length() + insertion.length()) {
				newCaretPosition = caretPosition + insertion.length();
			} else {
				if (start != end) {
					String formatInsertion = numberFormat.format(Double.parseDouble(insertion.replace(decimalFormatSymbols.getDecimalSeparator(), '.')));
					if (0 != start) {
						newCaretPosition = start + 1 + formatInsertion.length() - decimals - 1;
					} else {
						if (insertion.contains("" + decimalFormatSymbols.getDecimalSeparator())) {
							newCaretPosition = start + formatInsertion.length();
						} else {
							newCaretPosition = start + formatInsertion.length() - decimals - 1;
						}
					}
				} else if (caretPosition >= 1) {
					newCaretPosition = caretPosition + insertion.length() + lengthDifference;
				} else {
					newCaretPosition = caretPosition + insertion.length() + lengthDifference - 1;
				}
			}
		}

		return newCaretPosition;
	}
}
