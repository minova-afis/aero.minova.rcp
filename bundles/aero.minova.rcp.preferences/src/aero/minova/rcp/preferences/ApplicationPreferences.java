package aero.minova.rcp.preferences;

public class ApplicationPreferences {

	/**
	 * Knoten an dem die Preferences abgelegt werden.
	 * 
	 * <pre>
	 * &#64;Inject
	 * &#64;Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	 * String timezone;
	 * </pre>
	 */
	public static final String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";

	/**
	 * Bestimmt die Zeitzone des Anwenders. Der Server arbeitet immer in der Zeitzone UTC.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String TIMEZONE = "Timezone";

	/**
	 * Die Daten im Index werden beim Öffnen der Maske automatisch geladen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String AUTO_LOAD_INDEX = "AutoLoadIndex";

	/**
	 * Der Index wird nach dem Speichern automatisch aktualisiert.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String AUTO_RELOAD_INDEX = "AutoReloadIndex";

	/**
	 * Erstellt beim Drucken neben einem PDF auch eine eine XML und XSL Datei im gleichen Ordner.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String CREATE_XML_XS = "CreateXMLXS";

	/**
	 * Deaktiviert die interne Druckvorschau.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String DISABLE_PREVIEW = "DisablePreview";

	/**
	 * Bestimmt die Zeit, die die Anwendung wartet bevor sie den Detail Bereich aktualisiert .
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String DISPLAY_BUFFER_MS = "DisplayBufferMs";

	/**
	 * <b>Fall True (Default):</b> <br>
	 * Der eingetragene oder ausgewählte Wert wird festgesetzt und der Benutzer springt ins erste Pflichtfeld der Maske. <br>
	 * <br>
	 * <b>Fall False:</b> <br>
	 * Der eingetragene oder ausgewählte Wert wird festgesetzt und der Benutzer springt ins nächste Pflichtfeld.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String ENTER_SELECTS_FIRST_REQUIRED = "EnterSelectsFirstRequired";

	/**
	 * Bestimmt die Schriftgröße der Anwendung.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String FONT_ICON_SIZE = "FontSize";

	/**
	 * Verbirgt beim Drucken die leeren Spalten.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String HIDE_EMPTY_COLS = "HideEmptyCols";

	/**
	 * Verbirgt beim Drucken die Spalten, die die Gruppen bilden.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String HIDE_GROUP_COLS = "HideGroupCols";

	/**
	 * Verbirgt beim Drucken die Suchkriterien.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String HIDE_SEARCH_CRITERIAS = "HideSearchCriterias";

	/**
	 * Bestimmt die Schriftart des Inhaltsverzeichnisses beim Drucken.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String INDEX_FONT = "IndexFont";

	/**
	 * Bestimmt den Zeitraum vor einer Linzenz Warnung in Wochen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String LICENCE_WARNING_BEFORE_WEEKS = "LicenceWarningBeforeWeeks";

	/**
	 * Bestimmt die Sprache der Anwendung.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String LOCALE_LANGUAGE = "LocalLanguage";

	/**
	 * <b>Fall True (Default):</b> <br>
	 * Der in der LookupComboBox ausgewählte Wert wird festgesetzt und der Benutzer springt ins nächste Pflichtfeld. <br>
	 * <br>
	 * <b>Fall False:</b> <br>
	 * Der in der LookupComboBox ausgewählte Wert wird festgesetzt und der Benutzer bleibt im Feld.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String LOOKUP_ENTER_SELECTS_NEXT_REQUIRED = "LookupEnterSelectsNextRequired";

	/**
	 * Bestimmt den Puffer in dem die Anwendung Zwischenänderungen sichtbar macht, während der Anzeige-Puffer noch nicht abgelaufen ist.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String MAX_BUFFER_MS = "MaxBufferMs";

	/**
	 * Bestimmt die maximal Anzahl an Zeichen in der Konsole. Die ältesten Einträge werden abgeschnitten.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String MAX_CHARS = "MaxChars";

	/**
	 * Optimiert die Breiten der Spalten beim Drucken.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String OPTIMIZED_WIDTHS = "OptimizeWidths";

	/**
	 * <b>Fall True (Default):</b> <br>
	 * Der Benutzer kann mit TAB in die Controls(Speichern, Neu, ...) navigieren. * <br>
	 * <br>
	 * <b>Fall False:</b> <br>
	 * Der Benutzer kann mit TAB nicht in die Controls(Speichern, Neu, ...) navigieren.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SELECT_ALL_CONTROLS = "SelectAllControls";

	/**
	 * Das Meldungsfenster für Fehler wird an die Menüleiste angebunden.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHEET_STYLES_MESSAGE_BOXES = "SheetStylesMessageBoxes";

	/**
	 * Blendet den Text neben den Schaltflächen in den Toolbars der Sections ein.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_BUTTON_TEXT = "ShowButtonText";

	/**
	 * Blendet die Schaltflächen in den Sections ein.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_BUTTON_IN_SECTION = "ShowButtonsInSection";

	/**
	 * Makiert geänderte, gelöschte und neue Zeilen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_CHANGED_ROWS = "ShowChangedRows";

	/**
	 * Blendet den Text von Schaltflächen in den Toolbars ein.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_DETAIL_BUTTON_TEXT = "ShowDetailButtonText";

	/**
	 * Erlaubt Gruppen in Teiltabellen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_GROUPS = "ShowGroups";

	/**
	 * Bestimmt die Dauer, die bei einer Auswahl gewartet wird, bevor das Event gesendet wird. Zum Beispiel, wenn man mit den Pfeiltasten durch die Tabelle
	 * geht, wird nicht bei jeder Auswahl das Event gesendet, sondern nur, wenn in dem angegebenen Zeitraum die Auswahl nicht geändert wurde.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String TABLE_SELECTION_BUFFER_MS = "TableSelectionBufferMs";

	/**
	 * Das Trennzeichen, dass beim Export des Index als CSV verwendet wird. Default: ,
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String INDEX_CSV_SEPARATOR = "IndexCSVSeparator";

	/**
	 * Das Zeichen, mit dem Werte beim Export des Index als CSV umgeben werden. Default: "
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String INDEX_CSV_BRACKETS = "IndexCSVBrackets";

	/**
	 * Masken Puffer benutzen. ENTFERNEN?!
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String USE_FORM_BUFFER = "UseFormBuffer";

	/**
	 * Bestimmt das Land, das für das Locale genutzt wird.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String COUNTRY = "country";

	/**
	 * Bestimmt den FormatStyle von eingebenen Date. Mögliche FormatStyles sind: FULL, LONG, MEDIUM und SHORT.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String DATE_UTIL = "DateUtil";

	/**
	 * Bestimmt den FormatStyle von eingegebenen Time. Mögliche FormatStyles sind: FULL, LONG, MEDIUM und SHORT.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String TIME_UTIL = "TimeUtil";

	/**
	 * Legt die Zeit fest, nach der bei einer Anfrage ans CAS ein Timeout auftritt
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String TIMEOUT_CAS = "TimeoutCAS";

	/**
	 * Legt die Zeit fest, nach der sich bei einer Anfrage ans CAS eine Meldung öffnet, dass Daten geladen werden
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String TIMEOUT_OPEN_NOTIFICATION = "TimeoutOpenNotification";

	/**
	 * Soll eine Nachricht angezeigt werden, wenn ungespeicherte Änderungen durch das Wählen eines neuen Datensatzes im Index verworfen werden
	 */
	public static final String SHOW_DISCARD_CHANGES_DIALOG_INDEX = "ShowDiscardChangesDialogIndex";

	/**
	 * Sollen versteckte Abschnitte angezeigt werden. Ersatz für app.isSUMode() aus Masken der Version 11
	 */
	public static final String SHOW_HIDDEN_SECTIONS = "ShowHiddenSections";

	/**
	 * Soll eine Nachricht angezeigt werden bevor ein Datensatz gelöscht wird
	 */
	public static final String SHOW_DELETE_WARNING = "ShowDeleteWarning";

	public static final String GRID_TAB_NAVIGATION = "GridTabNavigation";

	/**
	 * Wenn im Index mehr als diese Anzahl Datensätze geladen werden wird Dialog zum Limit setzten angezeigt
	 */
	public static final String INDEX_LIMIT = "IndexLimit";

	/*
	 * Wenn diese Option gesetzt ist werden alle Dateien immer neu vom CAS geladen, der Hash wird ignoriert
	 */
	public static final String DISABLE_FILE_CACHE = "DisableFileCache";

	private ApplicationPreferences() {}
}
