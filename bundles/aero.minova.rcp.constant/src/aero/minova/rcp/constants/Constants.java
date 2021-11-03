package aero.minova.rcp.constants;

public class Constants {

	// Parts
	public static final String SEARCH_PART = "aero.minova.rcp.rcp.part.search";
	public static final String INDEX_PART = "aero.minova.rcp.rcp.part.index";
	public static final String DETAIL_PART = "aero.minova.rcp.rcp.part.details";

	public static final String SAVE_DETAIL_BUTTON = "aero.minova.rcp.rcp.handledtoolitem.save";

	public static final String MDI_FILE_NAME = "application.mdi";
	public static final String XBS_FILE_NAME = "application.xbs";

	public static final String TABLE_KEYTEXT = "KeyText";
	public static final String TABLE_KEYLONG = "KeyLong";
	public static final String TABLE_DESCRIPTION = "Description";
	public static final String TABLE_LASTACTION = "LastAction";
	public static final String TABLE_COUNT = "Count";
	public static final String TABLE_FILTERLASTACTION = "FilterLastAction";

	public static final String CONTROL_FIELD = "field";
	public static final String CONTROL_OPTIONS = "options";
	public static final String CONTROL_CONSUMER = "consumer";
	public static final String CONTROL_LOOKUPCONSUMER = "lookupConsumer";
	public static final String CONTROL_KEYLONG = "keyLong";
	public static final String CONTROL_DATATYPE = "dataType";
	public static final String CONTROL_DECIMALS = "decimals";
	public static final String CONTROL_VALUE = "value";
	public static final String CONTROL_ID = "id";
	public static final String CONTROL_MDETAIL = "mDetail";
	public static final String CONTROL_WIZARD = "wizard";
	public static final String CONTROL_GRID_BUTTON_ID = "grid_button_id";
	public static final String CONTROL_GRID_BUTTON_INSERT = "grid_button_insert";
	public static final String CONTROL_GRID_BUTTON_DELETE = "grid_button_delete";
	public static final String CONTROL_GRID_BUTTON_OPTIMIZEHEIGHT = "grid_button_optimizeHeight";
	public static final String CONTROL_GRID_BUTTON_OPTIMIZEWIDTH = "grid_button_optimizeWidth";
	public static final String CONTROL_GRID_ID = "grid_id";

	// Trenner für Serializer/Deserialiser
	public static final String SOH = "\u0001";

	// dient dazu, um auf die aus den preferences übernommene Zeitzone im textfieldVerifier zuzugreifen
	public static final String FOCUSED_ORIGIN = "XMLDetailPart";

	public static final String CLEAR_REQUEST = "Clear";
	public static final String DELETE_REQUEST = "Delete";
	public static final String INSERT_REQUEST = "Insert";
	public static final String UPDATE_REQUEST = "Update";
	public static final String READ_REQUEST = "Read";

	// Felder aus der Form, welche ohne Halperclass direkt angesprochen werden müssen
	public static final String FORM_BOOKINGDATE = "BookingDate";
	public static final String FORM_STARTDATE = "StartDate";
	public static final String FORM_ENDDATE = "EndDate";
	public static final String FORM_RENDEREDQUANTITY = "RenderedQuantity";
	public static final String FORM_CHARGEDQUANTITY = "ChargedQuantity";
	public static final String EMPLOYEEKEY = "EmployeeKey";

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
	public static final String BROKER_SHOWERROR = "aero/minova/rcp/ShowError";
	public static final String BROKER_SHOWNOTIFICATION = "aero/minova/rcp/ShowNotification";
	public static final String BROKER_SHOWERRORMESSAGE = "aero/minova/rcp/ShowErrorMessage";
	public static final String BROKER_SHOWCONNECTIONERRORMESSAGE = "aero/minova/rcp/ShowConnectionErrorMessage";
	public static final String BROKER_PROCEDUREWITHTABLE = "aero/minova/rcp/ProcedureWithTable";
	public static final String BROKER_PROCEDUREWITHTABLEERROR = "aero/minova/rcp/ProcedureWithTableError";
	public static final String BROKER_PROCEDUREWITHTABLESUCCESS = "aero/minova/rcp/ProcedureWithTableSuccess";
	public static final String BROKER_PROCEDUREWITHTABLESUCCESSFINISHED = "aero/minova/rcp/ProcedureWithTableSuccessFinished";
	public static final String BROKER_PROCEDUREWITHTABLEEMPTYRESPONSE = "aero/minova/rcp/ProcedureWithTableEmptyResponse";

	// Operatoren
	public static final String[] OPERATORS = { "<>", "<=", ">=", "<", ">", "=", "!~", "~", "null", "!null" };
	public static final String[] STRING_OPERATORS = { "<>", "=", "!~", "~", "null", "!null" };
	public static final String[] NUMBER_OPERATORS = { "<>", "<=", ">=", "<", ">", "=", "null", "!null" };
	public static final String[] WILDCARD_OPERATORS = { "_", "%" };

	// SeachCriteria
	public static final String SEARCHCRITERIA_DEFAULT = "DEFAULT";
	public static final String LAST_LOADED_SEARCHCRITERIA = "aero.minova.rcp.preferences.lastsearchcriteria";
	public static final String LAST_SEARCHCRITERIA = "LastLoadedSearchCriteria";

	// Wiederherstellen der UI
	public static final String LAST_STATE = "LAST_STATE";
	public static final String RESTORING_UI_MESSAGE_SHOWN_THIS_SESSION = "RestoringUIMessageShownThisSession";
	public static final String NEVER_SHOW_RESTORING_UI_MESSAGE = "NeverShowRestoringUIMessage";

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

	public static final String DETAIL_WIDTH = "Detail_Width";
	public static final String OPTION_PAGES = "OptionPages";
	public static final Object OPTION_PAGE_GRID = "OptionPageGrid";

	// Commands
	public static final String AERO_MINOVA_RCP_RCP_COMMAND_GRIDBUTTONCOMMAND = "aero.minova.rcp.rcp.command.gridbuttoncommand";
	public static final String AERO_MINOVA_RCP_RCP_COMMAND_LOADINDEX = "aero.minova.rcp.rcp.command.loadindex";
	public static final String AERO_MINOVA_RCP_RCP_COMMAND_PRINTDETAIL = "aero.minova.rcp.rcp.command.printdetail";
	public static final String AERO_MINOVA_RCP_RCP_COMMAND_SAVEDETAIL = "aero.minova.rcp.rcp.command.savedetail";

	// XBS Einstellungen
	public static final String XBS_SHOW_DELETE_DIALOG = "ShowDeleteDialog";

	/**
	 * Hier werden Standard-Einstellungen definiert, die wirklich oft genutzt werden
	 *
	 * @author dombrovski
	 */
	public enum Standard {
		/** alte Standardverbindung */
		CONNECTION("connection"),

		CONNECTION_DRIVER("driver", "net.sourceforge.jtds.jdbc.Driver"),

		/** Standardverbindung für Dienste und Java 2 */
		CONNECTION2("connection2"),

		/** Verbindung zur Truckdb */
		TRUCKCONNECTION("truckconnection"),

		/** Kunden-ID, z.B. 'ZOTZ' */
		CUSTOMER_ID("CustomerID"),

		/** Kodierung der XML-Reports */
		ENCODING_XML_REPORT("xmlReportEncoding", "UTF-8"),

		/**
		 * optionale Einstellung für Report-Sprache, Default: Sprache des Users<br>
		 * z.B. "fr_CH" oder "de"
		 */
		REPORT_LOCALE("ReportLocale"),

		/**
		 * optionale Einstellung für System-Sprache, Default: Sprache des Betriebssystems<br>
		 * z.B. "fr_CH" oder "de"
		 */
		// SYSTEM_LOCALE("SystemLocale"), WIS: wird derzeit über UI-Preferences gemacht

		/** MDI-Dateiname */
		FILE_MDI("MDIFilename"),

		/** Lizenz gültig von */
		LICENCE_VALID_FROM("licensedata/validFrom"),

		/** Lizenz gültig bis */
		LICENCE_VALID_UNTIL("licensedata/validUntil"),

		/** Logo (muss im Report-Pfad liegen) */
		LOGO("Logo", "logo.gif"),

		/** Bild-Pfad */
		PATH_IMAGE("ImagePath"),

		/** Report-Pfad */
		PATH_REPORT("ReportPath"),

		/** Dokumente-Pfad */
		PATH_DOCUMENT("DocumentPath"),

		/** Lizenz-Signatur */
		SIGNATURE("signature"),

		/** Stornieren- oder Löschen-Dialog anzeigen? */
		SHOW_CANCEL_DIALOG("ShowCancelDialog", "1"),

		/** Korrektur-Dialog anzeigen? */
		SHOW_CORRECTION_DIALOG("ShowCorrectionDialog", "1"),

		SITE_ADDRESS_1("SiteAddress1", "MINOVA Information Systems GmbH"),

		SITE_ADDRESS_2("SiteAddress2", "Tröltschstraße 4"),

		SITE_ADDRESS_3("SiteAddress3", "D-97072 Würzburg"),

		SITE_PHONE("SitePhone", "+49 (931) 322 35-0"),

		SITE_FAX("SiteFax", "+49 (931) 322 35-55");

		public final String defaultValue;

		public final String path;

		/**
		 * Liefert alle geladenen Standardwerte für die gegebene Applikation
		 */

		Standard(String path) {
			this(path, null);
		}

		Standard(String path, String defaultValue) {
			this.path = path;
			this.defaultValue = defaultValue;
		}
	}
}
