package aero.minova.rcp.preferencewindow.control;

import static aero.minova.rcp.preferencewindow.control.CustomLocale.getLocales;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

public class CustomLocaleTests {

	@Test
	public void testGetLocales() {
		Locale list[] = getLocales();
		assertNotNull(list);
		assertEquals(748, list.length);
	}

	@Test
	public void testGetCountrys() {
		List<String> countrys = CustomLocale.getCountries("language");
		assertNotNull(countrys);
		assertTrue(countrys.size() > 0);
	}

	@Test
	public void testGetLanguageForCountry() {
		List<String> languages = CustomLocale.getLanguages();
		assertNotNull(languages);
	}

	@Test
	public void testGetLocale() {
		Locale locale = CustomLocale.getLocale();
		assertNotNull(locale);
		assertEquals(Locale.getDefault(), locale);
	}
}
