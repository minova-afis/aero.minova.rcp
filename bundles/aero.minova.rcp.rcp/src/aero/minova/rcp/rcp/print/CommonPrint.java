package aero.minova.rcp.rcp.print;

/**
 * allgemeine Funktionen und Konstanten für den Druck (Funktionen ohne UI)
 *
 * @author wild
 */
public abstract class CommonPrint {
	public enum PageSize {
		A3, A4, A5, DEFAULT
	}

	public enum Orientation {
		PORTRAIT, LANDSCAPE, DEFAULT
	}

	public enum ReportType {
		AUTO, // index|detail; Report wird automatisch erzeugt
		VIEW, // detail über view(=index) drucken; x-report-type="view" x-report="evtl.xsl" x-report-root="name des root-Elements"
		PROCEDURE, // detail; x-report-type="procedure" x-report="evtl.xsl" x-report-root="name des root-Elements" x-report-procedure="name der procedure"
		HTML, // TODO: BIRT ... ???
		NONE, // sollte nicht vorkommen
	}

	// allgemeine Druck-Konstanten
	public static final double INCH_MM = 25.4;
	public static final int MIN_COL_SIZE_PX = 15;
	public static final int PAGE_MM_A4_PORTRAIT = 210;
	public static final int PAGE_MM_A4_LANDSCAPE = 297;
}