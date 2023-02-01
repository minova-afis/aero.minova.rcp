package aero.minova.rcp.preferencewindow.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.jupiter.api.Test;
import org.osgi.service.prefs.Preferences;

class TestZoneIdTests {

	static final String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";
	Preferences preferences = InstanceScope.INSTANCE.getNode(PREFERENCES_NODE);

	@Test
	void testNotNull() {
		Map<String, ZoneId> zones = CustomTimeZone.getZones(Locale.GERMAN);
		assertNotNull(zones);
	}

	@Test
	void testGetIdENGLISH() {
		Map<String, ZoneId> zones = CustomTimeZone.getZones(Locale.US);
		assertNotNull(CustomTimeZone.getId(zones, "Central European Time", Locale.US));
		assertEquals("Europe/Monaco", CustomTimeZone.getId(zones, "Central European Time", Locale.US).toString());
	}

	@Test
	void testGetTimeZoneId() {
		Object value = "Ulyanovsk Time";
		Locale l = Locale.getDefault(Category.DISPLAY);
		Map<String, ZoneId> zones = CustomTimeZone.getZones(Locale.GERMAN);
		String id = value.toString().substring(value.toString().lastIndexOf(")") + 1);
		String zoneId = CustomTimeZone.getId(zones, id, l).toString();
		assertEquals("Europe/Ulyanovsk", zoneId);
	}

}
