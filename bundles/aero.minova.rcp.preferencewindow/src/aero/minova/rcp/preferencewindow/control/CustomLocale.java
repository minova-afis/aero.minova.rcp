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
	static Preferences preferences = InstanceScope.INSTANCE.getNode("aero.minova.rcp.preferencewindow");

	public static Locale[] getLocales() {
		return SimpleDateFormat.getAvailableLocales();
	}

	public static List<String> getLanguages() {
		Locale[] locales = getLocales();
		List<String> languages = new ArrayList<>();
		for (Locale l : locales) {
			if (!l.getDisplayLanguage(l).equals("") && !languages.contains(l.getDisplayLanguage(l))) {
				languages.add(l.getDisplayLanguage(l));
			}
		}
		Collections.sort(languages);
		return languages;
	}
	
	public static Locale getLocale() {
		Locale[] locales = CustomLocale.getLocales();
		String language = InstancePreferenceAccessor.getValue(preferences, "language", DisplayType.LOCALE).toString();
		String country = InstancePreferenceAccessor.getValue(preferences, "country", DisplayType.LOCALE).toString();
		Locale locale = Locale.getDefault();
		
		for (Locale l : locales) {
			if(l.getDisplayLanguage(l).equals(language) && l.getDisplayCountry(l).equals(country))
				locale = l;
		}
		
		return locale;
	}

}
