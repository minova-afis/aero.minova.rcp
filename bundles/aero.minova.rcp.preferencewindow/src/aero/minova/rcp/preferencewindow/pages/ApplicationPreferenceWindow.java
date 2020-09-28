package aero.minova.rcp.preferencewindow.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.nebula.widgets.opal.preferencewindow.PWTab;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.graphics.Image;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferencewindow.builder.PreferenceDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceSectionDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceTabDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceWindowBuilder;
import aero.minova.rcp.preferencewindow.builder.PreferenceWindowModel;

public class ApplicationPreferenceWindow {

	// Konstante für den Pfad der .prefs erstellen
	public static final String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";
	Preferences preferences = InstanceScope.INSTANCE.getNode(PREFERENCES_NODE);

	// Widget Builder Impelentierung
	private PreferenceWindowBuilder pwb = PreferenceWindowBuilder.newPWB();
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
				pwb.addTitledSeparator(newTab, section.getLabel());
				
				for (PreferenceDescriptor pref : section.getPreferences()) {
					// Preference hinzufügen
					Object[] keys = pref.getValueAccessor().getPossibleValues();
					String key = pref.getValueAccessor().getKey();
					createWidgets(newTab, pref, key, keys);

				}
			}
		}

		window.setSelectedTab(0);
		if (window.open()) {
			
			// TODO Reicht nicht ein einziger flush?
			for (PreferenceTabDescriptor tab : preferenceTabs) {

				for (PreferenceSectionDescriptor section : tab.getSections()) {

					for (PreferenceDescriptor pref : section.getPreferences()) {
						Object value = window.getValueFor(pref.getValueAccessor().getKey());
						pref.getValueAccessor().flush(section, value);
					}
				}
			}
		}
	}

	public Map<String, Object> fillData(List<PreferenceTabDescriptor> preferenceTabs) {
		Map<String, Object> data = new HashMap<>();

		for (PreferenceTabDescriptor tab : preferenceTabs) {

			for (PreferenceSectionDescriptor section : tab.getSections()) {

				for (PreferenceDescriptor pref : section.getPreferences()) {
					String key = pref.getValueAccessor().getKey();
					data.put(key, pref.getValueAccessor().getValue(section));

				}
			}
		}

		return data;
	}

	public void createWidgets(PWTab tab, PreferenceDescriptor pref, String key, Object... keys) {

		switch (pref.getDisplayType()) {
		case STRING:
			pwb.addStringBox(tab, pref.getLabel(), key);
			break;
		case INTEGER:
			pwb.addIntegerBox(tab, pref.getLabel(), key);
			break;
		case FLOAT:
			pwb.addFloatBox(tab, pref.getLabel(), key);
			break;
		case FILE:
			pwb.addFileChooser(tab, pref.getLabel(), key);
			break;
		case DIRECTORY:
			pwb.addDirectoryChooser(tab, pref.getLabel(), key);
			break;
		case COMBO:
			pwb.addComboBoxRO(tab, pref.getLabel(), key, keys);
			break;
		case CHECK:
			pwb.addCheckbox(tab, pref.getLabel(), key);
			break;
		case URL:
			pwb.addURLBox(tab, pref.getLabel(), key);
			break;
		case PASSWORD:
			pwb.addPasswordBox(tab, pref.getLabel(), key);
			break;
		case TEXT:
			pwb.addTextarea(tab, pref.getLabel(), key);
			break;
		case FONT:
			pwb.addFontChooser(tab, pref.getLabel(), key);
			break;
		default:
			break;
		}

	}

}
