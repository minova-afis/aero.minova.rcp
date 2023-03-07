package aero.minova.rcp.rcp.accessor;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;

public class ShortDateValueAccessor extends AbstractValueAccessor {

	public ShortDateValueAccessor(MField field, Control control) {
		super(field, control);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		Locale locale = (Locale) control.getData(TRANSLATE_LOCALE);
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		String dateUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.DATE_UTIL, DisplayType.DATE_UTIL, "", locale);

		if (value == null) {
			setText(control, "");
		} else {
			Instant date = value.getInstantValue();
			LocalDate localDate = LocalDate.ofInstant(date, ZoneId.of("UTC"));
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateUtil, locale);
			if (dateUtil.equals("")) {
				// Bei der Formatierung geschehen fehler, wir erhalten das Milienium zurück
				setText(control, localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)));
			} else {
				// Bei der Formatierung geschehen fehler, wir erhalten das Milienium zurück
				setText(control, localDate.format(dtf));
			}
		}
	}
}
