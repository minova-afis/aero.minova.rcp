package aero.minova.rcp.model;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ValueSerializer implements JsonSerializer<Value> {

	@Override
	public JsonElement serialize(Value value, Type type, JsonSerializationContext context) {
		if (value.getValue() == null) {
			return null;
		}
		switch (value.getType()) {
		case INTEGER:
			return new JsonPrimitive("n-" + value.getIntegerValue());
		case DOUBLE:
			return new JsonPrimitive("d-" + value.getDoubleValue());
		case STRING:
			return new JsonPrimitive("s-" + value.getStringValue());
		case INSTANT:
			return new JsonPrimitive("i-" + value.getInstantValue().toString());
		case ZONED:
			return new JsonPrimitive("z-" + value.getZonedDateTimeValue().toString());
		case BOOLEAN:
			return new JsonPrimitive("b-" + value.getBooleanValue().toString());
		default:
			return null;
		}
	}

}
