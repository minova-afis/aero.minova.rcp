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
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;

public class DateTimeValueAccessor extends AbstractValueAccessor {

	public DateTimeValueAccessor(MField field, Control control) {
				super(field, control);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		Locale locale = (Locale) control.getData(TRANSLATE_LOCALE);
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		String dateUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.DATE_UTIL, DisplayType.DATE_UTIL, "", locale);
		String timeUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.TIME_UTIL, DisplayType.TIME_UTIL, "", locale);

		if (value == null) {
			setText(control, "");
		} else {
			Instant date = value.getInstantValue();
			LocalDateTime localDateTime = LocalDateTime.ofInstant(date, ZoneId.of("UTC"));
			String pattern = dateUtil + " " + timeUtil;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
			if (dateUtil.isBlank() && timeUtil.isBlank()) {
				// Bei der Formatierung geschehen fehler, wir erhalten das Milienium zurück
				setText(control, localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withLocale(locale)));
			} else if (timeUtil.isBlank()) {
				setText(control, localDateTime.format(DateTimeFormatter.ofPattern(pattern + "HH:mm").withLocale(locale)));
			} else if (dateUtil.isBlank()) {
				setText(control, localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy" + pattern).withLocale(locale)));
			} else {
				setText(control, localDateTime.format(dtf));
			}
		}
	}
	
	private void setText(Control control, String text) {
		if (control instanceof TextAssist) {
			((TextAssist) control).setText(text);
		} else if (control instanceof Text) {
			((Text) control).setText(text);
		}
	}

}
