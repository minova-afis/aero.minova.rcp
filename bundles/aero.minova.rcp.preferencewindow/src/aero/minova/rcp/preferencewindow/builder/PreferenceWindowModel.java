package aero.minova.rcp.preferencewindow.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import aero.minova.rcp.preferencewindow.control.CustomTimeZone;

public class PreferenceWindowModel {

	public static final String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";

	private Locale locale;

	public PreferenceWindowModel(Locale locale) {
		this.locale = locale;
	}

	public List<PreferenceTabDescriptor> createModel() {

		List<PreferenceTabDescriptor> cprf = new ArrayList<>();

		cprf.add(buildAnwendungsTab());

		cprf.add(buildDarstellungsTab());

		cprf.add(buildErweiterungTab());

		cprf.add(buildDruckenTab());

		cprf.add(buildConsoleTab());

		cprf.add(buildSISTab());

		return cprf;
	}

	private PreferenceTabDescriptor buildAnwendungsTab() {
		PreferenceTabDescriptor ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow",
				"icons/application.png", "applicationTab", "Anwendung", 0.1);
		PreferenceSectionDescriptor psd = new PreferenceSectionDescriptor("generalexecution", "Allgemeines", 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("licenceWarning", "LizenzWarnung [wochen]", 0.1, DisplayType.INTEGER, 0));
		return ptd;
	}

	private PreferenceTabDescriptor buildDarstellungsTab() {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/design.png", "designTab",
				"Darstellung", 0.2);
		psd = new PreferenceSectionDescriptor("generaldesign", "Allgemeines", 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("language", "Sprache", 0.1, DisplayType.LOCALE,
				Locale.getDefault().getDisplayLanguage(Locale.getDefault())));
		psd.add(new PreferenceDescriptor("timezone", "Zeitzone", 0.3, DisplayType.ZONEID,
				TimeZone.getDefault().toString(), CustomTimeZone.getTimeZones(locale).toArray()));

		psd = new PreferenceSectionDescriptor("designpreferences", "Design-Einstellungen", 0.2);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("font", "Schriftgröße", 0.1, DisplayType.COMBO, "M", "S", "M", "L", "XL"));
		psd.add(new PreferenceDescriptor("symbolMenu", "Symbole(Menü, Details)", 0.2, DisplayType.COMBO, "24x24",
				"16x16", "24x24", "32x32", "48x48", "64x64"));
		psd.add(new PreferenceDescriptor("symbolToolbar", "Symbole (Toolbar)", 0.3, DisplayType.COMBO, "32x32", "16x16",
				"24x24", "32x32", "48x48", "64x64"));
		return ptd;
	}

	private PreferenceTabDescriptor buildErweiterungTab() {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/erweitert.png", "expandedTab",
				"Erweitert", 0.3);
		psd = new PreferenceSectionDescriptor("generalexpanded", "Allgemeines", 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("masks", "Masken mehrfach öffnen", 0.1, DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor("dragdrop", "DragDrop deaktivieren", 0.2, DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor("icons", "Alle Icons in Symbolleiste einblenden ", 0.3, DisplayType.CHECK,
				false));
		psd.add(new PreferenceDescriptor("indexautoload", "Index beim Öffnen der Maske automatisch laden", 0.4,
				DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor("indexautoupdate", "Index automatisch nach dem Speichern aktualisieren", 0.5,
				DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor("reportwindow", "Meldungsfenster an Menüleiste", 0.6, DisplayType.CHECK,
				true));
		psd.add(new PreferenceDescriptor("descriptionButton", "Beschreibung für Schaltflächen einblenden", 0.7,
				DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("maskbuffer", "Masken Puffer benutzen", 0.8, DisplayType.CHECK, true));

		psd = new PreferenceSectionDescriptor("buffer", "Puffer", 0.2);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("displaybuffer", "Anzeige Puffer [ms]", 0.1, DisplayType.INTEGER, 20));
		psd.add(new PreferenceDescriptor("maxbuffer", "Max. Puffer [ms]", 0.2, DisplayType.INTEGER, 90));

		psd = new PreferenceSectionDescriptor("table", "Tabelle", 0.3);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("selectiondelay", "Auswahltverzögerung [ms]", 0.1, DisplayType.INTEGER, 150));

		psd = new PreferenceSectionDescriptor("lookup", "Nachschlagen", 0.5);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("sizeautoadjust", "Größe automatisch anpassen", 0.1, DisplayType.CHECK,
				false));

		psd = new PreferenceSectionDescriptor("parttable", "Teiltabelle", 0.6);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("fadeinbuttontext", "Schaltflächentext einblenden", 0.1, DisplayType.CHECK,
				false));
		psd.add(new PreferenceDescriptor("buttondetailarea", "Schaltfläche im Detailbereich", 0.2, DisplayType.CHECK,
				true));
		psd.add(new PreferenceDescriptor("showlookups", "Zeige Nachschläge", 0.3, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("fadeingroups", "Gruppen einblenden", 0.4, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("showchangedrow", "Zeige geänderte Zeilen", 0.5, DisplayType.CHECK, true));

		return ptd;
	}

	private PreferenceTabDescriptor buildDruckenTab() {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "", "printTab", "Drucken", 0.4);
		psd = new PreferenceSectionDescriptor("print", "Drucken", 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("xmlxsdcreate", "XML + XDS erstellen", 0.1, DisplayType.CHECK, false));
		psd.add(new PreferenceDescriptor("fontChooser", "Schriftart Inhaltsverzeichnis", 0.2, DisplayType.FONT, null));
		psd.add(new PreferenceDescriptor("optimizewidth", "Breiten optimieren", 0.3, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("hideemptycolumn", "Leere Spalten verbergen", 0.4, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("hidegoupcolumns", "Gruppenspalten verbergen", 0.5, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("hidesearchdetails", "Suchkriterien verbergen", 0.6, DisplayType.CHECK, true));
		psd.add(new PreferenceDescriptor("deactivateinternpreview", "Gruppenspalten verbergen", 0.7, DisplayType.CHECK,
				false));

		return ptd;
	}

	private PreferenceTabDescriptor buildConsoleTab() {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "", "consoleTab", "Konsole", 0.5);
		psd = new PreferenceSectionDescriptor("console", "Konsole", 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("maxCharacter", "Max. Zeichen", 0.1, DisplayType.INTEGER, 24000));

		return ptd;
	}

	private PreferenceTabDescriptor buildSISTab() {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "", "sisTab", "Stundenerfassung", 0.6);
		psd = new PreferenceSectionDescriptor("user", "Benutzer vorbelegen", 0.1);
		ptd.add(psd);
		psd.add(new PreferenceDescriptor("user", "Hier können Sie Ihren Benutzer vorbelegen", 0.1, DisplayType.STRING,
				"bauer"));

		return ptd;
	}

}
