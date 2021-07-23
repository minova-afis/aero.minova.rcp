package aero.minova.rcp.rcp.nattable;

import java.time.Instant;
import java.util.Locale;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

import aero.minova.rcp.util.DateUtil;

public class ShortDateDisplayConverter extends DisplayConverter {

	private Locale locale;

	public ShortDateDisplayConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		if (canonicalValue instanceof Instant) {
			return DateUtil.getDateString((Instant) canonicalValue, locale);
		}
		return null;
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		if (displayValue instanceof String && !((String) displayValue).isBlank()) {
			Instant res = DateUtil.getDate((String) displayValue);
			if (res != null) {
				return res;
			} else {
				throw new RuntimeException("Invalid input " + displayValue + " for datatype Instant");
			}
		}
		return null;
	}

}
