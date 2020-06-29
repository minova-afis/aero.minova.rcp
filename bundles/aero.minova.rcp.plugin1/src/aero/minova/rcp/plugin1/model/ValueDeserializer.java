package aero.minova.rcp.plugin1.model;

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
		String typeString = json.getAsString().substring(0, 1);
		String value = json.getAsString().substring(2);
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
		default:
			break;
		}
		return null;
	}

}
