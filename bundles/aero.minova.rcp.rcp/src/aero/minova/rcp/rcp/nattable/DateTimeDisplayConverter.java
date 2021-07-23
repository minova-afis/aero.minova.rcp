package aero.minova.rcp.rcp.nattable;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

import aero.minova.rcp.util.DateTimeUtil;
import aero.minova.rcp.util.DateUtil;

public class DateTimeDisplayConverter extends DisplayConverter {

	private Locale locale;

	public DateTimeDisplayConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		if (canonicalValue instanceof Instant) {
			IEclipsePreferences node = InstanceScope.INSTANCE.getNode("aero.minova.rcp.preferencewindow");
			String string = node.get("timezone", "UTC");
			ZoneId z = ZoneId.of(string);
			return DateUtil.getDateTimeString((Instant) canonicalValue, locale, z);
		}
		return null;
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		if (displayValue instanceof String && !((String) displayValue).isBlank()) {
			Instant res = DateTimeUtil.getDateTime((String) displayValue);
			if (res != null) {
				return res;
			} else {
				throw new RuntimeException("Invalid input " + displayValue + " for datatype Instant");
			}
		}
		return null;
	}
}
