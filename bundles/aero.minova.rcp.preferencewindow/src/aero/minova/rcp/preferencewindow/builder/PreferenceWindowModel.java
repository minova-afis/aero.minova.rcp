package aero.minova.rcp.preferencewindow.builder;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.MApplication;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.form.setup.util.XBSUtil;
import aero.minova.rcp.form.setup.xbs.Preferences;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.control.CustomTimeZone;

public class PreferenceWindowModel {

	private static final String PREFERENCES_GENERAL = "@Preferences.General";

	private Locale locale;

	@Inject
	MApplication mApplication;

	ILog logger = Platform.getLog(this.getClass());

	// Allgemeine Einstellungen aus XBS Datei
	private Map<String, String> xbsPreferences;

	public PreferenceWindowModel(Locale locale) {
		this.locale = locale;
	}

	public List<PreferenceTabDescriptor> createModel(IEclipseContext context) {
		TranslationService translationService = context.get(TranslationService.class);
		IExtensionRegistry extensionRegistry = context.get(IExtensionRegistry.class);
		Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);
		xbsPreferences = XBSUtil.getMapOfNode(preferences, "Settings");

		List<PreferenceTabDescriptor> cprf = new ArrayList<>();

		cprf.add(buildAnwendungsTab(translationService));
		cprf.add(buildDarstellungsTab(translationService));
		cprf.add(buildErweiterungTab(translationService));
		cprf.add(buildDruckenTab(translationService));

		IExtensionPoint point = extensionRegistry.getExtensionPoint("minova.preferencepage");
		for (IExtension extension : point.getExtensions()) {
			// find the category first
			for (IConfigurationElement element : extension.getConfigurationElements()) {
				try {
					PreferenceTabDescriptor createExecutableExtension = (PreferenceTabDescriptor) element.createExecutableExtension("class");
					cprf.add(createExecutableExtension);
				} catch (CoreException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		return cprf;
	}

	private PreferenceTabDescriptor buildAnwendungsTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd = new PreferenceTabDescriptor("FreeTables.Application", "applicationTab",
				translationService.translate("@Preferences.Application", null), 0.1);
		PreferenceSectionDescriptor psd = new PreferenceSectionDescriptor("GeneralExecution", translationService.translate(PREFERENCES_GENERAL, null), 0.1);
		ptd.add(psd);

		psd.add(new PreferenceDescriptor("DefaultWorkspace", translationService.translate("@Preferences.DefaultWorkspace", null),
				translationService.translate("@Preferences.DefaultWorkspace.Tooltip", null), 0.2, DisplayType.CUSTOMCHECK, false));
		psd.add(new PreferenceDescriptor("CurrentWorkspace", translationService.translate("@Preferences.CurrentWorkspace", null), null, 0.3,
				DisplayType.CUSTOMCHECK, false));
		psd.add(new PreferenceDescriptor("SendLogs", translationService.translate("@Preferences.SendLogs", null), null, 0.4, DisplayType.SENDLOGSBUTTON,
				false));
		psd = new PreferenceSectionDescriptor("KeyboardNavigation", translationService.translate("@Preferences.KeyboardNavigation", null), 0.2);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.ENTER_SELECTS_FIRST_REQUIRED,
				translationService.translate("@Preferences.EnterSelectFirstRequired", null), null, 0.1, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.LOOKUP_ENTER_SELECTS_NEXT_REQUIRED,
				translationService.translate("@Preferences.LookupEnterSelectNextRequired", null),
				translationService.translate("@Preferences.LookupEnterSelectNextRequired.Tooltip", null), 0.2, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SELECT_ALL_CONTROLS, translationService.translate("@Preferences.SelectAllControls", null), null,
				0.3, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.GRID_TAB_NAVIGATION, translationService.translate("@Preferences.GridTabNavigation", null),
				translationService.translate("@Preferences.GridTabNavigation.Tooltip", null), 0.4, DisplayType.CHECK, true));
		return ptd;
	}

	private PreferenceTabDescriptor buildDarstellungsTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;

		ptd = new PreferenceTabDescriptor("Languages", "designTab", translationService.translate("@Preferences.Layout", null), 0.2);
		psd = new PreferenceSectionDescriptor("GeneralDesign", translationService.translate(PREFERENCES_GENERAL, null), 0.1);
		ptd.add(psd);

		psd.add(new PreferenceDescriptor("ResetUI", translationService.translate("@Preferences.ResetUI", null), null, 0.1, DisplayType.RESETUIBUTTON, false));

		psd.add(new PreferenceDescriptor(ApplicationPreferences.LOCALE_LANGUAGE, translationService.translate("@Preferences.LocalLanguage", null), null, 0.2,
				DisplayType.LOCALE, Locale.getDefault().getDisplayLanguage(Locale.getDefault())));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.TIMEZONE, translationService.translate("@Preferences.Timezone", null), null, 0.3,
				DisplayType.ZONEID, CustomTimeZone.displayTimeZone(ZoneId.systemDefault().getDisplayName(TextStyle.FULL, locale), locale),
				CustomTimeZone.getTimeZones(locale).toArray()));

		psd = new PreferenceSectionDescriptor("Themes", translationService.translate("@Preferences.Themes", null), 0.2);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.FONT_ICON_SIZE, translationService.translate("@Preferences.FontSize", null), null, 0.1,
				DisplayType.COMBO, "M", "S", "M", "L", "XL"));
		psd = new PreferenceSectionDescriptor("Formatting", translationService.translate("@Preferences.Formatting", null), 0.3);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.DATE_UTIL, translationService.translate("@Preferences.DateUtilPattern", null), null, 0.1,
				DisplayType.DATE_UTIL, ""));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.TIME_UTIL, translationService.translate("@Preferences.TimeUtilPattern", null), null, 0.2,
				DisplayType.TIME_UTIL, ""));

		return ptd;
	}

	private PreferenceTabDescriptor buildErweiterungTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("Extended", "expandedTab", translationService.translate("@Preferences.Advanced", null), 0.3);
		psd = new PreferenceSectionDescriptor("GeneralExpanded", translationService.translate(PREFERENCES_GENERAL, null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.AUTO_LOAD_INDEX, translationService.translate("@Preferences.AutoLoadIndex", null), null, 0.1,
				DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.AUTO_RELOAD_INDEX, translationService.translate("@Preferences.AutoReloadIndex", null), null,
				0.2, DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SHOW_DISCARD_CHANGES_DIALOG_INDEX,
				translationService.translate("@Preferences.ShowDiscardChangesDialogIndex", null),
				translationService.translate("@Preferences.ShowDiscardChangesDialogIndex.Tooltip", null), 0.6, DisplayType.CHECK, false));

		String xbsShowDeleteString = xbsPreferences.get(Constants.XBS_SHOW_DELETE_DIALOG);
		boolean xbsShowDelete = xbsShowDeleteString == null || Boolean.parseBoolean(xbsShowDeleteString); // xbs
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SHOW_DELETE_WARNING, translationService.translate("@Preferences.ShowDeleteWarning", null), null,
				0.8, DisplayType.CHECK, xbsShowDelete));

		psd = new PreferenceSectionDescriptor("Table", translationService.translate("@Form.Index", null), 0.3);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.TABLE_SELECTION_BUFFER_MS,
				translationService.translate("@Preferences.Table.TableSelectionBufferMs", null),
				translationService.translate("@Preferences.Table.TableSelectionBufferMs.Tooltip", null), 0.1, DisplayType.INTEGER, 150));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.INDEX_LIMIT, translationService.translate("@Preferences.Table.IndexLimit", null),
				translationService.translate("@Preferences.Table.IndexLimit.Tooltip", null), 0.2, DisplayType.INTEGER, 1000));

		psd = new PreferenceSectionDescriptor("Timeout", translationService.translate("@Preferences.Timeout", null), 0.4);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.TIMEOUT_CAS, translationService.translate("@Preferences.Timeout.TimeoutCas", null), null, 0.1,
				DisplayType.INTEGER, 15));

		psd = new PreferenceSectionDescriptor("ExpertMode", translationService.translate("@Preferences.ExpertMode", null), 0.7);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SHOW_HIDDEN_SECTIONS, //
				translationService.translate("@Preferences.ShowHiddenSections", null), null, 0.1, DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.DISABLE_FILE_CACHE, //
				translationService.translate("@Preferences.DisableFileCache", null),
				translationService.translate("@Preferences.DisableFileCache.Tooltip", null), 0.2, DisplayType.CHECK, false));

		return ptd;
	}

	private PreferenceTabDescriptor buildDruckenTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("Printer", "printTab", translationService.translate("@Preferences.Print", null), 0.4);
		psd = new PreferenceSectionDescriptor("print", translationService.translate("@Preferences.Print", null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.CREATE_XML_XS, translationService.translate("@Preferences.Print.CreateXMLXSL", null), null, 0.1,
				DisplayType.CHECK, false));

		psd.add(new PreferenceDescriptor(ApplicationPreferences.OPTIMIZED_WIDTHS, translationService.translate("@Preferences.Print.OptimizeWidths", null), null,
				0.3, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.HIDE_EMPTY_COLS, translationService.translate("@Preferences.Print.HideEmptyCols", null), null,
				0.4, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.HIDE_GROUP_COLS, translationService.translate("@Preferences.Print.HideGroupCols", null), null,
				0.5, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.HIDE_SEARCH_CRITERIAS,
				translationService.translate("@Preferences.Print.HideSearchCriterias", null), null, 0.6, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.DISABLE_PREVIEW, translationService.translate("@Preferences.Print.DisablePreview", null), null,
				0.7, DisplayType.CHECK, false));

		return ptd;
	}
}
