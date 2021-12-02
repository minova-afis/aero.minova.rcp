package aero.minova.rcp.model;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZonedDateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import aero.minova.rcp.constants.Constants;

public class ValueDeserializer implements JsonDeserializer<Value> {

	private boolean useUserValues;

	public ValueDeserializer() {
		this(false);
	}

	public ValueDeserializer(boolean useUserValues) {
		this.useUserValues = useUserValues;
	}

	@Override
	public Value deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		return deserialize(json.getAsString(), useUserValues);
	}

	public static Value deserialize(String valueText) {
		return deserialize(valueText, false);
	}

	public static Value deserialize(String valueText, boolean useUserValues) {
		String typeString = valueText.substring(0, 1);
		String value = valueText.substring(2);
		switch (typeString) {
		case "n":
			return new Value(Integer.parseInt(value), DataType.INTEGER);
		case "d":
			return new Value(Double.parseDouble(value), DataType.DOUBLE);
		case "m":
			return new Value(Double.parseDouble(value), DataType.BIGDECIMAL);
		case "s":
			return new Value(value, DataType.STRING);
		case "i":
			return new Value(Instant.parse(value), DataType.INSTANT);
		case "z":
			return new Value(ZonedDateTime.parse(value), DataType.ZONED);
		case "b":
			return new Value(Boolean.valueOf(value), DataType.BOOLEAN);
		case "f": // Filter
			String operator = value.substring(0, value.indexOf("-"));
			String v = value.substring(value.indexOf("-") + 1);
			if (useUserValues) {
				String userInput = v.substring(v.indexOf(Constants.SOH) + 1);
				v = v.substring(0, v.indexOf(Constants.SOH));
				return new FilterValue(operator, deserialize(v).getValue(), userInput);
			} else {
				return new FilterValue(operator, deserialize(v).getValue(), "");
			}
		case "r":
			String[] split = value.split("-");
			String referenceID = split[0];
			int rowNumber = Integer.parseInt(split[1]);
			String columnName = split[2]; // Es kann "-" im Columnname enthalten sein
			for (int i = 3; i < split.length; i++) {
				columnName += "-" + split[i];
			}

			return new ReferenceValue(referenceID, rowNumber, columnName);
		default:
			System.err.println("Value mit prefix " + typeString + " nicht bekannt");
			break;
		}
		return null;
	}

}
