package aero.minova.rcp.rcp.util;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

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
	public static Object getNumberObjectFromString(String text, DataType type, DecimalFormatSymbols dfs) {
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
		return entry.replaceAll("[\\" + dfs.getGroupingSeparator() + "]", "");
	}

	public static String[] splitNumberUnitEntry(String entry, MField field) {
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
