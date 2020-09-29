package aero.minova.rcp.plugin1.model.builder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.plugin1.model.Value;

public class ValueBuilder {
	private Object translatedValue;
	private DateTimeFormatter dtfDate;
	private DateTimeFormatter dtfHour;
	private DataType dataType;

	private ValueBuilder(Value v) {
		dtfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
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
			ZonedDateTime zoned = v.getInstantValue().atZone(ZoneId.systemDefault());
			if (zoned.getHour() != 0) {
				translatedValue = dtfHour.format(zoned);
			} else {
				translatedValue = dtfDate.format(zoned);
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

	public static ValueBuilder newValue(Value v) {
		return new ValueBuilder(v);
	}

	public DataType dataType() {
		return dataType;
	}

	public Object create() {
		return translatedValue;
	}
}
