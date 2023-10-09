package aero.minova.rcp.rcp.util;

import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import org.eclipse.e4.core.services.translation.TranslationService;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;
import aero.minova.rcp.model.QuantityValue;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.util.NumberFormatUtil;
import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.util.DateTimeUtil;
import aero.minova.rcp.util.DateUtil;
import aero.minova.rcp.util.TimeUtil;

/**
 * Mit dieser Klassen können die statischen/festen Strings aus der XBS in Values umgewandelt werden (siehe #1357)
 * 
 * @author janiak
 */
public class StaticXBSValueUtil {

	private StaticXBSValueUtil() {}

	/**
	 * Erstellt einen Value mit gegebenen Datentyp aus einem String. Sonderfälle:
	 * <li>String-Datentyp beginnend mit "@": wird übersetzt</li>
	 * <li>Für Datums-/Zeitvalues können bekannte Abkürzungen verwendet werden ("0", "1212", ...)</li>
	 * 
	 * @param valueString
	 * @param dataType
	 * @return
	 */
	public static Value stringToValue(String valueString, DataType dataType, DateTimeType dateTimeType, TranslationService translationService,
			String timezone) {

		switch (dataType) {
		case STRING:
			return new Value(translationService.translate(valueString, null));
		case BIGDECIMAL:
			return new Value(Double.valueOf(valueString), DataType.BIGDECIMAL);
		case BOOLEAN:
			return new Value(Boolean.valueOf(valueString), DataType.BOOLEAN);
		case DOUBLE:
			return new Value(Double.valueOf(valueString), DataType.BIGDECIMAL);
		case FILTER:
			// Sollte nicht vorkommen
			break;
		case INSTANT:

			try {
				return new Value(Instant.parse(valueString), DataType.INSTANT);
			} catch (DateTimeParseException e) {
				// Versuchen, Abkürzung zu parsen
			}

			switch (dateTimeType) {
			case DATE:
				return new Value(DateUtil.getDate(valueString), DataType.INSTANT);
			case DATETIME:
				return new Value(DateTimeUtil.getDateTime(valueString, timezone), DataType.INSTANT);
			case TIME:
				return new Value(TimeUtil.getTime(valueString), DataType.INSTANT);
			}
			return null;
		case INTEGER:
			return new Value(Integer.valueOf(valueString), DataType.INTEGER);
		case PERIOD:
			// Sollte nicht vorkommen
			break;
		case REFERENCE:
			// Sollte nicht vorkommen
			break;
		case ZONED:
			return new Value(ZonedDateTime.parse(valueString), DataType.ZONED);
		case QUANTITY:
			DecimalFormatSymbols dfs = new DecimalFormatSymbols(CustomLocale.getLocale());
			String[] numberAndUnit = NumberFormatUtil.splitNumberUnitEntry(valueString);
			return new QuantityValue(numberAndUnit[0], numberAndUnit[1], DataType.QUANTITY, dfs);
		}
		return null;
	}
}
