package aero.minova.rcp.preferencewindow.pages;

import static org.eclipse.jface.dialogs.PlainMessageDialog.getBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.ILocaleChangeService;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MHandler;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.PlainMessageDialog;
import org.eclipse.jface.util.Util;
import org.eclipse.nebula.widgets.opal.preferencewindow.PWTab;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWDirectoryChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWFileChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWPasswordText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWSeparator;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWTextarea;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWURLText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.preferencewindow.builder.PreferenceDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceSectionDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceTabDescriptor;
import aero.minova.rcp.preferencewindow.builder.PreferenceWindowModel;
import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.preferencewindow.control.CustomPWCheckbox;
import aero.minova.rcp.preferencewindow.control.CustomPWCombo;
import aero.minova.rcp.preferencewindow.control.CustomPWFloatText;
import aero.minova.rcp.preferencewindow.control.CustomPWFontChooser;
import aero.minova.rcp.preferencewindow.control.CustomPWIntegerText;
import aero.minova.rcp.preferencewindow.control.CustomPWStringText;
import aero.minova.rcp.preferencewindow.control.DateFormattingWidget;
import aero.minova.rcp.preferencewindow.control.PWLocale;
import aero.minova.rcp.preferencewindow.control.SendLogsButton;
import aero.minova.rcp.preferencewindow.control.TextButtonForCurrentWorkspace;
import aero.minova.rcp.preferencewindow.control.TextButtonForDefaultWorkspace;
import aero.minova.rcp.preferencewindow.control.TimeFormattingWidget;

@SuppressWarnings("restriction")
public class ApplicationPreferenceWindowHandler {

	// Konstante für den Pfad der .prefs erstellen
	Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);

	@Inject
	ILocaleChangeService lcs;

	@Inject
	@Named(TranslationService.LOCALE)
	Locale s;

	@Inject
	MApplication application;

	@Inject
	TranslationService translationService;

	@Inject
	IDataService dataService;

	@Inject
	EHandlerService handlerService;

	@Inject
	EModelService modelService;

	IWorkbench workbench;

	ILog logger = Platform.getLog(this.getClass());

	@Execute
	public void execute(IThemeEngine themeEngine, IWorkbench workbench, IEclipseContext context) {
		PreferenceWindowModel pwm = new PreferenceWindowModel(s);
		ContextInjectionFactory.inject(pwm, application.getContext()); // In Context injected, damit TranslationService genutzt werden kann

		this.workbench = workbench;

		// Shell des Windows der Application finden
		MWindow appWindow = application.getChildren().get(0);
		Shell shell = (Shell) appWindow.getWidget();
		// Die Shell des Windows deaktivieren
		shell.setEnabled(false);

		String currentTheme = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.FONT_ICON_SIZE, DisplayType.COMBO, "M", s);
		boolean curentSelectAllControls = (boolean) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.SELECT_ALL_CONTROLS,
				DisplayType.CHECK, true, s);
		List<PreferenceTabDescriptor> preferenceTabs = pwm.createModel(context);
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

		// Preference Handler finden
		List<MHandler> preferenceHandlers = modelService.findElements(application, "aero.minova.rcp.rcp.handler.preferencehandler", MHandler.class);
		MHandler preferenceHandler = preferenceHandlers.get(0);
		// Preference Handler deaktivieren
		handlerService.deactivateHandler("org.eclipse.ui.window.preferences", preferenceHandler.getObject());

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
						if (pref.getDisplayType() != DisplayType.ZONEID && pref.getDisplayType() != DisplayType.CUSTOMCHECK) {
							InstancePreferenceAccessor.putValue(preferences, pref.getKey(), pref.getDisplayType(), window.getValueFor(pref.getKey()), s);
						}
					}
				}

			}
			try {
				preferences.flush();
				lcs.changeApplicationLocale(CustomLocale.getLocale());
			} catch (BackingStoreException | NullPointerException e) {
				logger.error(e.getMessage(), e);
			}
		}

		if (Util.isValid(shell)) {
			// Die Shell des Windows aktivieren
			shell.setEnabled(true);
			// Preference Handler wieder aktivieren
			handlerService.activateHandler("org.eclipse.ui.window.preferences", preferenceHandler.getObject());
		}

		boolean newSelectAllControls = (boolean) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.SELECT_ALL_CONTROLS, DisplayType.CHECK,
				true, s);
		String newTheme = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.FONT_ICON_SIZE, DisplayType.COMBO, "M", s);
		if (!currentTheme.equals(newTheme) || !curentSelectAllControls == newSelectAllControls) {
			Shell activeShell = Display.getCurrent().getActiveShell();

			PlainMessageDialog confirmRestart = getBuilder(activeShell, translationService.translate("@Action.Restart", null))
					.buttonLabels(List.of(translationService.translate("@Action.Restart", null), translationService.translate("@Abort", null)))
					.message(translationService.translate("@Preferences.RestartMessage", null)).build();

			int openConfirm = confirmRestart.open();
			if (openConfirm == 0) {
				if (!currentTheme.equals(newTheme)) {
					updateTheme(newTheme, themeEngine, workbench);
				} else {
					workbench.restart();
				}
			}
		}

	}

	private void updateTheme(String newTheme, IThemeEngine themeEngine, IWorkbench workbench) {
		if ("M".equals(newTheme)) {
			themeEngine.setTheme("aero.minova.rcp.defaulttheme", true);
		} else {
			themeEngine.setTheme("aero.minova.rcp.defaulttheme-" + newTheme, true);
		}

		workbench.restart();
	}

	public Map<String, Object> fillData(List<PreferenceTabDescriptor> preferenceTabs) {
		Map<String, Object> data = new HashMap<>();

		for (PreferenceTabDescriptor tab : preferenceTabs) {

			for (PreferenceSectionDescriptor section : tab.getSections()) {

				for (PreferenceDescriptor pref : section.getPreferences()) {
					String key = pref.getKey();
					Object defaultValue = pref.getDefaultValue();
					if (pref.getDisplayType() != DisplayType.CUSTOMCHECK) {
						data.put(key, InstancePreferenceAccessor.getValue(preferences, pref.getKey(), pref.getDisplayType(), defaultValue, s));
					}

				}
			}
		}
		data.put(ApplicationPreferences.COUNTRY, InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.COUNTRY, DisplayType.LOCALE,
				Locale.getDefault().getDisplayCountry(Locale.getDefault()), s));

		return data;
	}

	public PWWidget createWidgets(PWTab tab, PreferenceDescriptor pref, String key, @Optional TranslationService translationService, Object... values) {
		PWWidget widget = null;
		switch (pref.getDisplayType()) {
		case STRING:
			widget = new CustomPWStringText(pref.getLabel(), pref.getTooltip(), key);
			break;
		case INTEGER:
			widget = new CustomPWIntegerText(pref.getLabel(), pref.getTooltip(), key);
			break;
		case FLOAT:
			widget = new CustomPWFloatText(pref.getLabel(), pref.getTooltip(), key);
			break;
		case FILE:
			widget = new PWFileChooser(pref.getLabel(), key).setIndent(25);
			break;
		case DIRECTORY:
			widget = new PWDirectoryChooser(pref.getLabel(), key).setIndent(25);
			break;
		case ZONEID:
			widget = new CustomPWCombo(pref.getLabel(), pref.getTooltip(), key, values).setAlignment(GridData.FILL);
			break;
		case COMBO:
			widget = new CustomPWCombo(pref.getLabel(), pref.getTooltip(), key, values).setWidth(50);
			break;
		case CHECK:
			widget = new CustomPWCheckbox(pref.getLabel(), pref.getTooltip(), key).setIndent(25).setAlignment(SWT.FILL);
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
			widget = new CustomPWFontChooser(pref.getLabel(), pref.getTooltip(), key, translationService);
			break;
		case LOCALE:
			widget = new PWLocale(pref.getLabel(), pref.getTooltip(), ApplicationPreferences.LOCALE_LANGUAGE, application.getContext(), translationService,
					dataService).setAlignment(GridData.FILL);
			break;
		case CUSTOMCHECK:
			if (pref.getKey().equals("DefaultWorkspace")) {
				widget = new TextButtonForDefaultWorkspace(pref.getLabel(), pref.getTooltip(), key, application.getContext()).setIndent(25);
			} else {
				widget = new TextButtonForCurrentWorkspace(pref.getLabel(), pref.getTooltip(), key, application.getContext()).setIndent(25);
			}
			break;
		case DATE_UTIL:
			widget = new DateFormattingWidget(pref.getLabel(), pref.getTooltip(), key, translationService, s).setIndent(25);
			break;
		case TIME_UTIL:
			widget = new TimeFormattingWidget(pref.getLabel(), pref.getTooltip(), key, translationService, s).setIndent(25);
			break;
		case SENDLOGSBUTTON:
			widget = new SendLogsButton(pref.getLabel(), pref.getTooltip(), key, translationService, dataService);
			break;
		default:
			break;
		}
		tab.add(widget);
		return widget;
	}
}
