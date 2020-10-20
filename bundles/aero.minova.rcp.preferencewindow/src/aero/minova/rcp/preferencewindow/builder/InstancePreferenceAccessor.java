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
 *
 */
public class InstancePreferenceAccessor {

	/**
	 * Holt den an den übergebenen Key gebunden Wert aus den angegebenen
	 * Preferences.
	 * 
	 * @param preferences
	 * @param preferenceKey
	 * @param type
	 * @return
	 */
	public static Object getValue(Preferences preferences, String preferenceKey, DisplayType type, Locale l) {
		switch (type) {
		case STRING:
		case FILE:
		case DIRECTORY:
		case COMBO:
		case URL:
		case PASSWORD:
		case LOCALE:
		case TEXT:
			return preferences.get(preferenceKey, "");
		case INTEGER:
			return preferences.getInt(preferenceKey, 0);
		case FLOAT:
			return preferences.getFloat(preferenceKey, 0);
		case CHECK:
			return preferences.getBoolean(preferenceKey, false);
		case FONT:
			String fd = preferences.get(preferenceKey, null);
			return (fd == null ? null : new FontData(fd));
		case ZONEID:
			String id = preferences.get(preferenceKey, "");
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
		case STRING:
		case FILE:
		case DIRECTORY:
		case COMBO:
		case URL:
		case LOCALE:
		case PASSWORD:
		case TEXT:
			preferences.put(preferenceKey, (String) value);
			break;
		case INTEGER:
			preferences.putInt(preferenceKey, Integer.valueOf((int) value));
			break;
		case FLOAT:
			preferences.putFloat(preferenceKey, Float.valueOf((float) value));
			break;
		case CHECK:
			preferences.putBoolean(preferenceKey, Boolean.valueOf((boolean) value));
			break;
		case FONT:
			preferences.put(preferenceKey, ((FontData) value).toString());
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
