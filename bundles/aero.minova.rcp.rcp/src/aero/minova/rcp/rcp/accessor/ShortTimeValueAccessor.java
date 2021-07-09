package aero.minova.rcp.rcp.accessor;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;

public class ShortTimeValueAccessor extends AbstractValueAccessor {

	public ShortTimeValueAccessor(MField field, TextAssist textAssist) {
		super(field, textAssist);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		Locale locale = (Locale) control.getData(TRANSLATE_LOCALE);
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		String timeUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.TIME_UTIL, DisplayType.TIME_UTIL, "", locale);

		if (value == null) {
			((TextAssist) control).setText("");
		} else {
			Instant time = value.getInstantValue();
			LocalTime localTime = LocalTime.ofInstant(time, ZoneId.of("UTC"));
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(timeUtil, locale);
			if(timeUtil.equals("")) {
				((TextAssist) control).setText(localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)));
			} else {
				((TextAssist) control).setText(localTime.format(dtf));
			}
		}
	}
}
