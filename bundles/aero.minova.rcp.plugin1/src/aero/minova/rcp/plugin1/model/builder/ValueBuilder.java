package aero.minova.rcp.plugin1.model.builder;

import java.time.format.DateTimeFormatter;

import aero.minova.rcp.plugin1.model.Value;

public class ValueBuilder {
	private Object translatedValue;
	private DateTimeFormatter dtfDate;
	private DateTimeFormatter dtfHour;

	private ValueBuilder(Value v) {
		dtfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		dtfHour = DateTimeFormatter.ofPattern("hh:mm");

		if (v.getBooleanValue() != null) {
			translatedValue = v.getBooleanValue();
		} else if (v.getZonedDateTimeValue() != null) {
			if (v.getZonedDateTimeValue().getHour() != 0) {
				translatedValue = dtfHour.format(v.getZonedDateTimeValue());
			} else {
				translatedValue = dtfDate.format(v.getZonedDateTimeValue());
			}
		} else if (v.getInstantValue() != null) {
			translatedValue = v.getInstantValue().toString();
		} else if (v.getDoubleValue() != null) {
			translatedValue = v.getDoubleValue().toString();
		} else if (v.getIntegerValue() != null) {
			translatedValue = v.getIntegerValue();
		} else if (v.getStringValue() != null) {
			translatedValue = v.getStringValue();
		}
	}

	public static ValueBuilder newValue(Value v) {
		return new ValueBuilder(v);
	}

	public Object create() {
		return translatedValue;
	}
}
