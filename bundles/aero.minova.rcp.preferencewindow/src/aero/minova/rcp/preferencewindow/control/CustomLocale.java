package aero.minova.rcp.preferencewindow.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;

public class CustomLocale {
	static Preferences preferences = InstanceScope.INSTANCE.getNode("aero.minova.rcp.preferencewindow");

	public static Locale[] getLocales() {
		return SimpleDateFormat.getAvailableLocales();
	}

	public static List<String> getLanguages() {
		Locale locale = Locale.getDefault();
		Locale[] locales = getLocales();
		List<String> languages = new ArrayList<>();
		for (Locale l : locales) {
			if (!l.getDisplayLanguage(locale).equals("") && !languages.contains(l.getDisplayLanguage(locale))) {
				languages.add(l.getDisplayLanguage(locale));
			}
		}
		Collections.sort(languages);
		return languages;
	}

	public static List<String> getCountries() {
		Locale locale = Locale.getDefault();
		List<String> countries = new ArrayList<>();
		Object data = PreferenceWindow.getInstance().getValueFor("language");
		Locale[] locales = getLocales();
		for (Locale l : locales) {
			if (data.toString().equals(l.getDisplayLanguage(locale))) {
				if (!l.getDisplayCountry(locale).equals("") && !countries.contains(l.getDisplayCountry(locale))) {
					countries.add(l.getDisplayCountry(locale));
				}
			}
		}
		Collections.sort(countries);
		return countries;
	}

	public static Locale getLocale() {
		Locale[] locales = getLocales();
		Locale locale = Locale.getDefault();
		for (Locale l : locales) {
			if (InstancePreferenceAccessor.getValue(preferences, "language", DisplayType.LOCALE)
					.equals(l.getDisplayLanguage())
					&& InstancePreferenceAccessor.getValue(preferences, "land", DisplayType.LOCALE)
							.equals(l.getDisplayCountry())) {
				locale = l;
			}
		}
		return locale;
	}

}
