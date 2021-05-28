package aero.minova.rcp.rcp.fields;

import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.widgets.Text;

public class NumberFieldUtil {

	private NumberFieldUtil() {}

	/**
	 * generiert aus den Properties
	 * <ul>
	 * <li>{@link NumberFieldUtil#FIELD_MAX_VALUE}</li>
	 * <li>{@link NumberFieldUtil#FIELD_MIN_VALUE}</li>
	 * <li>{@link NumberFieldUtil#FIELD_DECIMALS}</li>
	 * </ul>
	 * den Messagetext, der angezeigt wird, sobald das Feld leer ist.
	 * 
	 * @param text
	 */
	public static void setMessage(Text text) {
		int decimals = (int) text.getData(FieldUtil.FIELD_DECIMALS);
		double maximum = (double) text.getData(FieldUtil.FIELD_MAX_VALUE);
		Locale locale = (Locale) text.getData(FieldUtil.TRANSLATE_LOCALE);

		int integer = 0;
		if (maximum >= Float.MAX_VALUE) {
			integer = 9 - decimals;
		} else {
			while (maximum > 1) {
				maximum /= 10;
				integer++;
			}
		}

		// DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		numberFormat.setMaximumFractionDigits(decimals);
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setMaximumIntegerDigits(integer);
		numberFormat.setMinimumIntegerDigits(integer);
		text.setMessage(numberFormat.format(0.0d));
	}
}
