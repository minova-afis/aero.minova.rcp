package aero.minova.rcp.model.builder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;

public class ValueBuilder {
	private Object translatedValue;
	static private DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private DateTimeFormatter dtfHour;
	private DataType dataType;

	private ValueBuilder(Value v, Field f) {
		dtfHour = DateTimeFormatter.ofPattern("hh:mm");
		if (v.getBooleanValue() != null) {
			translatedValue = v.getBooleanValue();
			dataType = DataType.BOOLEAN;
		} else if (v.getZonedDateTimeValue() != null) {
			if (v.getZonedDateTimeValue().getHour() != 0) {
				translatedValue = dtfHour.format(v.getZonedDateTimeValue());
			} else {
				translatedValue = dtfDate.format(v.getZonedDateTimeValue());
			}
			dataType = DataType.ZONED;
		} else if (v.getInstantValue() != null) {
			ZonedDateTime zonedDate = v.getInstantValue().atZone(ZoneId.systemDefault());
			if (f != null && (f.getShortDate() != null || f.getLongDate() != null)) {
				translatedValue = dtfDate.format(zonedDate);

			} else {
				translatedValue = dtfHour.format(zonedDate);
			}
			dataType = DataType.INSTANT;
		} else if (v.getDoubleValue() != null) {
			translatedValue = v.getDoubleValue().toString();
			dataType = DataType.DOUBLE;
		} else if (v.getIntegerValue() != null) {
			translatedValue = v.getIntegerValue();
			dataType = DataType.INTEGER;
		} else if (v.getStringValue() != null) {
			translatedValue = v.getStringValue();
			dataType = DataType.STRING;
		}
	}

	// Aufruf alleine anhand des Fields dient nur dem Auslesen des DataTypes
	private ValueBuilder(Field f) {
		dtfHour = DateTimeFormatter.ofPattern("hh:mm");
		if (f.getBoolean() != null) {
			dataType = DataType.BOOLEAN;
		} else if (f.getBignumber() != null || f.getNumber() != null) {
			dataType = DataType.INTEGER;
		} else if (f.getDateTime() != null || f.getLongDate() != null || f.getShortDate() != null) {
			dataType = DataType.ZONED;
		} else if (f.getShortTime() != null || f.getLongTime() != null) {
			dataType = DataType.INSTANT;
		} else if (f.getMoney() != null) {
			dataType = DataType.DOUBLE;
		} else if (f.getText() != null) {
			dataType = DataType.STRING;
		}
	}

	public static ValueBuilder value(Value v) {
		return new ValueBuilder(v, null);
	}

	public static ValueBuilder value(Value v, Field f) {
		return new ValueBuilder(v, f);
	}

	public static ValueBuilder value(Field f) {
		return new ValueBuilder(f);
	}

	public DataType getDataType() {
		return dataType;
	}

	public Object create() {
		return translatedValue;
	}

	public String getText() {
		return translatedValue.toString();
	}
}
