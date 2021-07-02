package aero.minova.rcp.preferencewindow.control;

import java.io.File;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;

/**
 * Liefert Methoden zum holen aller Sprache, aller Locales und des aktuellen Locales.
 * 
 * @author bauer
 */
public class CustomLocale {
	static Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);

	/**
	 * Liefert einen Array mit allen Locales zurück.
	 * 
	 * @return
	 */
	public static Locale[] getLocales() {
		return SimpleDateFormat.getAvailableLocales();
	}

	/**
	 * Liefert eine Liste mit allen Sprachen wieder. Die Sprachen werden in ihrer eigenen Sprache dargestellt.
	 * 
	 * @return
	 */
	public static List<String> getLanguages(Locale activeLocale, IDataService dataService) {
		List<String> languages = new ArrayList<>();
		File workspace = new File(dataService.getStoragePath().toAbsolutePath().toString() + "/i18n");
		if (workspace.isDirectory()) {
			for (String messageProperties : workspace.list()) {
				if (messageProperties.contains("_")) {
					String shortcut = messageProperties.substring(messageProperties.indexOf("_") + 1, messageProperties.indexOf("_") + 3);
					Locale l = Locale.forLanguageTag(shortcut);
					if (l.getDisplayLanguage(l) != null && !languages.contains(l.getDisplayLanguage(l))) {
						languages.add(l.getDisplayLanguage(l));
					}
				}
			}

		}
		Locale l = Locale.forLanguageTag("en");
		if (l.getDisplayLanguage(l) != null && !languages.contains(l.getDisplayLanguage(l))) {
			languages.add(l.getDisplayLanguage(l));
		}

		Collator collator = Collator.getInstance(activeLocale);
		Collections.sort(languages, collator);
		return languages;
	}

	/**
	 * Gibt den Locale gemäß der ausgewählten Sprache und Landes zurück.
	 * 
	 * @return
	 */
	public static Locale getLocale() {

		Locale[] locales = CustomLocale.getLocales();
		Locale locale = Locale.getDefault();
		String language = InstancePreferenceAccessor
				.getValue(preferences, ApplicationPreferences.LOCALE_LANGUAGE, DisplayType.LOCALE, Locale.getDefault().getDisplayLanguage(locale), locale)
				.toString();
		String country = InstancePreferenceAccessor
				.getValue(preferences, ApplicationPreferences.COUNTRY, DisplayType.LOCALE, Locale.getDefault().getDisplayCountry(locale), locale).toString();

		for (Locale l : locales) {
			if (l.getDisplayLanguage(l).equals(language) && l.getDisplayCountry(l).equals(country))
				locale = l;
		}

		return locale;
	}

}
