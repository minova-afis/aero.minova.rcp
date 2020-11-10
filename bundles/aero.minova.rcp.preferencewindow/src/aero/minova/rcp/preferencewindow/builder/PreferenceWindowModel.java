package aero.minova.rcp.preferencewindow.builder;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.e4.core.services.translation.TranslationService;

import aero.minova.rcp.preferencewindow.control.CustomTimeZone;

public class PreferenceWindowModel {

	public static final String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";

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
		PreferenceTabDescriptor ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow",
				"icons/Application.png", "applicationTab",
				translationService.translate("@Preferences.Application", null), 0.1);
		PreferenceSectionDescriptor psd = new PreferenceSectionDescriptor("generalexecution",
				translationService.translate("@Preferences.General", null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("licenceWarning",
				translationService.translate("@Preferences.General.LicenceWarningBeforeWeeks", null), 0.1,
				DisplayType.INTEGER, 0));
		return ptd;
	}

	private PreferenceTabDescriptor buildDarstellungsTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/Locale.png", "designTab",
				translationService.translate("@Preferences.Layout", null), 0.2);
		psd = new PreferenceSectionDescriptor("generaldesign",
				translationService.translate("@Preferences.General", null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("language", translationService.translate("@Preferences.General.LocalLanguage", null),
				0.1, DisplayType.LOCALE, Locale.getDefault().getDisplayLanguage(Locale.getDefault())));
		psd.add(new PreferenceDescriptor("timezone", "Zeitzone", 0.3, DisplayType.ZONEID,
				CustomTimeZone.displayTimeZone(ZoneId.systemDefault().getDisplayName(TextStyle.FULL, locale), locale),
				CustomTimeZone.getTimeZones(locale).toArray()));

		psd = new PreferenceSectionDescriptor("designpreferences",
				translationService.translate("@Preference.Themes", null), 0.2);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("font", translationService.translate("@Preferences.FontSize", null), 0.1,
				DisplayType.COMBO, "M", "S", "M", "L", "XL"));
		psd.add(new PreferenceDescriptor("symbolMenu", translationService.translate("@Preferences.IconSize", null), 0.2,
				DisplayType.COMBO, "24x24", "16x16", "24x24", "32x32", "48x48", "64x64"));
		psd.add(new PreferenceDescriptor("symbolToolbar",
				translationService.translate("@Preferences.IconSizeBig", null), 0.3, DisplayType.COMBO, "32x32",
				"16x16", "24x24", "32x32", "48x48", "64x64"));
		return ptd;
	}

	private PreferenceTabDescriptor buildErweiterungTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/Extended.png", "expandedTab",
				translationService.translate("@Preferences.Advanced", null), 0.3);
		psd = new PreferenceSectionDescriptor("generalexpanded", "Allgemeines", 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("masks", translationService.translate("@Preferences.AllowMultipleForms", null),
				0.1, DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor("dragdrop",
				translationService.translate("@Preferences.General.DisallowDragAndDrop", null), 0.2, DisplayType.CHECK,
				false));
		psd.add(new PreferenceDescriptor("icons",
				translationService.translate("@Preferences.General.ShowAllActioninToolbar", null), 0.3,
				DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor("indexautoload",
				translationService.translate("@Preferences.General.AutoLoadIndex", null), 0.4, DisplayType.CHECK,
				false));
		psd.add(new PreferenceDescriptor("indexautoupdate",
				translationService.translate("@Preferences.General.AutoReloadIndex", null), 0.5, DisplayType.CHECK,
				false));
		psd.add(new PreferenceDescriptor("reportwindow",
				translationService.translate("@Preferences.General.SheetStylesMessageBoxes", null), 0.6,
				DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("descriptionButton",
				translationService.translate("@Preferences.General.ShowDetailButtonText", null), 0.7, DisplayType.CHECK,
				true));
		psd.add(new PreferenceDescriptor("maskbuffer", translationService.translate("@Preferences.UseFormBuffer", null),
				0.8, DisplayType.CHECK, true));

		psd = new PreferenceSectionDescriptor("buffer", translationService.translate("@Preferences.Buffer", null), 0.2);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("displaybuffer",
				translationService.translate("@Preferences.Buffer.DisplayBufferMs", null), 0.1, DisplayType.INTEGER,
				20));
		psd.add(new PreferenceDescriptor("maxbuffer",
				translationService.translate("@Preferences.Buffer.MaxBufferMs", null), 0.2, DisplayType.INTEGER, 90));

		psd = new PreferenceSectionDescriptor("table", translationService.translate("@Preferences.Table", null), 0.3);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("selectiondelay",
				translationService.translate("@Preferences.Table.TableSelectionBufferMs", null), 0.1,
				DisplayType.INTEGER, 150));

		psd = new PreferenceSectionDescriptor("lookup", translationService.translate("@Preferences.Lookup", null), 0.5);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("sizeautoadjust",
				translationService.translate("@Preferences.Table.AutoResize", null), 0.1, DisplayType.CHECK, false));

		psd = new PreferenceSectionDescriptor("parttable", translationService.translate("@Preferences.Grid", null),
				0.6);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("fadeinbuttontext",
				translationService.translate("@Preferences.Grid.ShowButtonText", null), 0.1, DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor("buttondetailarea",
				translationService.translate("@Preferences.Grid.ShowButtonsInSection", null), 0.2, DisplayType.CHECK,
				true));
		psd.add(new PreferenceDescriptor("showlookups",
				translationService.translate("@Preferences.Grid.ShowLookups", null), 0.3, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("fadeingroups",
				translationService.translate("@Preferences.Grid.ShowGroups", null), 0.4, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("showchangedrow",
				translationService.translate("@Preferences.CHANGED.ShowChangedRows", null), 0.5, DisplayType.CHECK,
				true));

		return ptd;
	}

	private PreferenceTabDescriptor buildDruckenTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/Print.png", "printTab",
				translationService.translate("@Action.Print", null), 0.4);
		psd = new PreferenceSectionDescriptor("print", translationService.translate("@Action.Print", null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("xmlxsdcreate", translationService.translate("@CreateXMLXS", null), 0.1,
				DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor("fontChooser", translationService.translate("@IndexFont", null), 0.2,
				DisplayType.FONT, null));
		psd.add(new PreferenceDescriptor("optimizewidth", translationService.translate("@OptimizeWidths", null), 0.3,
				DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("hideemptycolumn", translationService.translate("@HideEmptyCols", null), 0.4,
				DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("hidegoupcolumns", translationService.translate("@HideGroupCols", null), 0.5,
				DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("hidesearchdetails",
				translationService.translate("@HideSearchCriterias", null), 0.6, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("deactivateinternpreview",
				translationService.translate("@DisablePreview", null), 0.7, DisplayType.CHECK, false));

		return ptd;
	}

	private PreferenceTabDescriptor buildConsoleTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/Console.png", "consoleTab",
				translationService.translate("@Form.Console", null), 0.5);
		psd = new PreferenceSectionDescriptor("console", translationService.translate("@Form.Console", null), 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("maxCharacter", translationService.translate("@Preferences.MaxChars", null),
				0.1, DisplayType.INTEGER, 24000));

		return ptd;
	}

	private PreferenceTabDescriptor buildSISTab(TranslationService translationService) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/SIS.png", "sisTab",
				translationService.translate("@WorkingTime", null), 0.6);
		psd = new PreferenceSectionDescriptor("user", translationService.translate("@WorkingTime.UserPreselect", null),
				0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("user",
				translationService.translate("@WorkingTime.UserPreselectDescription", null), 0.1, DisplayType.STRING,
				"bauer"));

		return ptd;
	}

}
