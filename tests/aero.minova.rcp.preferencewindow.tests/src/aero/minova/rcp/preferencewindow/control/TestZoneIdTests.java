package aero.minova.rcp.preferencewindow.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.Test;
import org.osgi.service.prefs.Preferences;

public class TestZoneIdTests {

	public static final String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";
	Preferences preferences = InstanceScope.INSTANCE.getNode(PREFERENCES_NODE);

	@Test
	public void testNotNull() {
		Map<String, ZoneId> zones = CustomTimeZone.getZones(Locale.GERMAN);
		assertNotNull(zones);
	}

	@Test
	public void testGermanZones() {
		Map<String, ZoneId> zones = CustomTimeZone.getZones(Locale.GERMAN);
		assertEquals(198, zones.size());
	}

	@Test
	public void testGermanyZones() {
		Map<String, ZoneId> zones = CustomTimeZone.getZones(Locale.GERMANY);
		assertEquals(198, zones.size());
	}

	@Test
	public void testGetId() {
		Map<String, ZoneId> zones = CustomTimeZone.getZones(Locale.GERMANY);
		assertNotNull(CustomTimeZone.getId(zones, "Mitteleuropäische Zeit", Locale.GERMAN));
		assertEquals("Europe/Monaco", CustomTimeZone.getId(zones, "Mitteleuropäische Zeit", Locale.GERMAN).toString());
	}

	@Test
	public void testGetIdENGLISH() {
		Map<String, ZoneId> zones = CustomTimeZone.getZones(Locale.US);
		assertNotNull(CustomTimeZone.getId(zones, "Central European Time", Locale.US));
		assertEquals("Europe/Monaco", CustomTimeZone.getId(zones, "Central European Time", Locale.US).toString());
	}

	@Test
	public void testGetTimeZone() {
		String id = "Europe/Monaco";
		String tz = ZoneId.of(id).getDisplayName(TextStyle.FULL, Locale.GERMAN);
		String result = CustomTimeZone.displayTimeZone(Locale.GERMAN, tz);
		assertEquals("(GMT+1:00) Mitteleuropäische Zeit", result);
	}

	@Test
	public void testListSize() {
		List<String> zones = CustomTimeZone.getTimeZones("land", "language");
		assertEquals(198, zones.size());
	}

	@Test
	public void testGetTimeZoneId() {
		Object value = "Ulyanovsk Time";
		Locale l = Locale.getDefault(Category.DISPLAY);
		Map<String, ZoneId> zones = CustomTimeZone.getZones(l);
		String id = value.toString().substring(value.toString().lastIndexOf(")") + 1);
		String zoneId = CustomTimeZone.getId(zones, id, l).toString();
		assertEquals("Europe/Ulyanovsk", zoneId);
	}

}
