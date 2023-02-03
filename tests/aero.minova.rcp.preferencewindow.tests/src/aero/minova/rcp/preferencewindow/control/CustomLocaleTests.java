package aero.minova.rcp.preferencewindow.control;

import static aero.minova.rcp.preferencewindow.control.CustomLocale.getLocales;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

class CustomLocaleTests {

	@Test
	void testGetLocales() {
		Locale list[] = getLocales();
		assertNotNull(list);
	}

	@Test
	void testGetLanguageForCountry() {
		List<String> languageTags = new ArrayList<>();
		languageTags.add("de");
		languageTags.add("fr");
		languageTags.add("it");
		List<String> languages = CustomLocale.getLanguages(Locale.GERMAN, languageTags);
		assertNotNull(languages);
	}

}
