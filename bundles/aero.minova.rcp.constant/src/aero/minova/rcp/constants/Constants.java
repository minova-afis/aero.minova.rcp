package aero.minova.rcp.constants;

public class Constants {

	public static final String TABLE_KEYTEXT = "KeyText";
	public static final String TABLE_KEYLONG = "KeyLong";
	public static final String TABLE_DESCRIPTION = "Description";
	public static final String TABLE_LASTACTION = "LastAction";
	public static final String TABLE_COUNT = "Count";
	public static final String TABLE_FILTERLASTACTION = "FilterLastAction";

	public static final String TABLE_TICKETNUMBER = "TicketNumber";

	public static final String CONTROL_FIELD = "field";
	public static final String CONTROL_OPTIONS = "options";
	public static final String CONTROL_CONSUMER = "consumer";
	public static final String CONTROL_LOOKUPCONSUMER = "lookupConsumer";
	public static final String CONTROL_KEYLONG = "keyLong";
	public static final String CONTROL_DATATYPE = "dataType";
	public static final String CONTROL_DECIMALS = "decimals";
	public static final String CONTROL_VALUE = "value";

	// dient dazu, um auf die aus den preferences übernommene Zeitzone im
	// textfieldVerifier zuzugreifen
	public static final String FOCUSED_ORIGIN = "XMLDetailPart";

	public static final String CLEAR_REQUEST = "Clear";
	public static final String DELETE_REQUEST = "Delete";
	public static final String INSERT_REQUEST = "Insert";
	public static final String UPDATE_REQUEST = "Update";
	public static final String READ_REQUEST = "Read";

	// Felder aus der Form, welche ohne Halperclass direkt angesprochen werden
	// müssen
	public static final String FORM_BOOKINGDATE = "BookingDate";
	public static final String FORM_STARTDATE = "StartDate";
	public static final String FORM_ENDDATE = "EndDate";
	public static final String FORM_RENDEREDQUANTITY = "RenderedQuantity";
	public static final String FORM_CHARGEDQUANTITY = "ChargedQuantity";
	public static final String EMPLOYEEKEY = "EmployeeKey";

// Liste an Broker-Konstanten
	public static final String BROKER_SAVEENTRY = "aero/minova/rcp/SaveEntry";
	public static final String BROKER_DELETEENTRY = "aero/minova/rcp/DeleteEntry";
	public static final String BROKER_NEWENTRY = "aero/minova/rcp/NewFields";
	public static final String BROKER_CLEARFIELDS = "aero/minova/rcp/ClearFields";
	public static final String BROKER_REVERTENTRY = "aero/minova/rcp/RevertEntry";
	public static final String BROKER_WFCLOADALLLOOKUPVALUES = "aero/minova/rcp/WFCLoadAllLookUpValues";
	public static final String BROKER_LOADINDEXTABLE = "aero/minova/rcp/LoadIndexTable";
	public static final String BROKER_RESIZETABLE = "aero/minova/rcp/ResizeTable";
	public static final String BROKER_ACTIVEROWS = "aero/minova/rcp/ActiveRows";
	public static final String RECEIVED_TICKET = "aero/minova/rcp/WFCReceivedTicket";
	public static final String BROKER_SHOWERROR = "aero/minova/rcp/ShowError";
	public static final String BROKER_RESOLVETICKET = "aero/minova/rcp/ResolveTicket";

	// Operatoren
	public static final String[] OPERATORS = { "<>", "<=", ">=", "<", ">", "=", "!~", "~", "null", "!null" };
	public static final String[] STRING_OPERATORS = { "<>", "=", "!~", "~", "null", "!null" };
	public static final String[] NUMBER_OPERATORS = { "<>", "<=", ">=", "<", ">", "=", "null", "!null" };
	public static final String[] WILDCARD_OPERATORS = { "_", "%" };

}
