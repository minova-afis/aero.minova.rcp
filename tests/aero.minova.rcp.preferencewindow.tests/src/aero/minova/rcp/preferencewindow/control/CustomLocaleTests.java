package aero.minova.rcp.preferencewindow.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;

public class CustomLocaleTests {

	@Test
	public void testGetLocales() {
		Locale list[] = CustomLocale.getLocales();
		assertNotNull(list);
		assertEquals(748, list.length);
	}
}
