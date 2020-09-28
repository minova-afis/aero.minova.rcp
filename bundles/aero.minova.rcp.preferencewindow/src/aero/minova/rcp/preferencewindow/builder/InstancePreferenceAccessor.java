package aero.minova.rcp.preferencewindow.builder;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
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
			return preferences.get(preferenceKey, "font");
		default:
			break;
		}
		throw new RuntimeException("getValue called but no fitting case");
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
				preferences.put(preferenceKey, (String) value);
				break;
			default:
				break;
			}
	}
//	@Override
//	public void flush(PreferenceSectionDescriptor section, Object value) {
//		Preferences preferences = InstanceScope.INSTANCE.getNode(preferenceNode);
//		putValue(section, value);
//		try {
//			preferences.flush();
//		} catch (BackingStoreException e) {
//			Platform.getLog(this.getClass()).error(e.getMessage());
//		}
//	}
}
