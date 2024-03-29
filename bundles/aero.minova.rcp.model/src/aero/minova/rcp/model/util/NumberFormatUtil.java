package aero.minova.rcp.model.util;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;

public class NumberFormatUtil {

	public static class Result {
		public String text;
		public Value value;
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
	 * @param textBefore
	 *            der Text, der aktuell (vor Verarbeitung dieses Events) im Feld steht ({@link Text#getText()})
	 * @param decimalFormatSymbols
	 *            {@link DecimalFormatSymbols} des aktuellen locale
	 * @return
	 */
	public static Result processInput(MField field, String insertion, int start, int end, int keyCode, int decimals, Locale locale, String textBefore,
			DecimalFormatSymbols dfs, boolean rangeSelected) {
		Result result = new Result();
		String text;
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		numberFormat.setMaximumFractionDigits(decimals);
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setGroupingUsed(true);
		StringBuilder sb = new StringBuilder();

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
		insertion = insertion.replaceAll("[\\" + dfs.getGroupingSeparator() + "]", "");

		if (start != end) {
			// wir müssen etwas herausschneiden
			text = textBefore.substring(0, start) + textBefore.substring(end);
		} else {
			text = textBefore;
		}

		if (insertion.length() > 0) {
			// wir müssen etwas einfügen
			text = text.substring(0, start) + insertion + text.substring(start);
		}

		// text auf dezimal Trennzeichen prüfen
		if (text.contains("" + dfs.getDecimalSeparator())) {
			int decimalOverLength = text.substring(text.lastIndexOf(dfs.getDecimalSeparator()) + 1).length() - decimals;
			// schneidet den dezimal Bereich auf die angebene dezimal Länge
			if (!textBefore.isEmpty() && 0 < decimalOverLength) {
				text = text.substring(0, text.length() - decimalOverLength);
			}
		}

		if (decimals > 0) {
			try {
				result.value = new Value(Double.parseDouble(text.replace(dfs.getDecimalSeparator(), '.')), field.getDataType());
				if (field.getDataType().equals(DataType.BIGDECIMAL)) {
					result.text = numberFormat.format(result.value.getBigDecimalValue());
				} else {
					result.text = numberFormat.format(result.value.getDoubleValue());
				}
			} catch (NumberFormatException e) {
				result.value = new Value(0.0, field.getDataType());
				if (field.getDataType().equals(DataType.BIGDECIMAL)) {
					result.text = numberFormat.format(result.value.getBigDecimalValue());
				} else {
					result.text = numberFormat.format(result.value.getDoubleValue());
				}
			}
		} else {
			try {
				result.value = new Value(Integer.parseInt(text));
				result.text = numberFormat.format(result.value.getIntegerValue());
			} catch (NumberFormatException e) {
				result.value = null;
				result.text = "";
			}
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
	public static Value newValue(String text, DataType type, DecimalFormatSymbols dfs) {
		Value value = null;

		switch (type) {
		case INTEGER:
			value = new Value(Integer.parseInt(text));
			break;
		case DOUBLE:
		case BIGDECIMAL:
			value = new Value(Double.parseDouble(text.replace(dfs.getDecimalSeparator(), '.')), type);
			break;
		default:
			break;
		}

		return value;
	}

	/**
	 * Diese Methode liefert ein Object zurück, für den übergebenen String.
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
	public static Number getNumberObjectFromString(String text, DataType type, DecimalFormatSymbols dfs) {
		switch (type) {
		case INTEGER:
			return Integer.parseInt(text);
		case DOUBLE:
		case BIGDECIMAL:
			return Double.parseDouble(text.replace(dfs.getDecimalSeparator(), '.'));
		default:
			return null;
		}
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
	public static String getValueString(NumberFormat format, DataType type, Value value) {
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

	public static String clearNumberFromGroupingSymbols(String entry, Locale locale) {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
		return entry.replaceAll("[\\s*" + dfs.getGroupingSeparator() + "]", "");
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
	public static int getNewCaretPosition(String text, String textBefore, String insertion, int keyCode, int start, int end, int ostart, int oend, int decimals,
			int caretPosition, NumberFormat numberFormat, DecimalFormatSymbols dfs) {

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
			if (textBefore.charAt(caretPosition) == dfs.getGroupingSeparator() || textBefore.charAt(caretPosition) == dfs.getDecimalSeparator()
					|| (text.length() == textBefore.length() && caretPosition < decimalCaretPostion)) {
				newCaretPosition = caretPosition + 1;
			} else if (countGroupingSeperator == 0) {
				newCaretPosition = caretPosition;
			} else {
				newCaretPosition = caretPosition + countGroupingSeperator + getGroupingSeperatorCount(textBefore.substring(ostart, oend), dfs);
			}
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
	private static int getGroupingSeperatorCount(String text, DecimalFormatSymbols dfs) {
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

	public static String[] splitNumberUnitEntry(String entry) {
		String numbers = entry;
		String unit = "";
		String[] numberAndUnit = new String[2];

		try {
			numbers = entry.substring(0, findFirstLetterPosition(entry));
			unit = entry.substring(findFirstLetterPosition(entry));
		} catch (Exception e) {
			// Nothing to handle
		}

		numberAndUnit[0] = numbers;
		numberAndUnit[1] = unit;

		return numberAndUnit;
	}

	public static int findFirstLetterPosition(String input) {
		for (int i = 0; i < input.length(); i++) {
			if (Character.isLetter(input.charAt(i))) {
				return i;
			}
		}
		return -1; // not found
	}

}
