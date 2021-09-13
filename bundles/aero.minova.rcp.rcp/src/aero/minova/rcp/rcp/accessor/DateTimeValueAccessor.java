package aero.minova.rcp.rcp.accessor;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

public class DateTimeValueAccessor extends AbstractValueAccessor {

	public DateTimeValueAccessor(MField field, TextAssist textAssist) {
		super(field, textAssist);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		Locale locale = (Locale) control.getData(TRANSLATE_LOCALE);
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		String dateUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.DATE_UTIL, DisplayType.DATE_UTIL, "", locale);
		String timeUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.TIME_UTIL, DisplayType.TIME_UTIL, "", locale);

		if (value == null) {
			((TextAssist) control).setText("");
		} else {
			Instant date = value.getInstantValue();
			LocalDateTime localDateTime = LocalDateTime.ofInstant(date, ZoneId.of("UTC"));
			String pattern = dateUtil + " " + timeUtil;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
			if (dateUtil.isBlank() && timeUtil.isBlank()) {
				// Bei der Formatierung geschehen fehler, wir erhalten das Milienium zurück
				((TextAssist) control).setText(localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withLocale(locale)));
			} else if (timeUtil.isBlank()) {
				((TextAssist) control).setText(localDateTime.format(DateTimeFormatter.ofPattern(pattern + "HH:mm").withLocale(locale)));
			} else if (dateUtil.isBlank()) {
				((TextAssist) control).setText(localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy" + pattern).withLocale(locale)));
			} else {
				((TextAssist) control).setText(localDateTime.format(dtf));
			}
		}
	}

}
