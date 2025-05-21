package aero.minova.rcp.model.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONObject;

import aero.minova.rcp.model.DateTimeType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;

public class ParamJsonUtil {

	private ParamJsonUtil() {}

	public static String convertValuesToJson(List<MField> subMFields) {
		if (subMFields == null) {
			return null;
		}

		JSONObject jsonObj = new JSONObject();

		for (MField f : subMFields) {
			if (f.getValue() != null) {
				jsonObj.put(f.getName(), getValueAsString(f.getValue(), f.getDateTimeType()));
			}
		}

		return jsonObj.toString();

	}

	public static void convertJsonParameterToValues(String value, List<MField> subMFields) {

		JSONObject jsonObj = new JSONObject(value);

		for (MField f : subMFields) {
			Value v = null;

			if (jsonObj.has(f.getName())) {
				v = getValue(f, jsonObj.get(f.getName()));
			}

			f.setValue(v, false);
		}
	}

	private static Value getValue(MField f, Object jsonValue) {
		Value v = null;
		switch (f.getDataType()) {
		case BIGDECIMAL:
			v = new Value(Double.valueOf(jsonValue.toString()));
			break;
		case BOOLEAN:
			v = new Value(Boolean.valueOf(jsonValue.toString()));
			break;
		case DOUBLE:
			v = new Value(Double.valueOf(jsonValue.toString()));
			break;
		case INSTANT:
			switch (f.getDateTimeType()) {
			case DATE:
				v = new Value(LocalDate.parse(jsonValue.toString(), DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0).atZone(ZoneOffset.UTC).toInstant());
				break;
			case DATETIME:
				v = new Value(LocalDateTime.parse(jsonValue.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(ZoneId.systemDefault()).toInstant());
				break;
			case TIME:
				v = new Value(LocalTime.parse(jsonValue.toString(), DateTimeFormatter.ISO_LOCAL_TIME).atDate(LocalDate.of(1900, 01, 01)).atZone(ZoneOffset.UTC)
						.toInstant());
				break;
			default:
				throw new IllegalArgumentException("Unsupported data type: " + f.getDateTimeType());
			}
			break;
		case INTEGER:
			v = new Value(Integer.valueOf(jsonValue.toString()));
			break;
		case STRING:
			v = new Value(jsonValue.toString());
			break;
		default:
			throw new IllegalArgumentException("Unsupported data type: " + f.getDataType());
		}
		return v;
	}

	private static String getValueAsString(Value v, DateTimeType dateTimeType) {
		if (v == null) {
			return null;
		}

		switch (v.getType()) {
		case BIGDECIMAL:
			return v.getBigDecimalValue().toString();
		case BOOLEAN:
			return v.getBooleanValue().toString();
		case DOUBLE:
			return v.getDoubleValue().toString();
		case INSTANT:
			switch (dateTimeType) {
			case DATE:
				return v.getInstantValue().atZone(ZoneId.systemDefault()).toLocalDate().toString();
			case DATETIME:
				return v.getInstantValue().atZone(ZoneId.systemDefault()).toLocalDateTime().toString();
			case TIME:
				return v.getInstantValue().atZone(ZoneId.systemDefault()).toLocalTime().toString();
			}
			return v.getInstantValue().toString();
		case INTEGER:
			return v.getIntegerValue().toString();
		case STRING:
			return v.getStringValue();
		case ZONED:
			return v.getZonedDateTimeValue().toString();
		default:
			return null;
		}
	}

}
