package aero.minova.rcp.rcp.nattable;

import java.time.Instant;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.util.DateTimeUtil;

public class DateTimeDisplayConverter extends DisplayConverter {

	private Locale locale;
	private Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
	private String dateUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.DATE_UTIL, DisplayType.DATE_UTIL, "", locale);
	private String timeUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.TIME_UTIL, DisplayType.TIME_UTIL, "", locale);

	public DateTimeDisplayConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		if (canonicalValue instanceof Instant) {
			IEclipsePreferences node = InstanceScope.INSTANCE.getNode("aero.minova.rcp.preferencewindow");
			return DateTimeUtil.getDateTimeString((Instant) canonicalValue, locale, dateUtil, timeUtil);
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
