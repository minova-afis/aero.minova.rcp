package aero.minova.rcp.model;

import java.time.Instant;
import java.time.ZonedDateTime;

public class Value {

	private final DataType type;
	private final Object value;

	public Value(Object valueNew) {
		if (valueNew instanceof Integer) {
			type = DataType.INTEGER;
		} else if (valueNew instanceof Boolean) {
			type = DataType.BOOLEAN;
		} else if (valueNew instanceof Double || valueNew instanceof Float) {
			type = DataType.DOUBLE;
		} else if (valueNew instanceof String) {
			type = DataType.STRING;
		} else if (valueNew instanceof Instant) {
			type = DataType.INSTANT;
		} else if (valueNew instanceof ZonedDateTime) {
			type = DataType.ZONED;
		} else {
			throw new RuntimeException();
		}
		this.value = valueNew;
	}

	public Value(Object valueNew, DataType dataType) {
		type = dataType;
		value = valueNew;
	}

	public Value(Integer integerValue) {
		type = DataType.INTEGER;
		value = integerValue;
	}

	public Value(Boolean booleanValue) {
		type = DataType.BOOLEAN;
		value = booleanValue;
	}

	public Value(Double doubleValue) {
		type = DataType.DOUBLE;
		value = doubleValue;
	}

	public Value(String stringValue) {
		type = DataType.STRING;
		value = stringValue;
	}

	public Value(Instant instantValue) {
		type = DataType.INSTANT;
		value = instantValue;
	}

	public Value(ZonedDateTime zonedDateTimeValue) {
		type = DataType.ZONED;
		value = zonedDateTimeValue;
	}

	public Object getValue() {
		return value;
	}

	public DataType getType() {
		return type;
	}

	public Integer getIntegerValue() {
		return type == DataType.INTEGER ? (Integer) value : null;
	}

	public String getStringValue() {
		return type == DataType.STRING ? (String) value : null;
	}

	public Double getDoubleValue() {
		return type == DataType.DOUBLE ? (Double) value : null;
	}

	public Instant getInstantValue() {
		return type == DataType.INSTANT ? (Instant) value : null;
	}

	public ZonedDateTime getZonedDateTimeValue() {
		return type == DataType.ZONED ? (ZonedDateTime) value : null;
	}

	public Boolean getBooleanValue() {
		return type == DataType.BOOLEAN ? (Boolean) value : null;
	}

	@Override
	public String toString() {
		return "Value [type=" + type + ", value=" + value + "]";
	}

}
