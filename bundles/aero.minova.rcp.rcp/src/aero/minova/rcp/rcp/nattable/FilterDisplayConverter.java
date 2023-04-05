package aero.minova.rcp.rcp.nattable;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;
import aero.minova.rcp.model.FilterValue;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.rcp.util.OperatorExtractionUtil;
import aero.minova.rcp.util.DateTimeUtil;
import aero.minova.rcp.util.DateUtil;
import aero.minova.rcp.util.TimeUtil;

public class FilterDisplayConverter extends DisplayConverter {

	private Locale locale;
	private DataType datatype;
	private DateTimeType datetimetype;
	private int decimals;

	public FilterDisplayConverter(DataType datatype, Locale locale, DateTimeType datetimetype) {
		this.locale = locale;
		this.datatype = datatype;
		this.datetimetype = datetimetype;
	}

	public FilterDisplayConverter(DataType datatype, Locale locale, int decimals) {
		this.locale = locale;
		this.datatype = datatype;
		this.decimals = decimals;
	}

	public FilterDisplayConverter(DataType datatype) {
		this.datatype = datatype;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		String dateUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.DATE_UTIL, DisplayType.DATE_UTIL, "", locale);
		String timeUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.TIME_UTIL, DisplayType.TIME_UTIL, "", locale);
		String timezone = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.TIMEZONE, DisplayType.STRING, "", locale);

		if (canonicalValue instanceof FilterValue cv) {
			String val = "";
			if (cv.getValue().toString().contains("null")) {
				return cv.getValue().toString();
			}
			switch (datatype) {
			case INSTANT:
				switch (datetimetype) {
				case DATE:
					val = DateUtil.getDateString(cv.getFilterValue().getInstantValue(), locale, dateUtil);
					break;
				case TIME:
					val = TimeUtil.getTimeString(cv.getFilterValue().getInstantValue(), locale, timeUtil);
					break;
				case DATETIME:
					val = DateTimeUtil.getDateTimeString(cv.getFilterValue().getInstantValue(), locale, dateUtil, timeUtil, timezone);
					break;
				}
				break;
			case ZONED:
				val = DateTimeUtil.getDateTimeString(cv.getFilterValue().getInstantValue(), locale, dateUtil, timeUtil, timezone);
				break;
			case DOUBLE:
				NumberFormat formatter = NumberFormat.getInstance(locale);
				formatter.setMaximumFractionDigits(decimals);
				formatter.setMinimumFractionDigits(decimals);
				val = formatter.format(cv.getFilterValue().getDoubleValue());
				break;
			default:
				val = cv.getFilterValue().getValue().toString();
			}

			return cv.getValue().toString() + " " + val;
		}
		return null;
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		String timezone = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.TIMEZONE, DisplayType.STRING, "", locale);

		if (displayValue instanceof String valueString) {

			int operatorPos;
			switch (datatype) {
			case INSTANT, ZONED, INTEGER, DOUBLE:
				operatorPos = OperatorExtractionUtil.getOperatorEndIndex(valueString, Constants.getNumberOperators());
				break;
			case STRING:
				operatorPos = OperatorExtractionUtil.getOperatorEndIndex(valueString, Constants.getStringOperators());
				break;
			default:
				operatorPos = 0;
			}
			String operator = valueString.substring(0, operatorPos);

			// Bei "null" und "!null" wird kein Wert eingegeben
			if (operator.contains("null")) {
				return new FilterValue(operator, null, valueString);
			}

			String filterValueString = valueString.substring(operatorPos).strip();
			Object filterValue = null;

			if (filterValueString.length() > 0) {
				switch (datatype) {
				case INSTANT:
					switch (datetimetype) {
					case DATE:
						filterValue = DateUtil.getDate(filterValueString);
						break;
					case TIME:
						filterValue = TimeUtil.getTime(filterValueString);
						break;
					case DATETIME:
						filterValue = DateTimeUtil.getDateTime(LocalDateTime.now(ZoneId.of(timezone)).toInstant(ZoneOffset.UTC), filterValueString, locale,
								timezone);
						break;
					}
					break;
				case ZONED:
					filterValue = DateTimeUtil.getDateTime(LocalDateTime.now(ZoneId.of(timezone)).toInstant(ZoneOffset.UTC), filterValueString, locale,
							timezone);
					break;
				case INTEGER:
					filterValue = Integer.parseInt(filterValueString);
					break;
				case DOUBLE:
					filterValue = Double.parseDouble(filterValueString);
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
						if (!containsWildcard(filterValueString)) {
							filterValue = filterValue + "%";
						}
					}

					// Standart Operator ist "="
					else if (operator.equals("")) {
						operator = "=";
					}

					return new FilterValue(operator, filterValue, valueString);
				} else {
					throw new RuntimeException("Invalid input " + filterValueString + " for datatype " + datatype);
				}
			}
		}
		return null;
	}

	private boolean containsWildcard(String filterValue) {
		for (String wildcard : Constants.getWildcardOperators()) {
			if (filterValue.contains(wildcard)) {
				return true;
			}
		}
		return false;
	}

}
