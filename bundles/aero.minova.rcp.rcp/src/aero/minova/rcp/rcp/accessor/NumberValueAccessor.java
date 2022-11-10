package aero.minova.rcp.rcp.accessor;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.SWT;
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

	public static class Result {
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
		if (!isFocussed()) {
			return; // Wir sind aktiv, wenn der Control den Focus hat
		}
		if (verificationActive) {
			return; // diese Methode setzt einen neuen Wert
		}
		if (!e.doit) {
			return; // anscheinend hat schon jemand reagiert
		}

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
		boolean rangeSelected = false;

		int caretPosition = control.getCaretPosition();
		if (control.getSelection() != null) {
			caretPosition = control.getSelection().x;
			rangeSelected = true;
		}

		String textBefore = control.getText();

		// allegmeine Variablen
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
		if (insertion.matches("([0-9]*)|([\\" + dfs.getGroupingSeparator() + dfs.getDecimalSeparator() + "]*)|([\\-+]*)")) {
			Result r = new Result();

			try {
				r = processInput(insertion, start, end, keyCode, decimals, locale, caretPosition, textBefore, dfs, rangeSelected);
			} catch (Exception exception) {
				NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
				r.value = newValue("0", false, field.getDataType(), dfs);
				r.text = getValueString(numberFormat, field.getDataType(), r.value);
				r.caretPosition = 1;
			}

			verificationActive = true;
			field.setValue(r.value, true);
			control.setText(r.text);
			control.setSelection(r.caretPosition);
		}
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
			DecimalFormatSymbols dfs, boolean rangeSelected) {
		Result result = new Result();
		String text;
		boolean doit = true;
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		numberFormat.setMaximumFractionDigits(decimals);
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setGroupingUsed(true);
		StringBuilder sb = new StringBuilder();
		int originalStart = start;
		int originalEnd = end;
		boolean negative = false;
		String originalInsertion = insertion;
		String originalTextBefore = textBefore;

		// Löschen von Dezimal- und Grupierungstrennzeichen abfangen
		if (!textBefore.isEmpty() && start + 1 == end) {
			doit = shouldSymbolBeDeleted(keyCode, caretPosition, textBefore, dfs, decimals);
		} else if (!textBefore.isEmpty() && !insertion.isEmpty() && dfs.getDecimalSeparator() == insertion.charAt(0)) {
			doit = false;
		}

		// Prüfen ob die Eingabe ein Minus enthält oder die Zahl im Field negativ ist
		// negativ = true Zahl bleibt negativ oder wird negativ gesetzt am Ende
		if (insertion.contains("-") || textBefore.contains("-")) {
			if (!insertion.contains("+") && getCharacterForDeletion(keyCode, textBefore, caretPosition) != '-') {
				negative = true;
			}
		}

		if (doit) {

			// textBefore von überflüssigen Zeichen befreien
			int position = 0;
			for (char c : textBefore.toCharArray()) {
				if ((c >= '0' && c <= '9') || (c == dfs.getDecimalSeparator())) {
					sb.append(c);
				} else {
					// wir entfernen das Zeichen
					if (start > position) {
						start--;
					}
					if (end > position) {
						end--;
					}
					position--; // wird am Ende der Schleife wieder hochgezählt
				}
				position++;
			}
			textBefore = sb.toString();

			// insertion von überflüssigen Zeichen befreien
			insertion = insertion.replaceAll("[\\" + dfs.getGroupingSeparator() + "]|[\\-+]", "");

			if (start != end) {
				// wir müssen etwas herausschneiden
				text = textBefore.substring(0, start) + textBefore.substring(end);
			} else {
				text = textBefore;
			}

			if (insertion.length() > 0) {
				// die Zahl vor dem Komma ist nur 0 und wird durch die Eingabe ersetzt
				if (!textBefore.isEmpty() && textBefore.charAt(0) == '0' && caretPosition <= 1) {
					// 0 in Integer Feldern ersetzten
					if (decimals == 0) {
						text = insertion;
						// 0 in Double/ BigDecimal Feldern ersetzen
					} else {
						text = insertion + textBefore.substring(1);
					}
					// wir müssen etwas einfügen
				} else {
					text = text.substring(0, start) + insertion + text.substring(start);
				}
			}

			// text auf dezimal Trennzeichen prüfen
			if (text.contains("" + dfs.getDecimalSeparator())) {
				int decimalOverLength = text.substring(text.lastIndexOf(dfs.getDecimalSeparator()) + 1).length() - decimals;
				// schneidet den dezimal Bereich auf die angebene dezimal Länge
				if (!textBefore.isEmpty() && 0 < decimalOverLength) {
					text = text.substring(0, text.length() - decimalOverLength);
				}
			}
		} else {
			// vorherige Zahl von Gruppierungsseperatoren bereinigen
			textBefore = textBefore.replaceAll("[\\" + dfs.getGroupingSeparator() + "]", "");
			text = textBefore;
			negative = false;
		}

		try {
			result.value = newValue(text, negative, field.getDataType(), dfs);
			result.text = getValueString(numberFormat, field.getDataType(), result.value);
			result.caretPosition = getNewCaretPosition(result.text, textBefore, insertion, originalInsertion, originalTextBefore, keyCode, start, end,
					originalStart, originalEnd, decimals, caretPosition, numberFormat, dfs);
		} catch (NumberFormatException e) {
			result.value = null;
			result.text = "";
			result.caretPosition = 0;
		}

		return result;
	}

	/**
	 * Diese Methode liefert ein Value zurück, für den übergebenen DataType und Wert.
	 * 
	 * @param text
	 *            Wert aus dem das VAlue gebildet werden soll
	 * @param negative
	 *            true - negative Zahl
	 * @param type
	 *            DataType des Fields
	 * @param dfs
	 *            DecimalFormatSymbols
	 * @return Value für den entsprechenden DataType
	 */
	private Value newValue(String text, boolean negative, DataType type, DecimalFormatSymbols dfs) {
		Value value = null;

		switch (type) {
		case INTEGER:
			if (negative) {
				value = new Value(Integer.parseInt(text) * -1);
			} else {
				value = new Value(Integer.parseInt(text));
			}
			break;
		case DOUBLE:
		case BIGDECIMAL:
			if (negative) {
				value = new Value(Double.parseDouble(text.replace(dfs.getDecimalSeparator(), '.')) * -1, type);
			} else {
				value = new Value(Double.parseDouble(text.replace(dfs.getDecimalSeparator(), '.')), type);
			}
			break;
		default:
			break;
		}

		return value;
	}

	/**
	 * Gibt den String des übergebenen Values für den richtigen DataType zurück
	 * 
	 * @param format
	 *            NumberFormat für die Formatierung
	 * @param type
	 *            DataType des Fields
	 * @param value
	 *            das Value das zum String werden soll
	 * @return String des übergebeben Values
	 */
	private String getValueString(NumberFormat format, DataType type, Value value) {
		switch (type) {
		case INTEGER:
			return format.format(value.getIntegerValue());
		case DOUBLE:
			return format.format(value.getDoubleValue());
		case BIGDECIMAL:
			return format.format(value.getBigDecimalValue());
		default:
			return null;
		}
	}

	/**
	 * Diese Methode liefert "true" zurück, wenn beim Löschen ein Dezimal- oder Gruppierungstrennzeichen entfernt werden soll. Dabei wird auf die verschiedenen
	 * Löscharten geachtet und entsprechend die zu Löschende Position ermittelt.
	 * 
	 * @param keyCode,
	 *            gibt an welcher Key auf der Tastatur gedrückt wurde
	 * @param caretPosition
	 *            position im Feld
	 * @param text
	 *            Einhalt des Feldes
	 * @param dfs,
	 *            liefert die benötigten Trennzeichen nach angegebenen Locale
	 * @param decimals,
	 *            Anzahl an Dezimalstellen
	 * @return boolean, true = es soll ein Dezimal- oder Gruppierungstrennzeichen gelöscht
	 */
	private boolean shouldSymbolBeDeleted(int keyCode, int caretPosition, String text, DecimalFormatSymbols dfs, int decimals) {
		switch (keyCode) {
		case SWT.BS:
			if (decimals > 0 && text.charAt(caretPosition - 1) == dfs.getDecimalSeparator()) {
				return false;
			}
			return true;
		case SWT.DEL:
			if (text.charAt(caretPosition) == dfs.getDecimalSeparator() || text.charAt(caretPosition) == dfs.getGroupingSeparator()) {
				return false;
			}
			return true;
		default:
			return true;
		}
	}

	private Character getCharacterForDeletion(int keyCode, String text, int caretPosition) {
		Character character;
		// Charakter beim Löschen mit der Backspace Taste
		if (keyCode == SWT.BS && caretPosition != 0) {
			character = text.charAt(caretPosition - 1);
			// Charakter beim Entfernen mit der Entfernen Taste
		} else {
			character = text.charAt(caretPosition);
		}
		return character;
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
	public int getNewCaretPosition(String text, String textBefore, String insertion, String originalInsertion, String originalTextBefore, int keyCode,
			int start, int end, int ostart, int oend, int decimals, int caretPosition, NumberFormat numberFormat, DecimalFormatSymbols dfs) {

		if (!textBefore.isEmpty()) {
			textBefore = numberFormat.format(Double.parseDouble(textBefore.replace(dfs.getDecimalSeparator(), '.')));
		}

		int newCaretPosition = 1;
		String formatted0 = numberFormat.format(0); // stellt die formattierte Zahl 0 mit den jeweiligen dezimal Stellen dar
		int decimalCaretPostion = text.length() - decimals; // ermittelt die Caret Postion nach dem dezimal Trennzeichen
		int countGroupingSeperator = getGroupingSeperatorCount(text, dfs) - getGroupingSeperatorCount(textBefore, dfs);

		// Wenn mit Backspace gelöscht wird
		if (keyCode == SWT.BS) {
			if (textBefore.length() - decimals <= caretPosition || countGroupingSeperator == 0) {
				newCaretPosition = caretPosition - 1;
			} else {
				newCaretPosition = start + 1 + countGroupingSeperator + getGroupingSeperatorCount(text, dfs);
			}
		}
		// Wenn mit ENTF gelöscht wird
		else if (SWT.DEL == keyCode) {
			if ((originalTextBefore.charAt(caretPosition) == dfs.getGroupingSeparator() || originalTextBefore.charAt(caretPosition) == dfs.getDecimalSeparator()
					|| (text.length() == originalTextBefore.length()) && caretPosition < decimalCaretPostion)) {
				newCaretPosition = caretPosition + 1;
			} else if (countGroupingSeperator == 0 || getCharacterForDeletion(keyCode, originalTextBefore, caretPosition) == '-') {
				newCaretPosition = caretPosition;
			} else {
				newCaretPosition = caretPosition + countGroupingSeperator + getGroupingSeperatorCount(textBefore.substring(ostart, oend), dfs);
			}
		} else if (originalInsertion.contains("-") && !originalTextBefore.contains("-")) {
			newCaretPosition = caretPosition + 1;
		} else if (originalInsertion.contains("+") && originalTextBefore.contains("-")) {
			newCaretPosition = caretPosition - 1;
		} else if (!insertion.isBlank() && insertion.charAt(0) == dfs.getDecimalSeparator()) {
			newCaretPosition = decimalCaretPostion;
		} else {
			// Falls der vorherige Text leer oder 0 ist oder der neue Text 0 ist.
			if ((textBefore.equals(formatted0) || textBefore.isBlank() || text.equals(formatted0)) && caretPosition < decimalCaretPostion) {
				newCaretPosition = insertion.length() + countGroupingSeperator;
			}
			// Prüft ob man sich hinter dem dezimal Trennzeichen befindet
			else if (decimalCaretPostion <= caretPosition) {
				newCaretPosition = caretPosition + insertion.length();
				if (newCaretPosition >= text.length()) {
					newCaretPosition = newCaretPosition - (newCaretPosition - text.length());
				}
			} else {
				if (start != end) {
					newCaretPosition = start + insertion.length() + getGroupingSeperatorCount(text, dfs);
				} else if (text.length() == textBefore.length() + insertion.length()) {
					newCaretPosition = caretPosition + insertion.length();
				} else {
					newCaretPosition = caretPosition + insertion.length() + countGroupingSeperator;
				}
			}
		}

		return newCaretPosition;

	}

	/**
	 * Zählt die GroupingSeperator im übergebenen String.
	 *
	 * @param text
	 * @return Anzahl an GroupingSeperatoren
	 */
	private int getGroupingSeperatorCount(String text, DecimalFormatSymbols dfs) {
		int groupingSeperatorCount = 0;
		if (text != null && dfs != null) {
			for (Character gs : text.toCharArray()) {
				if (dfs.getGroupingSeparator() == gs) {
					groupingSeperatorCount++;
				}
			}
		}
		return groupingSeperatorCount;
	}
}
