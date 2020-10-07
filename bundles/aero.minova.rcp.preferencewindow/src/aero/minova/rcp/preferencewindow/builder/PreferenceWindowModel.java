package aero.minova.rcp.preferencewindow.builder;

import java.util.ArrayList;
import java.util.List;

import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.preferencewindow.control.CustomTimeZone;

public class PreferenceWindowModel {

	public static final String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";

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
		PreferenceSectionDescriptor psd = new PreferenceSectionDescriptor("executionplace", "Ausführungsort", 0.1);
		ptd.add(psd);
		PreferenceDescriptor pd = new PreferenceDescriptor("file", "Programmverzeichnis", 0.1, DisplayType.FILE);
		psd.add(pd);

		psd = new PreferenceSectionDescriptor("generalexecution", "Allgemeines", 0.2);
		ptd.add(psd);
		pd = new PreferenceDescriptor("licenceWarning", "LizenzWarnung [wochen]", 0.1, DisplayType.INTEGER);
		psd.add(pd);
		return ptd;
	}

	private PreferenceTabDescriptor buildDarstellungsTab() {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		PreferenceDescriptor pd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/design.png", "designTab",
				"Darstellung", 0.2);
		psd = new PreferenceSectionDescriptor("generaldesign", "Allgemeines", 0.1);
		ptd.add(psd);
		pd = new PreferenceDescriptor("land", "Land", 0.1, DisplayType.COMBO, CustomLocale.getCountrys().toArray());
		psd.add(pd);
		pd = new PreferenceDescriptor("language", "Landessprache", 0.2, DisplayType.COMBO,
				CustomLocale.getLanguageForCountry("land").toArray());
		psd.add(pd);
		pd = new PreferenceDescriptor("timezone", "Zeitzone", 0.3, DisplayType.ZONEID,
				CustomTimeZone.getTimeZones().toArray());
		psd.add(pd);

		psd = new PreferenceSectionDescriptor("designpreferences", "Design-Einstellungen", 0.2);
		ptd.add(psd);
		pd = new PreferenceDescriptor("font", "Schriftgröße", 0.1, DisplayType.COMBO, "S", "M", "L", "XL");
		psd.add(pd);
		pd = new PreferenceDescriptor("symbolMenu", "Symbole(Menü, Details)", 0.2, DisplayType.COMBO, "16x16", "24x24",
				"32x32", "48x48", "64x64");
		psd.add(pd);
		pd = new PreferenceDescriptor("symbolToolbar", "Symbole (Toolbar)", 0.3, DisplayType.COMBO, "16x16", "24x24",
				"32x32", "48x48", "64x64");
		psd.add(pd);
		return ptd;
	}

	private PreferenceTabDescriptor buildErweiterungTab() {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		PreferenceDescriptor pd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/erweitert.png", "expandedTab",
				"Erweitert", 0.3);
		psd = new PreferenceSectionDescriptor("generalexpanded", "Allgemeines", 0.1);
		ptd.add(psd);
		pd = new PreferenceDescriptor("masks", "Masken mehrfach öffnen", 0.1, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("dragdrop", "DragDrop deaktivieren", 0.2, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("icons", "Alle Icons in Symbolleiste einblenden ", 0.3, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("indexautoload", "Index beim Öffnen der Maske automatisch laden", 0.4,
				DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("indexautoupdate", "Index automatisch nach dem Speichern aktualisieren", 0.5,
				DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("reportwindow", "Meldungsfenster an Menüleiste", 0.6, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("descriptionButton", "Beschreibung für Schaltflächen einblenden", 0.7,
				DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("maskbuffer", "Masken Puffer benutzen", 0.8, DisplayType.CHECK);
		psd.add(pd);
		psd = new PreferenceSectionDescriptor("buffer", "Puffer", 0.2);
		ptd.add(psd);
		pd = new PreferenceDescriptor("displaybuffer", "Anzeige Puffer [ms]", 0.1, DisplayType.INTEGER);
		psd.add(pd);
		pd = new PreferenceDescriptor("maxbuffer", "Max. Puffer [ms]", 0.2, DisplayType.INTEGER);
		psd.add(pd);

		psd = new PreferenceSectionDescriptor("table", "Tabelle", 0.3);
		ptd.add(psd);
		pd = new PreferenceDescriptor("selectiondelay", "Auswahltverzögerung [ms]", 0.1, DisplayType.INTEGER);
		psd.add(pd);
		pd = new PreferenceDescriptor("sizeautoadjust", "Größe automatisch anpassen", 0.2, DisplayType.CHECK);
		psd.add(pd);

		psd = new PreferenceSectionDescriptor("parttable", "Teiltabelle", 0.5);
		ptd.add(psd);
		pd = new PreferenceDescriptor("fadeinbuttontext", "Schaltflächentext einblenden", 0.1, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("buttondetailarea", "Schaltfläche im Detailbereich", 0.2, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("showlookups", "Zeige Nachschläge", 0.3, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("fadeingroups", "Gruppen einblenden", 0.4, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("showchangedrow", "Zeige geänderte Zeilen", 0.5, DisplayType.CHECK);
		psd.add(pd);

		return ptd;
	}

	private PreferenceTabDescriptor buildDruckenTab() {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		PreferenceDescriptor pd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "", "printTab", "Drucken", 0.4);
		psd = new PreferenceSectionDescriptor("print", "Drucken", 0.1);
		ptd.add(psd);
		pd = new PreferenceDescriptor("xmlxsdcreate", "XML + XDS erstellen", 0.1, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("fontChooser", "Schriftart Inhaltsverzeichnis", 0.2, DisplayType.FONT);
		psd.add(pd);
		pd = new PreferenceDescriptor("optimizewidth", "Breiten optimieren", 0.3, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("hideemptycolumn", "Leere Spalten verbergen", 0.4, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("hidegoupcolumns", "Gruppenspalten verbergen", 0.5, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("hidesearchdetails", "Suchkriterien verbergen", 0.6, DisplayType.CHECK);
		psd.add(pd);
		pd = new PreferenceDescriptor("deactivateinternpreview", "Gruppenspalten verbergen", 0.7, DisplayType.CHECK);
		psd.add(pd);

		return ptd;
	}

	private PreferenceTabDescriptor buildConsoleTab() {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		PreferenceDescriptor pd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "", "consoleTab", "Konsole", 0.5);
		psd = new PreferenceSectionDescriptor("console", "Konsole", 0.1);
		ptd.add(psd);
		pd = new PreferenceDescriptor("maxCharacter", "Max. Zeichen", 0.1, DisplayType.INTEGER);
		psd.add(pd);

		return ptd;
	}

	private PreferenceTabDescriptor buildSISTab() {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		PreferenceDescriptor pd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "", "sisTab", "Stundenerfassung", 0.6);
		psd = new PreferenceSectionDescriptor("user", "Benutzer vorbelegen", 0.1);
		ptd.add(psd);
		pd = new PreferenceDescriptor("user", "Hier können Sie Ihren Benutzer vorbelegen", 0.1, DisplayType.STRING);
		psd.add(pd);

		return ptd;
	}

}
