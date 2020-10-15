package aero.minova.rcp.preferencewindow.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.Preferences;

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

}
