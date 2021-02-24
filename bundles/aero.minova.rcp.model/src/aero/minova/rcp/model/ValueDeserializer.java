package aero.minova.rcp.model;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZonedDateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ValueDeserializer implements JsonDeserializer<Value> {

	@Override
	public Value deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		return deserialize(json.getAsString());
	}

	public static Value deserialize(String valueText) {
		String typeString = valueText.substring(0, 1);
		String value = valueText.substring(2);
		switch (typeString) {
		case "n":
			return new Value(Integer.parseInt(value));
		case "d":
			return new Value(Double.parseDouble(value));
		case "s":
			return new Value(value);
		case "i":
			return new Value(Instant.parse(value));
		case "z":
			return new Value(ZonedDateTime.parse(value));
		case "b":
			return new Value(Boolean.valueOf(value));
		case "f": // Filter
			String operator = value.substring(0, value.indexOf("-"));
			String v = value.substring(value.indexOf("-") + 1);
			return new FilterValue(operator, deserialize(v).getValue());
		default:
			break;
		}
		return null;
	}

}
