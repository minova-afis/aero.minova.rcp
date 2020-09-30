package aero.minova.rcp.preferencewindow.builder;

import org.eclipse.swt.graphics.FontData;
import org.osgi.service.prefs.Preferences;

public class InstancePreferenceAccessor  {

	public static Object getValue(Preferences preferences, String preferenceKey, DisplayType type) {
		switch (type) {
		case STRING:
		case FILE:
		case DIRECTORY:
		case COMBO:
		case URL:
		case PASSWORD:
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
			return (fd== null? null:new FontData(fd));
		default:
			break;
		}
		throw new RuntimeException("Keinen passenden Wert gefunden");
	}

	public static void putValue(Preferences preferences, String preferenceKey, DisplayType type, Object value) {
			switch (type) {
			case STRING:
			case FILE:
			case DIRECTORY:
			case COMBO:
			case URL:
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
			default:
				break;
			}
	}

}
