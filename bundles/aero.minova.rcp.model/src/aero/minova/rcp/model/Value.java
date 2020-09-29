package aero.minova.rcp.model;

import java.time.Instant;
import java.time.ZonedDateTime;

public class Value {

	private final DataType type;
	private final Object value;

	// TODO Werte sollten nicht null sein!

	public Value(Object value) {
		if (value instanceof Integer) {
			type = DataType.INTEGER;
		} else if (value instanceof Boolean) {
			type = DataType.BOOLEAN;
		} else if (value instanceof Double || value instanceof Float) {
			type = DataType.DOUBLE;
		} else if (value instanceof String) {
			type = DataType.STRING;
		} else if (value instanceof Instant) {
			type = DataType.INSTANT;
		} else if (value instanceof ZonedDateTime) {
			type = DataType.ZONED;
		} else {
			//TODO
			throw new RuntimeException();
		}
		this.value = value;
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
}
