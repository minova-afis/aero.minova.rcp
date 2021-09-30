package aero.minova.rcp.rcp.nattable;

import java.time.Instant;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.util.TimeUtil;

public class ShortTimeDisplayConverter extends DisplayConverter {

	private Locale locale;

	public ShortTimeDisplayConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		String timeUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.TIME_UTIL, DisplayType.TIME_UTIL, "", locale);

		if (canonicalValue instanceof Instant) {
			return TimeUtil.getTimeString((Instant) canonicalValue, locale, timeUtil);
		}
		return null;
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		if (displayValue instanceof String && !((String) displayValue).isBlank()) {
			Instant res = TimeUtil.getTime((String) displayValue);
			if (res != null) {
				return res;
			} else {
				throw new RuntimeException("Invalid input " + displayValue + " for datatype Instant");
			}
		}
		return null;
	}

}
