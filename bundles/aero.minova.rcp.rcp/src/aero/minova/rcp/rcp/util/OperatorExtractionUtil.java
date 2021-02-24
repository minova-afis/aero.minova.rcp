package aero.minova.rcp.rcp.util;

import java.util.Locale;

public class OperatorExtractionUtil {

	/**
	 * Wenn es einen Operator gibt, dann liefert die Funktion den Index bis zu dem sich der Operator erstreckt
	 *
	 * @param value
	 * @return 0, wenn es keinen Operator gibt
	 */

	public static int getOperatorEndIndex(String value) {

		if (value == null || value.length() == 0) {
			return 0;
		}

		// Wir simulieren einen ltrim, um die Anfangsposition des Operators festzustellen

		String tmp = (value + "_").trim();

		tmp = tmp.toLowerCase(Locale.ENGLISH).substring(0, tmp.length() - 1);

		final int shift = value.length() - tmp.length();

		for (final String sqlOperator : Constants.SQL_OPERATORS) {

			if (tmp.startsWith(sqlOperator)) {

				return shift + sqlOperator.length();

			}

		}

		return 0;

	}

}
