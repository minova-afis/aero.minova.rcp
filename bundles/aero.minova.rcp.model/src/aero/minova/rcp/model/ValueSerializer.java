package aero.minova.rcp.model;

import java.lang.reflect.Type;
import java.time.Instant;

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
			if (value != null && value.getType().equals(DataType.PERIOD)) { // Bei PeriodValue kann (base)Value null sein, aber dueDate trotzdem gesetzt
				return new JsonPrimitive("s-" + serializePeriodValue((PeriodValue) value).toString());
			}
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
			return new JsonPrimitive("s-" + serializePeriodValue((PeriodValue) value).toString());
		default:
			System.err.println("Value " + value.getType() + " nicht bekannt (ValueSerializer)");
			return null;
		}
	}

	private static JsonObject serializePeriodValue(PeriodValue pv) {
		Instant due = pv.getDueDate() != null ? pv.getDueDate().getInstantValue() : null;

		JsonObject o = new JsonObject();
		o.add("base", pv.getBaseValue() != null ? new JsonPrimitive(pv.getBaseValue().toString()) : JsonNull.INSTANCE);
		o.add("userInput", pv.getUserInput() != null ? new JsonPrimitive(pv.getUserInput()) : JsonNull.INSTANCE);
		o.add("due", due != null ? new JsonPrimitive(due.toString()) : JsonNull.INSTANCE);
		return o;
	}

}
