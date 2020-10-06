package aero.minova.rcp.preferencewindow.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.nebula.widgets.opal.preferencewindow.PWTab;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWCheckbox;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWCombo;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWDirectoryChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWFileChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWPasswordText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWSeparator;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWTextarea;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWURLText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget;
import org.eclipse.swt.layout.GridData;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.preferencewindow.builder.PreferenceDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceSectionDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceTabDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceWindowModel;
import aero.minova.rcp.preferencewindow.control.CustomPWFloatText;
import aero.minova.rcp.preferencewindow.control.CustomPWFontChooser;
import aero.minova.rcp.preferencewindow.control.CustomPWIntegerText;
import aero.minova.rcp.preferencewindow.control.CustomPWStringText;

public class ApplicationPreferenceWindow {

	// Konstante für den Pfad der .prefs erstellen
	public static final String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";
	Preferences preferences = InstanceScope.INSTANCE.getNode(PREFERENCES_NODE);

	// Widget Builder Impelentierung
	private PreferenceWindowModel pwm = new PreferenceWindowModel();

	@Execute
	public void execute() {

		List<PreferenceTabDescriptor> preferenceTabs = pwm.createModel();
		PreferenceWindow window = PreferenceWindow.create(fillData(preferenceTabs));

		for (PreferenceTabDescriptor tabDescriptor : preferenceTabs) {
			// Tab erstellen und hinzufügen
			PWTab newTab = window.addTab(tabDescriptor.getImage(), tabDescriptor.getLabel());

			for (PreferenceSectionDescriptor section : tabDescriptor.getSections()) {
				// Section hinzufügen
				newTab.add(new PWSeparator(section.getLabel()));

				for (PreferenceDescriptor pref : section.getPreferences()) {
					// Preference hinzufügen
					Object[] values = pref.getPossibleValues();
					String key = pref.getKey();
					createWidgets(newTab, pref, key, values);

				}
			}
		}

		window.setSelectedTab(0);
		if (window.open()) {
			for (PreferenceTabDescriptor tab : preferenceTabs) {

				for (PreferenceSectionDescriptor section : tab.getSections()) {

					for (PreferenceDescriptor pref : section.getPreferences()) {
						InstancePreferenceAccessor.putValue(preferences, pref.getKey(), pref.getDisplayType(),
								window.getValueFor(pref.getKey()));
					}
				}

			}
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, Object> fillData(List<PreferenceTabDescriptor> preferenceTabs) {
		Map<String, Object> data = new HashMap<>();

		for (PreferenceTabDescriptor tab : preferenceTabs) {

			for (PreferenceSectionDescriptor section : tab.getSections()) {

				for (PreferenceDescriptor pref : section.getPreferences()) {
					String key = pref.getKey();
					data.put(key,
							InstancePreferenceAccessor.getValue(preferences, pref.getKey(), pref.getDisplayType()));

				}
			}
		}

		return data;
	}

	public PWWidget createWidgets(PWTab tab, PreferenceDescriptor pref, String key, Object... values) {
		PWWidget widget = null;
		switch (pref.getDisplayType()) {
		case STRING:
			widget = new CustomPWStringText(pref.getLabel(), key).setIndent(25);
			break;
		case INTEGER:
			widget = new CustomPWIntegerText(pref.getLabel(), key).setIndent(25);
			break;
		case FLOAT:
			widget = new CustomPWFloatText(pref.getLabel(), key);
			break;
		case FILE:
			widget = new PWFileChooser(pref.getLabel(), key).setIndent(25);
			break;
		case DIRECTORY:
			widget = new PWDirectoryChooser(pref.getLabel(), key).setIndent(25);
			break;
		case ZONEID:
		case COMBO:
			widget = new PWCombo(pref.getLabel(), key, values).setWidth(200);
			break;
		case CHECK:
			widget = new PWCheckbox(pref.getLabel(), key).setAlignment(GridData.FILL).setIndent(25);
			break;
		case URL:
			widget = new PWURLText(pref.getLabel(), key);
			break;
		case PASSWORD:
			widget = new PWPasswordText(pref.getLabel(), key);
			break;
		case TEXT:
			widget = new PWTextarea(pref.getLabel(), key);
			break;
		case FONT:
			widget = new CustomPWFontChooser(pref.getLabel(), key);
			break;
		default:
			break;
		}
		tab.add(widget);
		return widget;

	}

}
