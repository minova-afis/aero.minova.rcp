package aero.minova.rcp.preferencewindow.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

public class CustomLocaleTests {

	@Test
	public void testGetLocales() {
		Locale list[] = CustomLocale.getLocales();
		assertNotNull(list);
		assertEquals(748, list.length);
	}
	
	@Test
	public void testGetCountrys() {
		List<String> countrys = CustomLocale.getCountrys();
		assertNotNull(countrys);
		assertTrue(countrys.size() > 0);
	}
	
	@Test
	public void testGetLanguageForCountry() {
		List<String> languages = CustomLocale.getLanguageForCountry("land");
		assertNotNull(languages);
		System.out.println(languages);
	}
}
