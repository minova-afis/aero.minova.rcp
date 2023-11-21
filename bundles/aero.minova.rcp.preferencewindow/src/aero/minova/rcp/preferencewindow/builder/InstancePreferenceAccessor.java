package aero.minova.rcp.preferencewindow.builder;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.graphics.FontData;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferencewindow.control.CustomTimeZone;

/**
 * Liefert Methoden zu holen und setzen von Wert aus und in die Preferences.
 * 
 * @author bauer
 */
public class InstancePreferenceAccessor {

	private InstancePreferenceAccessor() {}

	/**
	 * Holt den an den übergebenen Key gebunden Wert aus den angegebenen Preferences.
	 * 
	 * @param preferences
	 * @param preferenceKey
	 * @param type
	 * @return
	 */
	public static Object getValue(Preferences preferences, String preferenceKey, DisplayType type, Object defaultValue, Locale l) {
		switch (type) {
		case STRING, FILE, DIRECTORY, COMBO, URL, PASSWORD, LOCALE, TEXT, DATE_UTIL, TIME_UTIL:
			return preferences.get(preferenceKey, (String) defaultValue);
		case INTEGER:
			return preferences.getInt(preferenceKey, (int) defaultValue);
		case FLOAT:
			return preferences.getFloat(preferenceKey, (float) defaultValue);
		case CHECK, SENDLOGSBUTTON, RESETUIBUTTON:
			return preferences.getBoolean(preferenceKey, (boolean) defaultValue);
		case FONT:
			String fd = preferences.get(preferenceKey, (String) defaultValue);
			if (fd == null || fd.length() == 0) {
				fd = null;
			}
			return (fd == null ? null : new FontData(fd));
		case ZONEID:
			String id = preferences.get(preferenceKey, (String) defaultValue);
			String result = CustomTimeZone.displayTimeZone(id, l);
			return result;
		default:
			break;
		}
		throw new RuntimeException("Keinen passenden Wert gefunden");
	}

	/**
	 * Setzt den übergebenen Wert mit dem Key in die angegebenen Preferences.
	 * 
	 * @param preferences
	 * @param preferenceKey
	 * @param type
	 * @param value
	 */
	public static void putValue(Preferences preferences, String preferenceKey, DisplayType type, Object value, Locale l) {
		switch (type) {
		case STRING, FILE, DIRECTORY, COMBO, URL, LOCALE, PASSWORD, TEXT, DATE_UTIL, TIME_UTIL:
			preferences.put(preferenceKey, (String) value);
			break;
		case INTEGER:
			preferences.putInt(preferenceKey, (int) value);
			break;
		case FLOAT:
			preferences.putFloat(preferenceKey, (float) value);
			break;
		case CHECK:
			preferences.putBoolean(preferenceKey, (boolean) value);
			break;
		case FONT:
			if (value != null) {
				preferences.put(preferenceKey, ((FontData) value).toString());
			} else {
				preferences.put(preferenceKey, "");
			}
			break;
		case ZONEID:
			Map<String, ZoneId> zones = CustomTimeZone.getZones(l);
			String id = value.toString().substring(value.toString().lastIndexOf(")") + 2);
			String zoneId = CustomTimeZone.getId(zones, id, l).toString();
			preferences.put(preferenceKey, zoneId);
			break;
		default:
			break;
		}
	}
}
