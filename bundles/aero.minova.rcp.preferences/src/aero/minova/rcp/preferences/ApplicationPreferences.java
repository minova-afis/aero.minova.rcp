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
	 * Erlaubt Masken merfach zu öffnen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String ALLOW_MULTIPLE_FORMS = "AllowMultipleForms";

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
	 * Passt die Größe der Tabellen automatisch an.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String AUTO_RESIZE = "AutoResize";

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
	 * DragDrop deaktivieren.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String DISALLOW_DRAG_AND_DROP = "DisallowDragAndDrop";

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
	public static final String FONT_SIZE = "FontSize";

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
	 * Bestimmt die Größe der Symbole für Menü, Detail, usw.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String ICON_SIZE = "IconSize";

	/**
	 * Bestimmt die Größe der Symbole in den ToolBars.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String ICON_SIZE_BIG = "IconSizeBig";

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
	 * Blendet die Icons aller Masken in die Symbolleiste ein.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_ALL_ACTION_IN_TOOLBAR = "ShowAllActioninToolbar";

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
	 * Aktiviert Lookups in Teiltabellen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_LOOKUPS = "ShowLookups";

	/**
	 * Bestimmt die Dauer, die bei einer Auswahl gewartet wird, bevor das Event gesendet wird. Zum Beispiel, wenn man mit den Pfeiltasten durch die Tabelle geht,
	 * wird nicht bei jeder Auswahl das Event gesendet, sondern nur, wenn in dem angegebenen Zeitraum die Auswahl nicht geändert wurde.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String TABLE_SELECTION_BUFFER_MS = "TableSelectionBufferMs";

	/**
	 * Masken Puffer benutzen. ENTFERNEN?!
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String USE_FORM_BUFFER = "UseFormBuffer";

	/**
	 * Bestimmt welcher Benutzer in den Detail Bereich eingetragen wird. ENTFERNEN? Über den Login regeln?
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String USER_PRESELECT_DESCRIPTOR = "UserPreselectDescription";

	/**
	 * Bestimmt das Land, das für das Locale genutzt wird.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String COUNTRY = "country";

	private ApplicationPreferences() {}
}
