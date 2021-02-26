package aero.minova.rcp.rcp.nattable;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;
import aero.minova.rcp.model.FilterValue;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.DateTimeUtil;
import aero.minova.rcp.rcp.util.OperatorExtractionUtil;
import aero.minova.rcp.rcp.util.TimeUtil;

public class FilterDisplayConverter extends DisplayConverter {

	private Locale locale;
	private DataType datatype;
	private DateTimeType datetimetype;
	private ZoneId zoneId;

	public FilterDisplayConverter(DataType datatype, Locale locale, DateTimeType datetimetype, ZoneId zoneId) {
		this.locale = locale;
		this.datatype = datatype;
		this.datetimetype = datetimetype;
		this.zoneId = zoneId;
	}

	public FilterDisplayConverter(DataType datatype, Locale locale, DateTimeType datetimetype) {
		this.locale = locale;
		this.datatype = datatype;
		this.datetimetype = datetimetype;
	}

	public FilterDisplayConverter(DataType datatype, Locale locale) {
		this.locale = locale;
		this.datatype = datatype;
	}

	public FilterDisplayConverter(DataType datatype) {
		this.datatype = datatype;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		if (canonicalValue instanceof FilterValue) {
			FilterValue cv = (FilterValue) canonicalValue;
			String val = "";
			if (cv.getValue().toString().contains("null"))
				return cv.getValue().toString();
			switch (datatype) {
			case INSTANT:
				switch (datetimetype) {
				case DATE:
					val = DateTimeUtil.getDateString((Instant) cv.getFilterValue().getValue(), locale);
					break;
				case TIME:
					val = TimeUtil.getTimeString((Instant) cv.getFilterValue().getValue(), locale);
					break;
				case DATETIME:
					val = DateTimeUtil.getDateTimeString((Instant) cv.getFilterValue().getValue(), locale, zoneId);
					break;
				}
				break;
			case ZONED:
				val = DateTimeUtil.getDateTimeString((Instant) cv.getFilterValue().getValue(), locale, zoneId);
				break;
			default:
				val = cv.getFilterValue().getValue().toString();
			}

			if (cv.getValue().toString().contains("null"))
				return cv.getValue().toString();
			return cv.getValue().toString() + " " + val;
		}
		return null;
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		if (displayValue instanceof String) {
			String valueString = (String) displayValue;

			int operatorPos;
			switch (datatype) {
			case INSTANT:
			case ZONED:
			case INTEGER:
			case DOUBLE:
				operatorPos = OperatorExtractionUtil.getOperatorEndIndex(valueString, Constants.NUMBER_OPERATORS);
				break;
			case STRING:
				operatorPos = OperatorExtractionUtil.getOperatorEndIndex(valueString, Constants.STRING_OPERATORS);
				break;
			default:
				operatorPos = 0;
			}
			String operator = valueString.substring(0, operatorPos);

			// Bei "null" und "!null" wird kein Wert eingegeben
			if (operator.contains("null"))
				return new FilterValue(operator, null, valueString);

			String filterValueString = valueString.substring(operatorPos).strip();
			Object filterValue = null;

			if (filterValueString.length() > 0) {
				switch (datatype) {
				case INSTANT:
					switch (datetimetype) {
					case DATE:
						filterValue = DateTimeUtil.getDate(filterValueString);
						break;
					case TIME:
						filterValue = TimeUtil.getTime(filterValueString);
						break;
					case DATETIME:
						// TODO: .getDateTime(String s) Methode hinzufügen?
						break;
					}
					break;
				case ZONED:
					// TODO: .getDateTime(String s) Methode hinzufügen?
					break;
				case INTEGER:
					filterValue = Integer.parseInt(filterValueString);
					break;
				case DOUBLE:
					filterValue = Double.parseDouble(filterValueString);
					NumberFormat formatter = NumberFormat.getInstance(locale);
					formatter.setMaximumFractionDigits(2);
					formatter.setMinimumFractionDigits(2);
					filterValue = formatter.format((double) filterValue);
					break;
				case BOOLEAN:
					filterValue = Boolean.parseBoolean(filterValueString);
					break;
				case STRING:
					filterValue = filterValueString;
					break;
				default:
					break;
				}

				if (filterValue != null) {

					/*
					 * Ein String soll im Allgemeinen mit dem Like-Operator gesucht werden. Außerdem wird am Ende ein Wildcard-Operator eingefürgt, damit Felder
					 * die mit der Eingabe beginnen gefunden werden.
					 */
					if (operator.equals("") && datatype.equals(DataType.STRING)) {
						operator = "~";
						if (!containsWildcard(filterValueString))
							filterValue = filterValue + "%";
					}

					// Standart Operator ist "="
					else if (operator.equals(""))
						operator = "=";

					return new FilterValue(operator, filterValue, valueString);
				} else {
					throw new RuntimeException("Invalid input " + filterValueString + " for datatype " + datatype);
				}
			}
		}
		return null;
	}

	private boolean containsWildcard(String filterValue) {
		for (String wildcard : Constants.WILDCARD_OPERATORS) {
			if (filterValue.contains(wildcard))
				return true;
		}
		return false;
	}

}
