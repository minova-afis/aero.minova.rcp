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
	 * Index beim Öffnen automatisch laden.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String AUTO_LOAD_INDEX = "AutoLoadIndex";

	/**
	 * Index automatisch nach dem Speichern aktualisieren.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String AUTO_RELOAD_INDEX = "AutoReloadIndex";

	/**
	 * Meldungsfenster an Menüleiste.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String AUTO_RESIZE = "AutoResize";

	/**
	 * Erstellt XMLXS.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String CREATE_XML_XS = "CreateXMLXS";

	/**
	 * Interne Vorschau deaktivieren.
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
	 * Anzeige Puffer[ms].
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
	 * Bestimmt die Schriftgröße an.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String FONT_SIZE = "FontSize";

	/**
	 * Leere Spalten verbergen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String HIDE_EMPTY_COLS = "HideEmptyCols";

	/**
	 * Gruppenspalten verbergen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String HIDE_GROUP_COLS = "HideGroupCols";

	/**
	 * Suchkriterien verbergen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String HIDE_SEARCH_CRITERIAS = "HideSearchCriterias";

	/**
	 * Bestimmt die Größe der Symbole (Menü, Detail).
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String ICON_SIZE = "IconSize";

	/**
	 * Bestimmt die Größe der Symbole (Toolbar).
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String ICON_SIZE_BIG = "IconSizeBig";

	/**
	 * Bestimmt die Schriftart des Inhaltsverzeichnisses im Druck.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String INDEX_FONT = "IndexFont";

	/**
	 * Bestimmt die Wochen vor einer Linzenz Warnung.
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
	 * Bestimmt den Max. Puffer [ms].
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String MAX_BUFFER_MS = "MaxBufferMs";

	/**
	 * Bestimmt die maximal Anzahl an Zeichen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String MAX_CHARS = "MaxChars";

	/**
	 * Breiten Optimieren.
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
	 * Meldungsfenster an Menüleiste.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHEET_STYLES_MESSAGE_BOXES = "SheetStylesMessageBoxes";

	/**
	 * Alle Icons in Symbolleiste einblenden.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_ALL_ACTION_IN_TOOLBAR = "ShowAllActioninToolbar";

	/**
	 * Schaltflächentext einblenden.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_BUTTON_TEXT = "ShowButtonText";

	/**
	 * Schaltflächentext im Detailbereich. *
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_BUTTON_IN_SECTION = "ShowButtonsInSection";

	/**
	 * Zeige geänderte Zeilen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_CHANGED_ROWS = "ShowChangedRows";
	
	/**
	 * Beschreibung für Schaltfläche einblenden.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_DETAIL_BUTTON_TEXT = "ShowDetailButtonText";
	
	/**
	 * Gruppen einblenden.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_GROUPS = "ShowGroups";
	
	/**
	 * Zeige Nachschläge.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String SHOW_LOOKUPS = "ShowLookups";
	
	/**
	 * Bestimmt die Auswahlverzögerung [ms] in den Tabellen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String TABLE_SELECTION_BUFFER_MS = "TableSelectionBufferMs";
	
	/**
	 * Masken Puffer benutzen.
	 * 
	 * @see ApplicationPreferences#PREFERENCES_NODE
	 */
	public static final String USE_FORM_BUFFER = "UseFormBuffer";
	
	/**
	 * Vorbelegung des Benutzernamens.
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
