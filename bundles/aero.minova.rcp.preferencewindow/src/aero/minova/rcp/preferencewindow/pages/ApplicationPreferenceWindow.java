package aero.minova.rcp.preferencewindow.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.ILocaleChangeService;
import org.eclipse.e4.core.services.translation.TranslationService;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.preferencewindow.builder.PreferenceDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceSectionDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceTabDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceWindowModel;
import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.preferencewindow.control.CustomPWCheckBox;
import aero.minova.rcp.preferencewindow.control.CustomPWFloatText;
import aero.minova.rcp.preferencewindow.control.CustomPWFontChooser;
import aero.minova.rcp.preferencewindow.control.CustomPWIntegerText;
import aero.minova.rcp.preferencewindow.control.CustomPWStringText;
import aero.minova.rcp.preferencewindow.control.PWLocale;

public class ApplicationPreferenceWindow {

	// Konstante für den Pfad der .prefs erstellen
	Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);

	// Widget Builder Impelentierung
	private PreferenceWindowModel pwm;

	@Inject
	IEclipseContext context;

	@Inject
	ILocaleChangeService lcs;

	@Inject
	@Named(TranslationService.LOCALE)
	Locale s;

	@Inject
	TranslationService translationService;

	@Execute
	public void execute() {
		pwm = new PreferenceWindowModel(s);

		Shell shell = new Shell();

		List<PreferenceTabDescriptor> preferenceTabs = pwm.createModel(translationService);
		Map<String, Object> data = fillData(preferenceTabs);
		PreferenceWindow window = PreferenceWindow.create(shell, data);

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
					createWidgets(newTab, pref, key, translationService, values);
				}
			}
		}

		window.setSelectedTab(0);
		if (window.open()) {
			InstancePreferenceAccessor.putValue(preferences, ApplicationPreferences.TIMEZONE, DisplayType.ZONEID,
					window.getValueFor(ApplicationPreferences.TIMEZONE), s);
			InstancePreferenceAccessor.putValue(preferences, ApplicationPreferences.LOCALE_LANGUAGE, DisplayType.LOCALE,
					window.getValueFor(ApplicationPreferences.LOCALE_LANGUAGE), s);
			InstancePreferenceAccessor.putValue(preferences, ApplicationPreferences.COUNTRY, DisplayType.LOCALE,
					window.getValueFor(ApplicationPreferences.COUNTRY), s);
			for (PreferenceTabDescriptor tab : preferenceTabs) {

				for (PreferenceSectionDescriptor section : tab.getSections()) {

					for (PreferenceDescriptor pref : section.getPreferences()) {
						if (pref.getDisplayType() != DisplayType.ZONEID
								&& pref.getDisplayType() != DisplayType.CUSTOMCHECK) {
							InstancePreferenceAccessor.putValue(preferences, pref.getKey(), pref.getDisplayType(),
									window.getValueFor(pref.getKey()), s);
						}
					}
				}

			}
			try {
				preferences.flush();
				Display display = Display.getDefault();
				if (display == null) {
					System.out.println("Display = null");
				}
				if (display.getThread() != Thread.currentThread()) {
					System.out.println("Invalid Access");
				}
				// eventuell muss es synchronisiert ausgeführt werden.
				lcs.changeApplicationLocale(CustomLocale.getLocale());
			} catch (BackingStoreException | NullPointerException e) {
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
					Object defaultValue = pref.getDefaultValue();
					if (pref.getDisplayType() != DisplayType.CUSTOMCHECK) {
						data.put(key, InstancePreferenceAccessor.getValue(preferences, pref.getKey(),
								pref.getDisplayType(), defaultValue, s));
					}

				}
			}
		}
		data.put("country", InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.COUNTRY, DisplayType.LOCALE,
				Locale.getDefault().getDisplayCountry(Locale.getDefault()), s));

		return data;
	}

	public PWWidget createWidgets(PWTab tab, PreferenceDescriptor pref, String key, @Optional TranslationService translationService, Object... values) {
		PWWidget widget = null;
		switch (pref.getDisplayType()) {
		case STRING:
			widget = new CustomPWStringText(pref.getLabel(), key);
			break;
		case INTEGER:
			widget = new CustomPWIntegerText(pref.getLabel(), key);
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
			widget = new PWCombo(pref.getLabel(), key, values).setAlignment(GridData.FILL);
			break;
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
			widget = new CustomPWFontChooser(pref.getLabel(), key, translationService);
			break;
		case LOCALE:
			widget = new PWLocale(pref.getLabel(), "language", context, translationService).setAlignment(GridData.FILL);
			break;
		case CUSTOMCHECK:
			widget = new CustomPWCheckBox(pref.getLabel(), key, translationService).setWidth(200).setIndent(25);
			break;
		default:
			break;
		}
		tab.add(widget);
		return widget;

	}

}
