package aero.minova.rcp.preferencewindow.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;

public class CustomLocale {
	private static Preferences preferences = InstanceScope.INSTANCE.getNode("aero.minova.rcp.preferencewindow");

	public static Locale[] getLocales() {
		return SimpleDateFormat.getAvailableLocales();
	}

	public static List<String> getCountries() {
		Locale locales[] = getLocales();
		List<String> countries = new ArrayList<String>();
		for (Locale locale : locales) {
			if (!locale.getDisplayCountry().equals("") && !countries.contains(locale.getDisplayCountry()))
				countries.add(locale.getDisplayCountry());
		}
		Collections.sort(countries);
		return countries;
	}

	public static Locale getLocale(String keyCountry, String keyLanguage) {
		Locale locale = Locale.getDefault();
		Locale locales[] = getLocales();
		Object valueCountry = InstancePreferenceAccessor.getValue(preferences, keyCountry, DisplayType.COMBO);
		Object valueLanguage = InstancePreferenceAccessor.getValue(preferences, keyLanguage, DisplayType.COMBO);
		for (Locale l : locales) {
			if(valueCountry.toString().equals(l.getDisplayCountry())) {
				if(valueLanguage.toString().equals(l.getDisplayLanguage())) {
					locale = l;
				}
			}
		}
		return locale;
	}

	public static List<String> getLanguageForCountry(String key) {

		String language;
		Locale locales[] = getLocales();
		Object valuePreferences = InstancePreferenceAccessor.getValue(preferences, key, DisplayType.COMBO);
		List<String> languages = new ArrayList<>();
		for (Locale locale : locales) {
			if (valuePreferences.toString().equals(locale.getDisplayCountry())) {
				language = locale.getDisplayLanguage();
				languages.add(language);
			}

		}
		return languages;
	}
}
