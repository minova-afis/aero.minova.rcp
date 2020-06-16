package aero.minova.rcp.plugin1.model;

import java.time.Instant;
import java.time.ZonedDateTime;

public class Value {
	public static enum Type {
		INTEGER, DOUBLE, STRING, INSTANT, ZONED
	};

	private final Type type;
	private final Integer integerValue;
	private final Double doubleValue;
	private final String stringValue;
	private final Instant instantValue;
	private final ZonedDateTime zonedDateTimeValue;

	public Value(Integer integerValue) {
		this(Type.INTEGER, integerValue, null, null, null, null);
	}

	public Value(Double doubleValue) {
		this(Type.DOUBLE, null, doubleValue, null, null, null);
	}

	public Value(String stringValue) {
		this(Type.STRING, null, null, stringValue, null, null);
	}
	
	public Value(Instant instantValue) {
		this(Type.INSTANT, null, null, null, instantValue, null);
	}
	
	public Value(ZonedDateTime zonedDateTimeValue) {
		this(Type.ZONED, null, null, null, null, zonedDateTimeValue);
	}
	
	public Value(Type type, Integer integerValue, Double doubleValue, String stringValue, Instant instantValue, ZonedDateTime zonedDateTimeValue) {
		this.type = type;
		if (type == Type.INTEGER)
			this.integerValue = integerValue;
		else
			this.integerValue = null;
		if (type == Type.DOUBLE)
			this.doubleValue = doubleValue;
		else
			this.doubleValue = null;
		if (type == Type.STRING)
			this.stringValue = stringValue;
		else
			this.stringValue = null;
		if (type == Type.INSTANT)
			this.instantValue = instantValue;
		else
			this.instantValue = null;
		if (type == Type.ZONED)
			this.zonedDateTimeValue = zonedDateTimeValue;
		else
			this.zonedDateTimeValue = null;
	}

	public Object getValue() {
		switch (type) {
		case INTEGER:
			return integerValue;
		case DOUBLE:
			return doubleValue;
		case STRING:
			return stringValue;
		case INSTANT:
			return instantValue;
		case ZONED:
			return zonedDateTimeValue;
		default:
			return null; // kein Type definiert - eigentlich nicht möglich
		}
	}
	
	public Type getType() {
		return type;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public Instant getInstantValue() {
		return instantValue;
	}

	public ZonedDateTime getZonedDateTimeValue() {
		return zonedDateTimeValue;
	}
}
