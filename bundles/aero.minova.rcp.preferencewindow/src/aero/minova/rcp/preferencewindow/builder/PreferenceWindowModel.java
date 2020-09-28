package aero.minova.rcp.preferencewindow.builder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;

public class PreferenceWindowModel {

	public static final  String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";

	@Inject
	Logger logger;

	public List<PreferenceTabDescriptor> createModel() {

		List<PreferenceTabDescriptor> cprf = new ArrayList<>();

		buildAnwendungsTab(cprf);
		
		buildDarstellungsTab(cprf);

		buildErweiterungTab(cprf);
		
		buildDruckenTab(cprf);

		return cprf;
	}

	private void buildAnwendungsTab(List<PreferenceTabDescriptor> cprf) {
		PreferenceTabDescriptor ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow",
				"icons/application.png", "applicationTab", "Anwendung", 0.1);
		cprf.add(ptd);
		PreferenceSectionDescriptor psd = new PreferenceSectionDescriptor("executionplace", "Ausführungsort", 0.1);
		ptd.add(psd);
		PreferenceDescriptor pd = new PreferenceDescriptor(psd, "programmDirectory", "Programmverzeichnis", 0.1,
				DisplayType.FILE);
		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "file", logger));
		psd.add(pd);
	
		psd = new PreferenceSectionDescriptor("generalexecution", "Allgemeines", 0.2);
		ptd.add(psd);
		pd = new PreferenceDescriptor(psd, "licenceWarning", "LizenzWarnung [wochen]", 0.1, DisplayType.INTEGER);
		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "licenswarning", logger));
		psd.add(pd);
	}

	private void buildDarstellungsTab(List<PreferenceTabDescriptor> cprf) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		PreferenceDescriptor pd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/design.png", "designTab",
				"Darstellung", 0.2);
		cprf.add(ptd);
		psd = new PreferenceSectionDescriptor("generaldesign", "Allgemeines", 0.1);
		ptd.add(psd);
		pd = new PreferenceDescriptor(psd, "language", "Landessprache", 0.1, DisplayType.COMBO);
		pd.setValueAccessor(
				new InstancePreferenceAccessor(PREFERENCES_NODE, "language", logger, "Deutsch", "Englisch"));
		psd.add(pd);
	
		psd = new PreferenceSectionDescriptor("designpreferences", "Design-Einstellungen", 0.2);
		ptd.add(psd);
		pd = new PreferenceDescriptor(psd, "font", "Schriftgröße", 0.1, DisplayType.COMBO);
		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "font", logger, "S", "M", "L", "XL"));
		psd.add(pd);
		pd = new PreferenceDescriptor(psd, "symbolMenu", "Symbole(Menü, Details)", 0.2, DisplayType.COMBO);
		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "symbolMenu", logger, "16x16", "24x24",
				"32x32", "48x48", "64x64"));
		psd.add(pd);
		pd = new PreferenceDescriptor(psd, "symbolToolbar", "Symbole (Toolbar)", 0.3, DisplayType.COMBO);
		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "symbolToolbar", logger, "16x16", "24x24",
				"32x32", "48x48", "64x64"));
		psd.add(pd);
	}

	private void buildErweiterungTab(List<PreferenceTabDescriptor> cprf) {
			PreferenceTabDescriptor ptd;
			PreferenceSectionDescriptor psd;
			PreferenceDescriptor pd;
			ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "icons/erweitert.png", "expandedTab",
					"Erweitert", 0.3);
			cprf.add(ptd);
			psd = new PreferenceSectionDescriptor("generalexpanded", "Allgemeines", 0.1);
			ptd.add(psd);
			pd = new PreferenceDescriptor(psd, "masks", "Masken mehrfach öffnen", 0.1, DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "masks", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "dragdrop", "DragDrop deaktivieren", 0.2, DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "dragdrop", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "icons", "Alle Icons in Symbolleiste einblenden ", 0.3, DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "icons", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "indexautoload", "Index beim Öffnen der Maske automatisch laden", 0.4,
					DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "indexautoload", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "indexautoupdate", "Index automatisch nach dem Speichern aktualisieren", 0.5,
					DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "indexautoupdate", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "reportwindow", "Meldungsfenster an Menüleiste", 0.6, DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "reportwindow", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "descriptionButton", "Beschreibung für Schaltflächen einblenden", 0.7,
					DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "descriptionButton", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "maskbuffer", "Masken Puffer benutzen", 0.8, DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "maskbuffer", logger));
			psd.add(pd);
	
			psd = new PreferenceSectionDescriptor("buffer", "Puffer", 0.2);
			ptd.add(psd);
			pd = new PreferenceDescriptor(psd, "displaybuffer", "Anzeige Puffer [ms]", 0.1, DisplayType.INTEGER);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "displaybuffer", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "maxbuffer", "Max. Puffer [ms]", 0.2, DisplayType.INTEGER);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "maxbuffer", logger));
			psd.add(pd);
			
			psd = new PreferenceSectionDescriptor("table", "Tabelle", 0.3);
			ptd.add(psd);
	//		pd = new PreferenceDescriptor(psd, "selectiondelay", "Auswahltverzögerung [ms]", 0.1, DisplayType.INTEGER);
	//		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "selectiondelay", logger));
	//		psd.add(pd);
			pd = new PreferenceDescriptor(psd, "sizeautoadjust", "Größe automatisch anpassen", 0.2, DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "sizeautoadjust", logger));
			psd.add(pd);
			
			psd = new PreferenceSectionDescriptor("parttable", "Teiltabelle", 0.5);
			ptd.add(psd);
			pd = new PreferenceDescriptor(psd, "fadeinbuttontext", "Schaltflächentext einblenden", 0.1, DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "fadeinbuttontext", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "buttondetailarea", "Schaltfläche im Detailbereich", 0.2, DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "buttondetailarea", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "showlookups", "Zeige Nachschläge", 0.3, DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "showlookups", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "fadeingroups", "Gruppen einblenden", 0.4, DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "fadeingroups", logger));
			psd.add(pd);
			pd = new PreferenceDescriptor(psd, "showchangedrow", "Zeige geänderte Zeilen", 0.5, DisplayType.CHECK);
			pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "showchangedrow", logger));
			psd.add(pd);
		}

	private void buildDruckenTab(List<PreferenceTabDescriptor> cprf) {
		PreferenceTabDescriptor ptd;
		PreferenceSectionDescriptor psd;
		PreferenceDescriptor pd;
		ptd = new PreferenceTabDescriptor("aero.minova.rcp.preferencewindow", "", "printTab", "Drucken", 0.4);
		cprf.add(ptd);
		psd = new PreferenceSectionDescriptor("print", "Drucken", 0.1);
		ptd.add(psd);
		pd = new PreferenceDescriptor(psd, "xmlxsdcreate", "XML + XDS erstellen", 0.1, DisplayType.CHECK);
		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "createxmlxsd", logger));
		psd.add(pd);
//		pd = new PreferenceDescriptor(psd, "font", "Schriftart Inhaltsverzeichnis", 0.2, DisplayType.FONT);
//		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "font", logger));
//		psd.add(pd);
		pd = new PreferenceDescriptor(psd, "optimizewidth", "Breiten optimieren", 0.3, DisplayType.CHECK);
		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "optimizewidth", logger));
		psd.add(pd);
		pd = new PreferenceDescriptor(psd, "hideemptycolumn", "Leere Spalten verbergen", 0.4, DisplayType.CHECK);
		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "hideemptycolumn", logger));
		psd.add(pd);
		pd = new PreferenceDescriptor(psd, "hidegoupcolumns", "Gruppenspalten verbergen", 0.5, DisplayType.CHECK);
		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "hidegoupcolumns", logger));
		psd.add(pd);
		pd = new PreferenceDescriptor(psd, "hidesearchdetails", "Suchkriterien verbergen", 0.6, DisplayType.CHECK);
		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "hidesearchdetails", logger));
		psd.add(pd);
		pd = new PreferenceDescriptor(psd, "deactivateinternpreview", "Gruppenspalten verbergen", 0.7, DisplayType.CHECK);
		pd.setValueAccessor(new InstancePreferenceAccessor(PREFERENCES_NODE, "deactivateinternpreview", logger));
		psd.add(pd);
	}

}
