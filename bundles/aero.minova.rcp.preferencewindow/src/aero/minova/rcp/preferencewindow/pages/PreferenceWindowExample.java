package aero.minova.rcp.preferencewindow.pages;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.nebula.widgets.opal.preferencewindow.PWTab;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferencewindow.builder.PreferenceWindowBuilder;

public class PreferenceWindowExample {

	// Konstante für den Pfad der .prefs erstellen
	public static final String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";
	Preferences preferences = InstanceScope.INSTANCE.getNode(PREFERENCES_NODE);

	// Widget Builder Impelentierung
	private PreferenceWindowBuilder pwb = PreferenceWindowBuilder.newPWB();

	// Erstellt Variablen für alle zuänderndern Werte
	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "file")
	String fileValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "lizenzwartung")
	Integer lzwValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "language")
	String languageValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "font")
	String fontValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "symbolMenu")
	String symbolMenuValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "symbolToolbar")
	String symbolToolbarValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "masks")
	Boolean masksValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "dragdrop")
	Boolean dragdropValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "icons")
	Boolean iconsValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "indexautoload")
	Boolean indexautoloadValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "indexautoupdate")
	Boolean indexautoupdateValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "reportwindow")
	Boolean reportwindowValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "descriptionButton")
	Boolean descriptionButtonValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "maskbuffer")
	Boolean maskbufferValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "displaybuffer")
	Integer displaybufferValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "maxbuffer")
	Integer maxbufferValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "selectiondelay")
	Integer selectiondelayValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "sizeautoadjust")
	Boolean sizeautoadjustValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "delay")
	Integer delayValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "fadeinbuttontext")
	Boolean fadeinbuttontextValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "buttondetailarea")
	Boolean buttondetailareaValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "showlookups")
	Boolean showlookupsValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "fadeingroups")
	Boolean fadeingroupsValue;

	@Inject
	@Preference(nodePath = PREFERENCES_NODE, value = "showchangedrow")
	Boolean showchangedrowValue;

	@Execute
	public void execute() {

		openPreferenceWindow();

	}

	/**
	 * Erstellt und öffnet das Preference Window
	 */
	public void openPreferenceWindow() {

		final PreferenceWindow window = PreferenceWindow.create(fillData());

		createAnwendungsTab(window);
		createDarstellungsTab(window);
		createErweitertTab(window);

		window.setSelectedTab(0);
		window.open();
	}

	/**
	 * Persistiert alle Änderung an den Preferences. Wird einem Button zugewiesen.
	 * 
	 * @param window
	 */
	public void updatePreferences(final PreferenceWindow window) {

		preferences.put("file", (String) window.getValueFor("file"));
		preferences.putInt("lizenzwartung", (Integer) window.getValueFor("lizenzwarnung"));

		preferences.put("language", (String) window.getValueFor("language"));
		preferences.put("font", (String) window.getValueFor("font"));
		preferences.put("symbolMenu", (String) window.getValueFor("symbolMenu"));
		preferences.put("symbolToolbar", (String) window.getValueFor("symbolToolbar"));

		preferences.putBoolean("masks", (Boolean) window.getValueFor("masks"));
		preferences.putBoolean("dragdrop", (Boolean) window.getValueFor("dragdrop"));
		preferences.putBoolean("icons", (Boolean) window.getValueFor("icons"));
		preferences.putBoolean("indexautoload", (Boolean) window.getValueFor("indexautoload"));
		preferences.putBoolean("indexautoupdate", (Boolean) window.getValueFor("indexautoupdate"));
		preferences.putBoolean("reportwindow", (Boolean) window.getValueFor("reportwindow"));
		preferences.putBoolean("descriptionButton", (Boolean) window.getValueFor("descriptionButton"));
		preferences.putBoolean("maskbuffer", (Boolean) window.getValueFor("maskbuffer"));
		preferences.putInt("displaybuffer", (Integer) window.getValueFor("displaybuffer"));
		preferences.putInt("maxbuffer", (Integer) window.getValueFor("maxbuffer"));
		preferences.putInt("selectiondelay", (Integer) window.getValueFor("selectiondelay"));
		preferences.putBoolean("sizeautoadjust", (Boolean) window.getValueFor("sizeautoadjust"));
		preferences.putInt("delay", (Integer) window.getValueFor("delay"));
		preferences.putBoolean("fadeinbuttontext", (Boolean) window.getValueFor("fadeinbuttontext"));
		preferences.putBoolean("buttondetailarea", (Boolean) window.getValueFor("buttondetailarea"));
		preferences.putBoolean("showlookups", (Boolean) window.getValueFor("showlookups"));
		preferences.putBoolean("fadeingroups", (Boolean) window.getValueFor("fadeingroups"));
		preferences.putBoolean("showchangedrow", (Boolean) window.getValueFor("showchangedrow"));

		try {
			// forces the application to save the preferences
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * erstellt die Data für das Preference Window und enthält alle zu ändernden
	 * Werte
	 * 
	 * @return
	 */
	public Map<String, Object> fillData() {

		final Map<String, Object> data = new HashMap<String, Object>();

		data.put("lizenzwarnung", Integer.valueOf(lzwValue));
		data.put("file", fileValue);

		data.put("language", languageValue);
		data.put("font", fontValue);
		data.put("symbolMenu", symbolMenuValue);
		data.put("symbolToolbar", symbolToolbarValue);

		data.put("masks", Boolean.valueOf(masksValue));
		data.put("dragdrop", Boolean.valueOf(dragdropValue));
		data.put("icons", Boolean.valueOf(iconsValue));
		data.put("indexautoload", Boolean.valueOf(indexautoloadValue));
		data.put("indexautoupdate", Boolean.valueOf(indexautoupdateValue));
		data.put("reportwindow", Boolean.valueOf(reportwindowValue));
		data.put("descriptionButton", Boolean.valueOf(descriptionButtonValue));
		data.put("maskbuffer", Boolean.valueOf(maskbufferValue));
		data.put("displaybuffer", Integer.valueOf(displaybufferValue));
		data.put("maxbuffer", Integer.valueOf(maxbufferValue));
		data.put("selectiondelay", Integer.valueOf(selectiondelayValue));
		data.put("sizeautoadjust", Boolean.valueOf(sizeautoadjustValue));
		data.put("delay", Integer.valueOf(delayValue));
		data.put("fadeinbuttontext", Boolean.valueOf(fadeinbuttontextValue));
		data.put("buttondetailarea", Boolean.valueOf(buttondetailareaValue));
		data.put("showlookups", Boolean.valueOf(showlookupsValue));
		data.put("fadeingroups", Boolean.valueOf(fadeingroupsValue));
		data.put("showchangedrow", Boolean.valueOf(showchangedrowValue));

		return data;

	}

	/**
	 * Erstellt einen Preference Window Tab
	 * 
	 * @param window
	 */
	protected void createAnwendungsTab(final PreferenceWindow window) {
		Image image = new Image(Display.getCurrent(),
				PreferenceWindowExample.class.getClassLoader().getResourceAsStream("icons/application.png"));
		final PWTab anwendungsTab = window.addTab(image, "Anwendung");

		pwb.addTitledSeparator(anwendungsTab, "Ausführungsort");
		pwb.addFileChooser(anwendungsTab, "Programmverzeichnis", "file");

		pwb.addTitledSeparator(anwendungsTab, "Allgemeines");
		pwb.addIntegerBox(anwendungsTab, "Lizenz Warnung [Wochen]", "lizenzwarnung");

		pwb.addButton(anwendungsTab, "Übernehmen", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				updatePreferences(window);
			}
		});
	}

	/**
	 * Erstellt einen Preference Window Tab
	 * 
	 * @param window
	 */
	protected void createDarstellungsTab(final PreferenceWindow window) {
		Image image = new Image(Display.getCurrent(),
				PreferenceWindowExample.class.getClassLoader().getResourceAsStream("icons/design.png"));
		final PWTab darstellungsTab = window.addTab(image, "Darstellung");

		pwb.addTitledSeparator(darstellungsTab, "Allgemeines");
		pwb.addComboBoxRO(darstellungsTab, "Landessprache", "language", "Deutsch", "Englisch");

		pwb.addTitledSeparator(darstellungsTab, "Design-Einstellungen");
		pwb.addComboBoxRO(darstellungsTab, "Schriftgröße", "font", "S", "M", "L", "XL");
		pwb.addComboBoxRO(darstellungsTab, "Symbole(Menü, Details", "symbolMenu", "16x16", "24x24", "32x32", "48x48",
				"64x64");
		pwb.addComboBoxRO(darstellungsTab, "Symbole(Toolbar)", "symbolToolbar", "16x16", "24x24", "32x32", "48x48",
				"64x64");

		pwb.addButton(darstellungsTab, "Übernehmen", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				updatePreferences(window);
			}
		});

	}

	/**
	 * Erstellt einen Preference Window Tab
	 * 
	 * @param window
	 */
	protected void createErweitertTab(final PreferenceWindow window) {
		Image image = new Image(Display.getCurrent(),
				PreferenceWindowExample.class.getClassLoader().getResourceAsStream("icons/erweitert.png"));
		final PWTab erweitertTab = window.addTab(image, "Erweitert");

		pwb.addTitledSeparator(erweitertTab, "Allgemeines");
		pwb.addCheckbox(erweitertTab, "Masken mehrfach öffnen", "masks");
		pwb.addCheckbox(erweitertTab, "DragDrop deaktivieren", "dragdrop");
		pwb.addCheckbox(erweitertTab, "Alle Icons in Symbolleiste einblenden", "icons");
		pwb.addCheckbox(erweitertTab, "Index beim Öffnen der Maske automatisch laden", "indexautoload");
		pwb.addCheckbox(erweitertTab, "Index automatisch nach dem Speichern aktualisieren", "indexautoupdate");
		pwb.addCheckbox(erweitertTab, "Meldungsfenster an Menüleiste", "reportwindow");
		pwb.addCheckbox(erweitertTab, "Beschreibung für Schaltflächen einblenden", "descriptionButton");
		pwb.addCheckbox(erweitertTab, "Masken Puffer benutzen", "maskbuffer");

		pwb.addTitledSeparator(erweitertTab, "Puffer");
		pwb.addTwoIntegerRow(erweitertTab, "Anzeige Puffer [ms]", "displaybuffer", "Max. Puffer [ms]", "maxbuffer");
//		pwb.addIntegerBox(erweitertTab, "Anzeige Puffer [ms]", "displaybuffer");
//		pwb.addIntegerBox(erweitertTab, "Max. Puffer [ms]", "maxbuffer");

		pwb.addTitledSeparator(erweitertTab, "Tabelle");
		pwb.addIntegerBox(erweitertTab, "Auswahlverzögerung [ms]", "selectiondelay");
		pwb.addCheckbox(erweitertTab, "Größe automatisch anpassen", "sizeautoadjust");

		pwb.addTitledSeparator(erweitertTab, "Nachschlagen");
		pwb.addIntegerBox(erweitertTab, "Verzögerung [ms]", "delay");

		pwb.addTitledSeparator(erweitertTab, "Teiltabelle");
		pwb.addCheckbox(erweitertTab, "Schaltflächentext einblenden", "fadeinbuttontext");
		pwb.addCheckbox(erweitertTab, "Schaltflächen im Detailbereich", "buttondetailarea");
		pwb.addCheckbox(erweitertTab, "Zeige Nachschläge", "showlookups");
		pwb.addCheckbox(erweitertTab, "Gruppen einblenden", "fadeingroups");
		pwb.addCheckbox(erweitertTab, "Zeige geänderte Zeilen", "showchangedrow");

		pwb.addButton(erweitertTab, "Übernehmen", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				updatePreferences(window);
			}
		});

	}
}
