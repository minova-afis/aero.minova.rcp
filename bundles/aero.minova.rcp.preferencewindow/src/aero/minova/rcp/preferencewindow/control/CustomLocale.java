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

/**
 * Liefert Methoden zum holen aller Sprache, aller Locales und des aktuellen
 * Locales.
 * 
 * @author bauer
 *
 */
public class CustomLocale {
	static Preferences preferences = InstanceScope.INSTANCE.getNode("aero.minova.rcp.preferencewindow");

	/**
	 * Liefert einen Array mit allen Locales zurück.
	 * 
	 * @return
	 */
	public static Locale[] getLocales() {
		return SimpleDateFormat.getAvailableLocales();
	}

	/**
	 * Liefert eine Liste mit allen Sprachen wieder. Die Sprachen werden in ihrer
	 * eigenen Sprache dargestellt.
	 * 
	 * @return
	 */
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

	/**
	 * Gibt den Locale gemäß der ausgewählten Sprache und Landes zurück. 
	 * @return
	 */
	public static Locale getLocale() {
		Locale[] locales = CustomLocale.getLocales();
		String language = InstancePreferenceAccessor.getValue(preferences, "language", DisplayType.LOCALE).toString();
		String country = InstancePreferenceAccessor.getValue(preferences, "country", DisplayType.LOCALE).toString();
		Locale locale = Locale.getDefault();

		for (Locale l : locales) {
			if (l.getDisplayLanguage(l).equals(language) && l.getDisplayCountry(l).equals(country))
				locale = l;
		}

		return locale;
	}

}
