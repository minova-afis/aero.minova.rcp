package aero.minova.rcp.model;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import aero.minova.rcp.constants.Constants;

public class ValueSerializer implements JsonSerializer<Value> {

	private boolean useUserValues;

	public ValueSerializer() {
		this(false);
	}

	public ValueSerializer(boolean useUserValues) {
		this.useUserValues = useUserValues;
	}

	@Override
	public JsonElement serialize(Value value, Type type, JsonSerializationContext context) {
		return serialize(value, useUserValues);
	}

	public static JsonElement serialize(Value value) {
		return serialize(value, false);
	}

	public static JsonElement serialize(Value value, boolean useUserValues) {
		if (value == null || value.getValue() == null) {
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
		case BIGDECIMAL:
			return new JsonPrimitive("m-" + value.getBigDecimalValue().toString());
		case FILTER:
			if (useUserValues) {
				return new JsonPrimitive("f-" + value.getOperatorValue() + "-" + serialize(((FilterValue) value).getFilterValue()).getAsString() + Constants.SOH
						+ ((FilterValue) value).getUserInput());
			} else {
				if (((FilterValue) value).getFilterValue() == null)
					return new JsonPrimitive("f-" + value.getOperatorValue());
				return new JsonPrimitive("f-" + value.getOperatorValue() + "-" + serialize(((FilterValue) value).getFilterValue()).getAsString());
			}
		case REFERENCE:
			ReferenceValue rv = (ReferenceValue) value;
			return new JsonPrimitive("r-" + rv.getReferenceValue() + "-" + rv.getRowNumber() + "-" + rv.getColumnName());
		case PERIOD:
			PeriodValue pv = (PeriodValue) value;
			JsonObject o = new JsonObject();
			o.add("base", serialize(new Value(pv.getBaseValue())));
			o.add("userInput", pv.getUserInput() != null ? new JsonPrimitive(pv.getUserInput()) : JsonNull.INSTANCE);
			o.add("due", serialize(pv.getDueDate()));
			return new JsonPrimitive("s-" + o.toString());
		default:
			System.err.println("Value " + value.getType() + " nicht bekannt (ValueSerializer)");
			return null;
		}
	}

}
