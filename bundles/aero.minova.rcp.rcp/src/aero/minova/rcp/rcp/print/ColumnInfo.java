package aero.minova.rcp.rcp.print;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByDataLayer;

import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.Row;

/**
 * Wird verwendet, um eine Felddefinition mit der aktuellen Spaltenbreite zu versehen<br>
 * Das wird fürs Drucken verwendet, aber u.a. auch um die Ansicht-Einstellungen zu speichern.
 *
 * @author dombrovski
 */
public class ColumnInfo {

	/**
	 * Erstellt automatisch Column-Info mit Hilfe eines Font-Objektes.
	 *
	 * @param table
	 *            Tabelle mit Daten
	 * @param font
	 * @return
	 */
	public static List<ColumnInfo> createColumnInfo(GroupByDataLayer<Row> table, List<Column> cols, Font font) {
		if (table == null) {
			return null;
		}
		final List<ColumnInfo> toRet = new ArrayList<>();
		if (font == null) {
			for (int i = 0; i < table.getColumnCount(); i++) {
				ColumnInfo c = new ColumnInfo(cols.get(i), 80);
				toRet.add(c);
			}
		}
		return toRet;
	}

	/** Aktuelle Breite der Column in Pixel */
	public int width;

	/** Aktueller Index der Column in der Nattable **/
	public int index;

	/**
	 * Die Breite der Column kann für den Druck optimiert werden.<br>
	 * Falls bereits geschehen, steht die optimierte Breite in diesem Feld.
	 */
	protected int optimizedPrintWidth;

	public boolean visible = true;

	/** Felddefinition, die sich auf die Spalte bezieht */
	public final Column column;

	/** Formattiert einen Double **/
	public NumberFormat numberFormat;

	private ReportConfiguration reportConf;

	public ColumnInfo(Column field, int width) {
		this(field, width, true);
	}

	public ColumnInfo(Column field, int width, boolean visible) {
		this.width = width;
		this.column = field;
		this.visible = visible;
	}

	public ColumnInfo(Column field, int width, boolean visible, int index) {
		this(field, width, visible);
		this.index = index;
	}

	/**
	 * @return DPI-Zahl, die im ReportCreator festgelegt wurde, Standard 72
	 */
	protected int getDPI() {
		if (this.reportConf != null) {
			return this.reportConf.DPI;
		} else {
			return 72;
		}
	}

	/**
	 * ColumnInfo über die ReportConfiguration informieren<br>
	 * wird vorm Drucken gesetzt, um Größenberechnungen zu ermöglichen
	 *
	 * @param reportConf
	 *            the reportConf to set
	 */
	void setReportConf(final ReportConfiguration reportConf) {
		this.reportConf = reportConf;
	}

	@Override
	public String toString() {
		return (this.column == null ? "<Empty>" : this.column.getName());
	}

	/**
	 * Berechnet die Breite des Strings
	 *
	 * @param str
	 * @param f
	 * @param frc
	 * @return
	 */
	protected int widthOf(String str, Font f, FontRenderContext frc) {
		final Rectangle2D bounds = f.getStringBounds(str, frc);
		final double dWidth = bounds.getWidth();

		// WIS: Umrechnung DPI: die Font-Funktionen rechnen mit 72 DPI, das muss man also ggf. umrechnen
		final double dpiFactor = (double) getDPI() / 72;

		// Umrechnung Schriftgröße brauchen wir nicht, denn hier ist schon die richtige Schrift übergeben

		final int iWidth = (int) (dWidth * dpiFactor);
		return iWidth;
	}
}