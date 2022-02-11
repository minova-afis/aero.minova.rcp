package aero.minova.rcp.preferencewindow.builder;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.control.CustomLocale;

public class ComboHeightAdjust {

	private static Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
	private static String fontSize = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.FONT_ICON_SIZE, DisplayType.COMBO, "M",
			CustomLocale.getLocale());

	public static int getComboHeight() {
		int size = 25;

		switch (fontSize) {
		case "S":
			size = 16;
			break;
		case "M":
			size = 24;
			break;
		case "L":
			size = 26;
			break;
		case "XL":
			size = 28;
			break;
		default:
			break;
		}

		return size;
	}
}
