package aero.minova.rcp.preferencewindow.builder;

import java.util.ArrayList;
import java.util.List;


public class PreferenceWindowModel {

	public static final  String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";

	public List<PreferenceTabDescriptor> createModel() {

		List<PreferenceTabDescriptor> cprf = new ArrayList<>();

		cprf.add(buildAnwendungsTab());
		
		cprf.add(buildDarstellungsTab());

		cprf.add(buildErweiterungTab());
		
		cprf.add(buildDruckenTab());

		return cprf;
	}

	private PreferenceTabDescriptor buildAnwendungsTab() {
		PreferenceTabDescriptor ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow",
				"icons/application.png", "applicationTab", "Anwendung", 0.1);
		PreferenceSectionDescriptor psd = new PreferenceSectionDescriptor("executionplace", "Ausführungsort", 0.1);
		ptd.add(psd);
		PreferenceDescriptor pd = new PreferenceDescriptor("programmDirectory", "Programmverzeichnis", 0.1, DisplayType.FILE);
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
		pd = new PreferenceDescriptor("language", "Landessprache", 0.1, DisplayType.COMBO, "Deutsch", "Englisch");
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
			PreferenceDescriptor preferenceDescriptor;
			ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/erweitert.png", "expandedTab",
					"Erweitert", 0.3);
			psd = new PreferenceSectionDescriptor("generalexpanded", "Allgemeines", 0.1);
			ptd.add(psd);
		
			preferenceDescriptor = new PreferenceDescriptor("myinteger", "Auswahltverzögerung [ms]", 0.1, DisplayType.INTEGER);
			psd.add(preferenceDescriptor);
			
			
			preferenceDescriptor = new PreferenceDescriptor("showchangedrow", "Zeige geänderte Zeilen", 0.5, DisplayType.CHECK);
			psd.add(preferenceDescriptor);
			
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
//		pd = new PreferenceDescriptor(psd, "font", "Schriftart Inhaltsverzeichnis", 0.2, DisplayType.FONT);
//		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "font"));
//		psd.add(pd);
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

}
