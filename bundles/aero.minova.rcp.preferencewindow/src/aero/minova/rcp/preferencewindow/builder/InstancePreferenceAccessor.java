package aero.minova.rcp.preferencewindow.builder;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.log.Logger;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class InstancePreferenceAccessor implements PreferenceAccessor {

	private Logger logger;
	private String preferenceKey;
	private String preferenceNode;
	private Object preferenceValue;
	private Object[] preferenceValues;

	public InstancePreferenceAccessor(String preferenceNode, String preferenceKey, Logger logger,
			Object... preferenceValues) {
		super();
		this.preferenceNode = preferenceNode;
		this.preferenceKey = preferenceKey;
		this.preferenceValues = preferenceValues;
		this.logger = logger;
	}

	@Override
	public String getKey() {
		return preferenceKey;
	}

	@Override
	public Object getValue(PreferenceSectionDescriptor section) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(preferenceNode);
		for (PreferenceDescriptor pref : section.getPreferences()) {
			switch (pref.getDisplayType()) {
			case STRING:
				preferenceValue = preferences.get(preferenceKey, "");
				break;
			case INTEGER:
				preferenceValue = preferences.getInt(preferenceKey, 0);
				break;
			case FLOAT:
				preferenceValue = preferences.getFloat(preferenceKey, 0);
				break;
			case FILE:
				preferenceValue = preferences.get(preferenceKey, "");
				break;
			case DIRECTORY:
				preferenceValue = preferences.get(preferenceKey, "");
				break;
			case COMBO:
				preferenceValue = preferences.get(preferenceKey, "");
				break;
			case CHECK:
				preferenceValue = preferences.getBoolean(preferenceKey, false);
				break;
			case URL:
				preferenceValue = preferences.get(preferenceKey, "");
				break;
			case PASSWORD:
				preferenceValue = preferences.get(preferenceKey, "");
				break;
			case TEXT:
				preferenceValue = preferences.get(preferenceKey, "");
				break;
			case FONT:
				preferenceValue = preferences.get(preferenceKey, "font");
				break;
			default:
				break;
			}
		}
		return preferenceValue;
	}

	@Override
	public Object[] getPossibleValues() {
		return preferenceValues;
	}

	@Override
	public Object putValue(PreferenceSectionDescriptor section, Object value) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(preferenceNode);
		for (PreferenceDescriptor pref : section.getPreferences()) {
			switch (pref.getDisplayType()) {
			case STRING:
				preferences.put(preferenceKey, (String) value);
				break;
			case INTEGER:
				preferences.putInt(preferenceKey, Integer.valueOf((int) value));
				break;
			case FLOAT:
				preferences.putFloat(preferenceKey, Float.valueOf((float) value));
				break;
			case FILE:
				preferences.put(preferenceKey, (String) value);
				break;
			case DIRECTORY:
				preferences.put(preferenceKey, (String) value);
				break;
			case COMBO:
				preferences.put(preferenceKey, (String) value);
				break;
			case CHECK:
				preferences.putBoolean(preferenceKey, Boolean.valueOf((boolean) value));
				break;
			case URL:
				preferences.put(preferenceKey, (String) value);
				break;
			case PASSWORD:
				preferences.put(preferenceKey, (String) value);
				break;
			case TEXT:
				preferences.put(preferenceKey, (String) value);
				break;
			case FONT:
				preferences.put(preferenceKey, (String) value);
				break;
			default:
				break;
			}
		}
		return preferences;
	}

	@Override
	public void flush(PreferenceSectionDescriptor section, Object value) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(preferenceNode);
		putValue(section, value);
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			logger.error(e);
		}
	}
}
