package aero.minova.rcp.rcp.nattable;

import java.time.Instant;
import java.util.Locale;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

import aero.minova.rcp.rcp.util.TimeUtil;

public class ShortTimeDisplayConverter extends DisplayConverter {

	private Locale locale;

	public ShortTimeDisplayConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		if (canonicalValue instanceof Instant) {
			return TimeUtil.getTimeString((Instant) canonicalValue, locale);
		}
		return null;
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		return null;
	}

}
