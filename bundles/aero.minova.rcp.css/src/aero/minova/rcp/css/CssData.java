package aero.minova.rcp.css;

/**
 * enthält Informationen, die für die Berechnung der CSS-Styles wichtig sind
 * 
 * @author wild
 * @since 11.0.0
 */
public class CssData {
	/*** Key der Widgets als Data gesetzt werden kann, um CSS-Informationen Minova-intern weiterzugeben ***/
	public static final String CSSDATA_KEY = "aero.minova.css.data";

	/**
	 * Feldtype
	 */
	public final CssType cssType;
	/**
	 * Textfelder können sich über mehrere Zeilen erstrecken
	 */
	public final int numberRowsSpanned;
	/**
	 * hat das Feld eine Standardbreite von 2 oder geht es über alle Spalten (4)
	 */
	public final int numberColumnsSpanned;
	/**
	 * Spalte (0 - 3)
	 */
	public final int column;
	/**
	 * Zeile beginnend bei 0
	 */
	public final int row;
	/**
	 * Soll das Feld die Breite nach rechts ausfüllen
	 */
	public final boolean fill;

	public CssData(CssType cssType, int column, int row, int numberColumnsSpanned, int numberRowsSpanned, boolean fill) {
		this.cssType = cssType;
		this.column = column;
		this.row = row;
		this.numberColumnsSpanned = numberColumnsSpanned;
		this.numberRowsSpanned = numberRowsSpanned;
		this.fill = fill;
	}

	@Override
	public String toString() {
		return "CssData(column: " + column + ", row: " + row + ", number columns spanned: " + numberColumnsSpanned + ", number rows spanned: "
				+ numberRowsSpanned + ")";
	}
}
