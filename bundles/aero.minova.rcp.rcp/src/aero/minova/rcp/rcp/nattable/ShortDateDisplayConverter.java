package aero.minova.rcp.rcp.nattable;

import java.time.Instant;
import java.util.Locale;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

import aero.minova.rcp.rcp.util.DateTimeUtil;

public class ShortDateDisplayConverter extends DisplayConverter {

	private Locale locale;

	public ShortDateDisplayConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		if (canonicalValue instanceof Instant) {
			return DateTimeUtil.getDateString((Instant) canonicalValue, locale);
		}
		return null;
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		return null;
	}

}
