package aero.minova.rcp.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;

import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.util.DateTimeUtil;
import aero.minova.rcp.util.DateUtil;
import aero.minova.rcp.util.TimeUtil;

public class Value implements Serializable {
	private static final long serialVersionUID = 202011291518L;

	private final DataType type;
	private final Object value;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.DATE_UTIL)
	String datePattern;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIME_UTIL)
	String timePattern;

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
			throw new RuntimeException("Class " + valueNew.getClass() + " not supported for Value");
		}
		this.value = valueNew;
	}

	public String getValueString(Locale locale) {
		String returnValue = "";
		switch (type) {
		case DOUBLE:
		case STRING:
		case BOOLEAN:
		case INTEGER:
		case BIGDECIMAL:
			returnValue += value;
			break;
		case ZONED:
			ZonedDateTime z = (ZonedDateTime) value;
			Instant i = z.toInstant();
			if (z.getYear() == 1900 && z.getDayOfMonth() == 1 && z.getMonthValue() == 1) {
				return TimeUtil.getTimeString(i, locale, timePattern);
			}
			return DateTimeUtil.getDateTimeString(i, locale, datePattern, timePattern);
		case INSTANT:
			Instant i2 = (Instant) value;
			LocalDateTime d1 = LocalDateTime.ofEpochSecond(i2.getEpochSecond(), i2.getNano(), ZoneOffset.UTC);
			if (d1.getYear() == 1900 && d1.getDayOfMonth() == 1 && d1.getMonthValue() == 1) {
				return TimeUtil.getTimeString(i2, locale, timePattern);
			}
			return DateTimeUtil.getDateTimeString(i2, locale, datePattern, timePattern);
		default:
			break;
		}
		return returnValue;
	}

	public String getValueString(Locale locale, DateTimeType dateTimeType) {
		if (dateTimeType == null) {
			return getValueString(locale);
		}

		String returnValue = "";
		switch (type) {
		case ZONED:
			ZonedDateTime z = (ZonedDateTime) value;
			Instant i = z.toInstant();
			switch (dateTimeType) {
			case TIME:
				returnValue = TimeUtil.getTimeString(i, locale, timePattern);
				break;
			case DATE:
				returnValue = DateUtil.getDateString(i, locale, datePattern);
				break;
			case DATETIME:
				returnValue = DateTimeUtil.getDateTimeString(i, locale, datePattern, timePattern);
				break;
			}
			break;
		case INSTANT:
			Instant i2 = (Instant) value;
			switch (dateTimeType) {
			case TIME:
				returnValue = TimeUtil.getTimeString(i2, locale, timePattern);
				break;
			case DATE:
				returnValue = DateUtil.getDateString(i2, locale, datePattern);
				break;
			case DATETIME:
				returnValue = DateTimeUtil.getDateTimeString(i2, locale, datePattern, timePattern);
				break;
			}
			break;
		default:
			break;
		}
		return returnValue;
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

	public Value(FilterValue filterValue) {
		type = DataType.FILTER;
		value = filterValue;
	}

	public Object getValue() {
		return value;
	}

	public Value getThis() {
		return this;
	}

	public DataType getType() {
		return type;
	}

	public Integer getIntegerValue() {
		if (value instanceof LookupValue) {
			return ((LookupValue) value).getKeyLong();
		}
		return type == DataType.INTEGER ? (Integer) value : null;
	}

	public String getStringValue() {
		return type == DataType.STRING ? (String) value : null;
	}

	public Double getDoubleValue() {
		return type == DataType.DOUBLE ? (Double) value : null;
	}

	public Double getBigDecimalValue() {
		return type == DataType.BIGDECIMAL ? (Double) value : null;
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

	public String getOperatorValue() {
		return type == DataType.FILTER ? (String) value : null;
	}

	public String getReferenceValue() {
		return type == DataType.REFERENCE ? (String) value : null;
	}

	@Override
	public String toString() {
		return "Value [type=" + type + ", value=" + value + "]";
	}

	@Override
	public boolean equals(Object obj) {
		Value v = null;
		if (obj instanceof Value) {
			v = (Value) obj;
		}
		if (v == null) {
			return false; // wir sind gesetzt und vergleichen mit null -> false
		}

		if (v.getValue() instanceof LookupValue && value instanceof Integer) {
			return ((LookupValue) v.getValue()).getKeyLong().equals(value);
		}
		if (value instanceof LookupValue && v.getValue() instanceof Integer) {
			return ((LookupValue) value).getKeyLong().equals(v.getValue());
		}

		if (value == null && v.value != null) {
			return false;
		} else if (value == null && v.value == null) {
			return this.type == v.type && this.getClass().equals(v.getClass());
		}

		return (this.type == v.type && this.value.equals(v.value) && this.getClass().equals(v.getClass()));
	}
}
