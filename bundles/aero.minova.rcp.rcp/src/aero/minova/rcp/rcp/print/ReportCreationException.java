package aero.minova.rcp.rcp.print;


/**
 * Fehlermeldung für alle Report-Erstellungsfehler
 *
 * @author dombrovski
 */
public class ReportCreationException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Fehlertypen, die programmatisch ausgewertet werden können
	 *
	 * @author dombrovski
	 */
	public enum Cause {
		DATA_ERROR, // Fehler bei der Bereitstellung der Daten für den Report
		XML_TRANSFORM_ERROR, //
		XML_PARSER_ERROR, //
		IO_ERROR, //
		XSL_TEMPLATES_ERROR, //
		XSL_SAVE_ERROR, //
		PDF_TRANSFORM_ERROR, //
		XML_SAVE_ERROR, //
		UNKNOWN, //
	}

	public final Cause cause;

	public ReportCreationException(Cause cause, String message) {
		super(message);
		this.cause = (cause == null ? Cause.UNKNOWN : cause);
	}

	public ReportCreationException(Cause cause, Exception ex) {
		super(ex);
		this.cause = (cause == null ? Cause.UNKNOWN : cause);
	}

	public ReportCreationException(Cause cause, String message, Exception ex) {
		super(message, ex);
		this.cause = (cause == null ? Cause.UNKNOWN : cause);
	}
}