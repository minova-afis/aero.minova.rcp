package aero.minova.rcp.rcp.util;

import java.time.Instant;
import java.time.ZonedDateTime;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;

public class StringToValueUtil {

	private StringToValueUtil() {}

	public static Value stringToValue(String valueString, DataType dataType) {
		switch (dataType) {
		case STRING:
			return new Value(valueString);
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
			return new Value(Instant.parse(valueString), DataType.INSTANT);
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
		}
		return null;
	}
}
