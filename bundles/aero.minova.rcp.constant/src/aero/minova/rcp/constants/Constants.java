package aero.minova.rcp.constants;

public class Constants {

	private Constants() {}

	public static final String ERROR = "Error";

	// Parts
	public static final String SEARCH_PART = "aero.minova.rcp.rcp.part.search";
	public static final String INDEX_PART = "aero.minova.rcp.rcp.part.index";
	public static final String DETAIL_PART = "aero.minova.rcp.rcp.part.details";
	public static final String PREVIEW_PART = "aero.minova.rcp.rcp.part.formpreview";
	public static final String DETAIL_TOOLBAR = "aero.minova.rcp.rcp.part.details.toolbar";
	public static final String STATISTICDETAIL_TOOLBAR = "aero.minova.rcp.rcp.part.statisticdetails.toolbar";

	public static final String SEARCH_TABLE = "NatTableDataSearchArea";

	public static final String SAVE_DETAIL_BUTTON = "aero.minova.rcp.rcp.handledtoolitem.save";
	public static final String REVERT_DETAIL_BUTTON = "aero.minova.rcp.rcp.handledtoolitem.revert";
	public static final String DELETE_DETAIL_BUTTON = "aero.minova.rcp.rcp.handledtoolitem.delete";

	public static final String MDI_FILE_NAME = "application.mdi";
	public static final String XBS_FILE_NAME = "application.xbs";

	public static final String TABLE_KEYTEXT = "KeyText";
	public static final String TABLE_KEYLONG = "KeyLong";
	public static final String TABLE_DESCRIPTION = "Description";
	public static final String TABLE_LASTACTION = "LastAction";
	public static final String TABLE_COUNT = "Count";
	public static final String TABLE_FILTERLASTACTION = "FilterLastAction";

	public static final String TRANSACTION_PARENT = "transactionParent";

	public static final String CONTROL_FIELD = "field";
	public static final String CONTROL_DATATYPE = "dataType";
	public static final String CONTROL_VALUE = "value";
	public static final String CONTROL_ID = "id";
	public static final String CONTROL_GRID_BUTTON_ID = "grid_button_id";
	public static final String CONTROL_GRID_BUTTON_INSERT = "grid_button_insert";
	public static final String CONTROL_GRID_BUTTON_DELETE = "grid_button_delete";
	public static final String CONTROL_GRID_BUTTON_OPTIMIZEHEIGHT = "grid_button_optimizeHeight";
	public static final String CONTROL_GRID_BUTTON_OPTIMIZEWIDTH = "grid_button_optimizeWidth";
	public static final String CONTROL_GRID_BUTTON_HORIZONTALFILL = "grid_button_horizontalFill";

	public static final String CONTROL_GRID_ID = "grid_id";
	public static final String DETAIL_COMPOSITE = "DetailComposite";
	public static final String DETAIL_LAYOUT = "DetailLayout";

	public static final String CLAZZ = "clazz";
	public static final String PARAMETER = "parameter";
	public static final String WIZARD = "wizard";
	public static final String PROCEDURE = "procedure";
	public static final String GROUP_NAME = "GroupName";
	public static final String GROUP_MENU = "GroupMenu";

	// Trenner für Serializer/Deserialiser
	public static final String SOH = "\u0001";

	// dient dazu, um auf die aus den preferences übernommene Zeitzone im textfieldVerifier zuzugreifen
	public static final String FOCUSED_ORIGIN = "XMLDetailPart";

	public static final String CLEAR_REQUEST = "Clear";
	public static final String DELETE_REQUEST = "Delete";
	public static final String INSERT_REQUEST = "Insert";
	public static final String UPDATE_REQUEST = "Update";
	public static final String READ_REQUEST = "Read";
	public static final String BLOCK_REQUEST = "Block";
	public static final String CORRECT_REQUEST = "Correct";
	public static final String CANCEL_REQUEST = "Cancel";
	public static final String ANNOUNCE_REQUEST = "Announce";

	// Liste an Broker-Konstanten
	public static final String BROKER_SAVEENTRY = "aero/minova/rcp/SaveEntry";
	public static final String BROKER_SAVECOMPLETE = "aero/minova/rcp/SaveComplete";
	public static final String BROKER_DELETEENTRY = "aero/minova/rcp/DeleteEntry";
	public static final String BROKER_NEWENTRY = "aero/minova/rcp/NewFields";
	public static final String BROKER_CLEARFIELDS = "aero/minova/rcp/ClearFields";
	public static final String BROKER_RELOADFIELDS = "aero/minova/rcp/ReloadFields";
	public static final String BROKER_CLEARSELECTION = "aero/minova/rcp/ClearSelection";
	public static final String BROKER_REVERTENTRY = "aero/minova/rcp/RevertEntry";
	public static final String BROKER_WFCLOADALLLOOKUPVALUES = "aero/minova/rcp/WFCLoadAllLookUpValues";
	public static final String BROKER_LOADINDEXTABLE = "aero/minova/rcp/LoadIndexTable";
	public static final String BROKER_RELOADINDEX = "aero/minova/rcp/ReloadIndex";
	public static final String BROKER_COLLAPSEINDEX = "aero/minova/rcp/CollapseIndex";
	public static final String BROKER_EXPANDINDEX = "aero/minova/rcp/ExpandIndex";
	public static final String BROKER_CLEARSEARCHTABLE = "aero/minova/rcp/ClearSearchTable";
	public static final String BROKER_REVERTSEARCHTABLE = "aero/minova/rcp/RevertSearchTable";
	public static final String BROKER_DELETEROWSEARCHTABLE = "aero/minova/rcp/DelteRowSearchTable";
	public static final String BROKER_SAVESEARCHCRITERIA = "aero/minova/rcp/SaveSearchCriteria";
	public static final String BROKER_LOADSEARCHCRITERIA = "aero/minova/rcp/LoadSearchCriteria";
	public static final String BROKER_RESIZETABLE = "aero/minova/rcp/ResizeTable";
	public static final String BROKER_ACTIVEROWS = "aero/minova/rcp/ActiveRows";
	public static final String RECEIVED_TICKET = "aero/minova/rcp/WFCReceivedTicket";
	public static final String BROKER_CHECKDIRTY = "aero/minova/rcp/checkDirty";

	/**
	 * Benutzt mit einem ErrorObject, öffnet einen Fehler-Dialog
	 */
	public static final String BROKER_SHOWERROR = "aero/minova/rcp/ShowError";
	/**
	 * Benutzt mit einem String, öffnet eine Benachrichtigungs-Popup rechts unten
	 */
	public static final String BROKER_SHOWNOTIFICATION = "aero/minova/rcp/ShowNotification";
	/**
	 * Benutzt mit einem String, öffnet einen einfachen Fehler-Dialog
	 */
	public static final String BROKER_SHOWERRORMESSAGE = "aero/minova/rcp/ShowErrorMessage";

	public static final String BROKER_SELECTSTATISTIC = "aero/minova/rcp/SelectStatistic";
	public static final String BROKER_SENDEVENTTOHELPER = "aero/minova/rcp/SendEventToHelper";
	public static final String BROKER_UPDATECOLUMNS = "aero/minova/rcp/UpdateColumns";

	// Operatoren
	public static final String NULL = "null";
	public static final String NOT_NULL = "!null";
	protected static final String[] OPERATORS = { "<>", "<=", ">=", "<", ">", "=", "!~", "~", NULL, NOT_NULL };

	public static String[] getOperators() {
		return OPERATORS;
	}

	protected static final String[] STRING_OPERATORS = { "<>", "=", "!~", "~", NULL, NOT_NULL };

	public static String[] getStringOperators() {
		return STRING_OPERATORS;
	}

	protected static final String[] NUMBER_OPERATORS = { "<>", "<=", ">=", "<", ">", "=", NULL, NOT_NULL };

	public static String[] getNumberOperators() {
		return NUMBER_OPERATORS;
	}

	protected static final String[] WILDCARD_OPERATORS = { "_", "%" };

	public static String[] getWildcardOperators() {
		return WILDCARD_OPERATORS;
	}

	// SeachCriteria
	public static final String SEARCHCRITERIA_DEFAULT = "DEFAULT";
	public static final String LAST_LOADED_SEARCHCRITERIA = "aero.minova.rcp.preferences.lastsearchcriteria";
	public static final String LAST_SEARCHCRITERIA = "LastLoadedSearchCriteria";

	public static final String LAST_STATE = "LAST_STATE";

	public static final String SHOW_WORKSPACE_RESET_MESSAGE = "ShowWorkspaceResetMessage";

	// NatTable Label
	public static final String COMPARATOR_LABEL = "CUSTOM_COMPARATOR_LABEL"; // Für eigene Sortierung
	public static final String REQUIRED_CELL_LABEL = "REQUIRED_CELL";
	public static final String READ_ONLY_CELL_LABEL = "READ_ONLY_CELL";
	public static final String INVALID_CELL_LABEL = "INVALID_CELL";
	public static final String VALIDATION_CELL_LABEL = "VALIDATION_CELL";
	public static final String SELECTED_ANCHOR_LABEL = "selectionAnchor";

	// NatTable Data
	public static final String GRID_DATA_SECTION = "Section";
	public static final String GRID_DATA_SELECTIONLAYER = "Selectionlayer";
	public static final String GRID_DATA_DATATABLE = "DataTable";

	// CSS Klassennamen
	public static final String CSS_STANDARD = "Standard";
	public static final String CSS_REQUIRED = "ValueRequired";
	public static final String CSS_INVALID = "InvalidValue";
	public static final String CSS_READONLY = "ReadOnly";

	public static final String FORM_NAME = "aero.minova.rcp.perspectiveswitcher.parameters.formName"; //$NON-NLS-1$
	public static final String FORM_ID = "aero.minova.rcp.perspectiveswitcher.parameters.formId"; //$NON-NLS-1$
	public static final String FORM_LABEL = "aero.minova.rcp.perspectiveswitcher.parameters.perspectiveLabel"; //$NON-NLS-1$
	public static final String FORM_ICON = "aero.minova.rcp.perspectiveswitcher.parameters.perspectiveIcon";

	public static final String PERSPECTIVE_TOOLBAR = "perspectivetoolbar";
	public static final String PREFERENCES_KEPTPERSPECTIVES = "aero.minova.rcp.preferences.keptperspectives";
	public static final String PREFERENCES_TOOLBARORDER = "aero.minova.rcp.preferences.toolbarorder";
	public static final String PREFERENCES_DETAILSECTIONS = "aero.minova.rcp.preferences.detailsections";

	public static final String KEPT_PERSPECTIVE_FORMNAME = ".FormName";
	public static final String KEPT_PERSPECTIVE_FORMID = ".FormId";
	public static final String KEPT_PERSPECTIVE_FORMLABEL = ".FormLabel";
	public static final String KEPT_PERSPECTIVE_ICONURI = ".IconURI";
	public static final String KEPT_PERSPECTIVE_LOCALIZEDLABEL = ".LocalizedLabel";
	public static final String KEPT_PERSPECTIVE_LOCALIZEDTOOLTIP = ".LocalizedToolTip";

	public static final String DIRTY_PERSPECTIVES = "DirtyPerspectives";

	public static final String SECTION_WIDTH = "SectionWidth";
	public static final String SECTION_NAME = "SectionName";
	public static final String DETAIL_WIDTH = "Detail_Width";
	public static final String OPTION_PAGES = "OptionPages";
	public static final String OPTION_PAGE = "OptionPage";
	public static final String OPTION_PAGE_GRID = "OptionPageGrid";
	public static final String GRID = "Grid";
	/**
	 * In der xbs zeigt dieses Zeichen an, dass ein statischer Wert eingetragen werden soll (siehe #1357)
	 */
	public static final String OPTION_PAGE_QUOTE_ENTRY_SYMBOL = "#";

	// Commands
	public static final String AERO_MINOVA_RCP_RCP_COMMAND_GRIDBUTTONCOMMAND = "aero.minova.rcp.rcp.command.gridbuttoncommand";
	public static final String AERO_MINOVA_RCP_RCP_COMMAND_LOADINDEX = "aero.minova.rcp.rcp.command.loadindex";
	public static final String AERO_MINOVA_RCP_RCP_COMMAND_PRINTDETAIL = "aero.minova.rcp.rcp.command.printdetail";
	public static final String AERO_MINOVA_RCP_RCP_COMMAND_SAVEDETAIL = "aero.minova.rcp.rcp.command.savedetail";
	public static final String AERO_MINOVA_RCP_RCP_COMMAND_SELECTSEARCHPART = "aero.minova.rcp.rcp.command.selectsearchpart";
	public static final String AERO_MINOVA_RCP_RCP_COMMAND_DYNAMIC_BUTTON = "aero.minova.rcp.rcp.command.dynamicbuttoncommand";
	public static final String COMMAND_PARAMETER_GOTOFIELD = "aero.minova.rcp.rcp.commandparameter.gotofield";

	// XBS Einstellungen
	public static final String XBS_SHOW_DELETE_DIALOG = "ShowDeleteDialog";

	// settings.properties Einstellungen
	public static final String SETTINGS_FILE_NAME = "settings.properties";
	public static final String SETTINGS_PROPERTIES = "SettingsProperties";
	public static final String SETTINGS_DEFAULT_CONNECTION_STRING = "defaultConnectionString";

	// Aus FieldUtil
	public static final String TRANSLATE_LOCALE = "aero.minova.rcp.translate.locale";
	public static final String TRANSLATE_PROPERTY = "aero.minova.rcp.translate.property";
	public static final String FIELD_MIN_VALUE = "aero.minova.rcp.field.minimum";
	public static final String FIELD_MAX_VALUE = "aero.minova.rcp.field.maximum";
	public static final String FIELD_DECIMALS = "aero.minova.rcp.field.decimals";

}
