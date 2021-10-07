package aero.minova.rcp.model.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import aero.minova.rcp.model.Value;

public class ParamStringUtil {

	private ParamStringUtil() {}

	private static final String PATTERN = "yyyyMMddHHmmss";

	/**
	 * TODO: geschachtelte Param-String-Felder?
	 * 
	 * @param values
	 * @return
	 */
	public static String convertValuesToStringParameter(List<Value> values, Locale locale) {
		if (values == null) {
			return null;
		}
		StringBuilder output = new StringBuilder();
		int i = 0;
		for (Value v : values) {

			if (v == null) {
				output.append("{" + i + "-" + IVariantType.VARIANT_EMPTY + "-" + "0}");
			} else {

				switch (v.getType()) {
				case BIGDECIMAL:
					Double bdv = v.getBigDecimalValue();
					output.append("{" + i + "-" + IVariantType.VARIANT_DOUBLE + "-" + bdv.toString().length() + "}" + bdv);
					break;
				case BOOLEAN:
					Boolean bv = v.getBooleanValue();
					output.append("{" + i + "-" + IVariantType.VARIANT_BOOLEAN + "-" + 1 + "}" + (bv.booleanValue() ? "1" : "0"));
					break;
				case DOUBLE:
					Double dv = v.getDoubleValue();
					output.append("{" + i + "-" + IVariantType.VARIANT_DOUBLE + "-" + dv.toString().length() + "}" + dv);
					break;
				case FILTER:
					// Nur für Suche benötigt
					break;
				case INSTANT:
					Instant instantv = v.getInstantValue();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN).withLocale(locale).withZone(ZoneId.systemDefault());
					String dateString = formatter.format(instantv);
					output.append("{" + i + "-" + IVariantType.VARIANT_DATE + "-" + dateString.length() + "}" + dateString);

					break;
				case INTEGER:
					Integer iv = v.getIntegerValue();
					output.append("{" + i + "-" + IVariantType.VARIANT_INT + "-" + iv.toString().length() + "}" + iv);
					break;
				case STRING:
					String sv = v.getStringValue();
					output.append("{" + i + "-" + IVariantType.VARIANT_STRING + "-" + sv.length() + "}" + sv);
					break;
				case ZONED:
					ZonedDateTime zdtv = v.getZonedDateTimeValue();
					formatter = DateTimeFormatter.ofPattern(PATTERN);
					dateString = zdtv.format(formatter);
					output.append("{" + i + "-" + IVariantType.VARIANT_DATE + "-" + dateString.length() + "}" + dateString);
					break;
				default:
					output.append("{" + i + "-" + IVariantType.VARIANT_OBJECT + "-" + v.getValue().toString().length() + "}" + v.getValue().toString());
					break;
				}
			}

			i++;
		}
		return output.toString();

	}

	public static List<Value> convertStringParameterToValues(String value, Locale locale) {
		List<Value> values = new ArrayList<>();
		if (value == null || value.length() == 0) {
			return values;
		}

		while (value.startsWith("{") && value.length() > 6 && value.indexOf("}") != -1) {
			// Wir schreiben die Eigenschaften des Parameters:{​i-t-l}​ in den StringTokenizer
			StringTokenizer st = new StringTokenizer(value.substring(1, value.indexOf("}")), "-");
			st.nextToken(); // Array-Index
			int typ = Integer.parseInt(st.nextToken()); // DatenTyp
			int length = Integer.parseInt(st.nextToken()); // Länge des Parameters
			String parameter = value.substring(value.indexOf("}") + 1, value.indexOf("}") + 1 + length);
			Object newVar = null;
			switch ((short) typ) {
			case IVariantType.VARIANT_STRING:
				newVar = parameter;
				break;
			case IVariantType.VARIANT_INT:
				newVar = new Integer(parameter);
				break;
			case IVariantType.VARIANT_DOUBLE:
				newVar = new Double(parameter);
				break;
			case IVariantType.VARIANT_SHORT:
				newVar = new Short(parameter);
				break;
			case IVariantType.VARIANT_BOOLEAN:
				newVar = Boolean.valueOf(parameter.equals("1") ? true : false);
				break;
			case IVariantType.VARIANT_OBJECT:
				if (length == 14) {
					newVar = LocalDateTime.parse(parameter, DateTimeFormatter.ofPattern(PATTERN, locale)).atZone(ZoneId.systemDefault()).toInstant();
				}
				break;
			case IVariantType.VARIANT_DATE:
				newVar = LocalDateTime.parse(parameter, DateTimeFormatter.ofPattern(PATTERN, locale)).atZone(ZoneId.systemDefault()).toInstant();
				break;
			case IVariantType.VARIANT_EMPTY:
				newVar = null;
				break;
			default:
				newVar = null;
				break;
			}
			// Die Zeile wird verkürzt
			value = value.substring(value.indexOf("}") + length + 1);
			// Variant List wird ergänzt
			Value v = newVar == null ? null : new Value(newVar);
			values.add(v);
		}

		return values;
	}

}
