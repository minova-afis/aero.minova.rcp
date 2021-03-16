package aero.minova.rcp.preferencewindow.builder;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.e4.core.services.translation.TranslationService;

import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.control.CustomTimeZone;

public class PreferenceWindowModel {

	private Locale locale;

	public PreferenceWindowModel(Locale locale) {
		this.locale = locale;
	}

	public List<PreferenceTabDescriptor> createModel(TranslationService translationService) {

		List<PreferenceTabDescriptor> cprf = new ArrayList<>();

		cprf.add(buildAnwendungsTab(translationService));

		cprf.add(buildDarstellungsTab(translationService));

		cprf.add(buildErweiterungTab(translationService));

		cprf.add(buildDruckenTab(translationService));

		cprf.add(buildConsoleTab(translationService));

		cprf.add(buildSISTab(translationService));

		return cprf;
	}

	private PreferenceTabDescriptor buildAnwendungsTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/Application.png", "applicationTab",
				translationService.translate("@Preferences.Application", null), 0.1);
		PreferenceSectionDescriptor psd = new PreferenceSectionDescriptor("GeneralExecution", translationService.translate("@Preferences.General", null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.LICENCE_WARNING_BEFORE_WEEKS,
				translationService.translate("@Preferences.General.LicenceWarningBeforeWeeks", null), 0.1, DisplayType.INTEGER, 0));
		psd.add(new PreferenceDescriptor("DefaultWorkspace", translationService.translate("@Preferences.DefaultWorkspace", null), 0.2, DisplayType.CUSTOMCHECK,
				false));
		psd = new PreferenceSectionDescriptor("KeyboardNavigation", translationService.translate("@Preferences.KeyboardNavigation", null), 0.2);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.ENTER_SELECTS_FIRST_REQUIRED,
				translationService.translate("@Preferences.EnterSelectFirstRequired", null), 0.1, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.LOOKUP_ENTER_SELECTS_NEXT_REQUIRED,
				translationService.translate("@Preferences.LookupEnterSelectNextRequired", null), 0.2, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SELECT_ALL_CONTROLS, translationService.translate("@Preferences.SelectAllControls", null), 0.3,
				DisplayType.CHECK, true));
		return ptd;
	}

	private PreferenceTabDescriptor buildDarstellungsTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/Locale.png", "designTab",
				translationService.translate("@Preferences.Layout", null), 0.2);
		psd = new PreferenceSectionDescriptor("GeneralDesign", translationService.translate("@Preferences.General", null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.LOCALE_LANGUAGE, translationService.translate("@Preferences.General.LocalLanguage", null), 0.1,
				DisplayType.LOCALE, Locale.getDefault().getDisplayLanguage(Locale.getDefault())));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.TIMEZONE, "Zeitzone", 0.3, DisplayType.ZONEID,
				CustomTimeZone.displayTimeZone(ZoneId.systemDefault().getDisplayName(TextStyle.FULL, locale), locale),
				CustomTimeZone.getTimeZones(locale).toArray()));

		psd = new PreferenceSectionDescriptor("Themes", translationService.translate("@Preference.Themes", null), 0.2);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.FONT_SIZE, translationService.translate("@Preferences.FontSize", null), 0.1, DisplayType.COMBO,
				"M", "S", "M", "L", "XL"));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.ICON_SIZE, translationService.translate("@Preferences.IconSize", null), 0.2, DisplayType.COMBO,
				"24x24", "16x16", "24x24", "32x32", "48x48", "64x64"));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.ICON_SIZE_BIG, translationService.translate("@Preferences.IconSizeBig", null), 0.3,
				DisplayType.COMBO, "32x32", "16x16", "24x24", "32x32", "48x48", "64x64"));

		psd = new PreferenceSectionDescriptor("Fromatting", translationService.translate("@Preferences.Formatting", null), 0.3);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.DATE_UTIL, translationService.translate("@Preferences.DateUtilFormatStyle", null), 0.1,
				DisplayType.COMBO, "", "", "SHORT", "MEDIUM", "LONG", "FULL"));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.TIME_UTIL, translationService.translate("@Preferences.TimeUtilFormatStyle", null), 0.1,
				DisplayType.COMBO, "", "", "SHORT", "MEDIUM", "LONG", "FULL"));
		return ptd;
	}

	private PreferenceTabDescriptor buildErweiterungTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/Extended.png", "expandedTab",
				translationService.translate("@Preferences.Advanced", null), 0.3);
		psd = new PreferenceSectionDescriptor("GeneralExpanded", translationService.translate("@Preferences.General", null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.ALLOW_MULTIPLE_FORMS, translationService.translate("@Preferences.AllowMultipleForms", null),
				0.1, DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.DISALLOW_DRAG_AND_DROP,
				translationService.translate("@Preferences.General.DisallowDragAndDrop", null), 0.2, DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SHOW_ALL_ACTION_IN_TOOLBAR,
				translationService.translate("@Preferences.General.ShowAllActioninToolbar", null), 0.3, DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.AUTO_LOAD_INDEX, translationService.translate("@Preferences.General.AutoLoadIndex", null), 0.4,
				DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.AUTO_RELOAD_INDEX, translationService.translate("@Preferences.General.AutoReloadIndex", null),
				0.5, DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SHEET_STYLES_MESSAGE_BOXES,
				translationService.translate("@Preferences.General.SheetStylesMessageBoxes", null), 0.6, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SHOW_DETAIL_BUTTON_TEXT,
				translationService.translate("@Preferences.General.ShowDetailButtonText", null), 0.7, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.USE_FORM_BUFFER, translationService.translate("@Preferences.UseFormBuffer", null), 0.8,
				DisplayType.CHECK, true));

		psd = new PreferenceSectionDescriptor("Buffer", translationService.translate("@Preferences.Buffer", null), 0.2);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.DISPLAY_BUFFER_MS, translationService.translate("@Preferences.Buffer.DisplayBufferMs", null),
				0.1, DisplayType.INTEGER, 20));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.MAX_BUFFER_MS, translationService.translate("@Preferences.Buffer.MaxBufferMs", null), 0.2,
				DisplayType.INTEGER, 90));

		psd = new PreferenceSectionDescriptor("Table", translationService.translate("@Preferences.Table", null), 0.3);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.TABLE_SELECTION_BUFFER_MS,
				translationService.translate("@Preferences.Table.TableSelectionBufferMs", null), 0.1, DisplayType.INTEGER, 150));

		psd = new PreferenceSectionDescriptor("Lookup", translationService.translate("@Preferences.Lookup", null), 0.5);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.AUTO_RESIZE, translationService.translate("@Preferences.Table.AutoResize", null), 0.1,
				DisplayType.CHECK, false));

		psd = new PreferenceSectionDescriptor("Grid", translationService.translate("@Preferences.Grid", null), 0.6);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SHOW_BUTTON_TEXT, translationService.translate("@Preferences.Grid.ShowButtonText", null), 0.1,
				DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SHOW_BUTTON_IN_SECTION,
				translationService.translate("@Preferences.Grid.ShowButtonsInSection", null), 0.2, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SHOW_LOOKUPS, translationService.translate("@Preferences.Grid.ShowLookups", null), 0.3,
				DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SHOW_GROUPS, translationService.translate("@Preferences.Grid.ShowGroups", null), 0.4,
				DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.SHOW_CHANGED_ROWS, translationService.translate("@Preferences.CHANGED.ShowChangedRows", null),
				0.5, DisplayType.CHECK, true));

		return ptd;
	}

	private PreferenceTabDescriptor buildDruckenTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/Print.png", "printTab",
				translationService.translate("@Action.Print", null), 0.4);
		psd = new PreferenceSectionDescriptor("print", translationService.translate("@Action.Print", null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.CREATE_XML_XS, translationService.translate("@CreateXMLXSL", null), 0.1, DisplayType.CHECK,
				false));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.INDEX_FONT, translationService.translate("@IndexFont", null), 0.2, DisplayType.FONT, null));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.OPTIMIZED_WIDTHS, translationService.translate("@OptimizeWidths", null), 0.3, DisplayType.CHECK,
				true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.HIDE_EMPTY_COLS, translationService.translate("@HideEmptyCols", null), 0.4, DisplayType.CHECK,
				true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.HIDE_GROUP_COLS, translationService.translate("@HideGroupCols", null), 0.5, DisplayType.CHECK,
				true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.HIDE_SEARCH_CRITERIAS, translationService.translate("@HideSearchCriterias", null), 0.6,
				DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor(ApplicationPreferences.DISABLE_PREVIEW, translationService.translate("@DisablePreview", null), 0.7, DisplayType.CHECK,
				false));

		return ptd;
	}

	private PreferenceTabDescriptor buildConsoleTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/Console.png", "consoleTab",
				translationService.translate("@Form.Console", null), 0.5);
		psd = new PreferenceSectionDescriptor("console", translationService.translate("@Form.Console", null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.MAX_CHARS, translationService.translate("@Preferences.MaxChars", null), 0.1,
				DisplayType.INTEGER, 24000));

		return ptd;
	}

	private PreferenceTabDescriptor buildSISTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/SIS.png", "sisTab", translationService.translate("@WorkingTime", null),
				0.6);
		psd = new PreferenceSectionDescriptor("user", translationService.translate("@WorkingTime.UserPreselect", null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor(ApplicationPreferences.USER_PRESELECT_DESCRIPTOR,
				translationService.translate("@WorkingTime.UserPreselectDescription", null), 0.1, DisplayType.STRING, "bauer"));

		return ptd;
	}

}
