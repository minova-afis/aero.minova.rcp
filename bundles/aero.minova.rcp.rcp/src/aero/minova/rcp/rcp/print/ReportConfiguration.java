package aero.minova.rcp.rcp.print;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * Beschreibt einige Einstellungen aller Reports. Alle Werte sind nie NULL!
 *
 * @author dombrovski
 */
public class ReportConfiguration extends CommonPrint {
	/**
	 * Report-Name. Wird für Dateinamen und Root-Tagnamen verwendet
	 */
	public final String reportName;
	/**
	 * Report-Title
	 */
	public final String title;
	/**
	 * Standard-Font<br>
	 * mit diesem Font soll gedruckt werden<br>
	 * wird verwendet, um Breiten automatisch zu berechnen
	 */
	public final Font standardFont;

	/**
	 * GUI-Font<br>
	 * Falls verfügbar, haben wir hier den von der GUI verwendeten Font (sonst Standardfont)<br>
	 * wird verwendet, um Breiten automatisch zu berechnen
	 */
	public final Font guiFont;

	public final String xslFontFamily;

	/**
	 * Diese Map kann für weitere Properties verwendet werden
	 */
	public final Map<String, String> props = new HashMap<>();

	/**
	 * Seitengröße (i.a. A4); wird auch verwendet um Breiten automatisch zu berechnen<br>
	 * wird i.a. nicht geändert<br>
	 * bei vorgefertigten Templates brauchen wir das gar nicht
	 */
	public PageSize pagesize = PageSize.A4;
	/**
	 * Seitenorientierung; wird auch verwendet um Breiten automatisch zu berechnen<br>
	 * kann seinerseits wiederum berechnet werden mit calculatePageOrientation, wenn man die benötigte Breite kennt<br>
	 * bei vorgefertigten Templates brauchen wir das gar nicht
	 */
	public Orientation orientation = Orientation.DEFAULT;
	/**
	 * rechter Rand; wird auch verwendet um Breiten automatisch zu berechnen<br>
	 * wird i.a. nicht geändert<br>
	 * bei vorgefertigten Templates brauchen wir das gar nicht
	 */
	public int rightBorder = 10;
	/**
	 * linker Rand; wird auch verwendet um Breiten automatisch zu berechnen<br>
	 * wird i.a. nicht geändert<br>
	 * bei vorgefertigten Templates brauchen wir das gar nicht
	 */
	public int leftBorder = 10;

	/**
	 * Standard für Text-Druck: 72 dpi<br>
	 * vorm Ändern WIS oder DOS fragen
	 */
	public final int DPI = 72;

	/**
	 * Größe eines Pixels in mm
	 */
	public final double PIXEL_SIZE_MM = INCH_MM / this.DPI;

	/**
	 * Abstand der Zellen für gedruckte Tabellen<br>
	 * darf geändert werden, aber nicht im laufenden Programm
	 */
	public final int CELL_PADDING_MM = 3;

	/**
	 * Suchkriterien verstecken?<br>
	 * Muss explizit auf false gesetzt werden, um die Suchkriterien im Index-Report anzuzeigen.<br>
	 * Für andere Reports hat der Wert keine Bedeutung.
	 */
	public boolean hideSearchCriterias = true;

	public static final ReportConfiguration DEFAULT = new ReportConfiguration();

	/**
	 * Erstellt eine Standardkonfiguration mit reportName='Report', title='', standardFont='Arial, Normal, 10'
	 */
	public ReportConfiguration() {
		this(null, null, null);
	}

	public ReportConfiguration(String reportName, String title, Font font) {
		this(reportName, title, font, font, null);
	}

	public ReportConfiguration(String reportName, String title, Font font, String xslFontFamily) {
		this(reportName, title, font, font, xslFontFamily);
	}

	public ReportConfiguration(String reportName, String title, Font font, Font guiFont) {
		this(reportName, title, font, guiFont, null);
	}

	public ReportConfiguration(String reportName, String title, Font font, Font guiFont, String xslFontFamily) {
		this.reportName = (reportName == null ? "Report" : reportName);
		this.title = (title == null ? "Title" : title);
		this.standardFont = (font == null ? new Font("Arial", Font.PLAIN, 10) : font);
		this.guiFont = (guiFont == null) ? this.standardFont : guiFont;
		this.xslFontFamily = (xslFontFamily == null ? "Humanst521 BT, Arial, sans-serif" : xslFontFamily);
	}

	public String getProp(String key, String def) {
		final String toRet = this.props.get(key);
		return (toRet == null ? def : toRet);
	}

	/**
	 * berechnet die verfügbare Breite der Seite in mm (abz. Rand)
	 *
	 * @return
	 */
	public int getAvailPageWidth() {
		if (this.pagesize == PageSize.A4 && this.orientation != Orientation.DEFAULT) {
			if (this.orientation == Orientation.LANDSCAPE) {
				return getAvailLandscapeWidth();
			} else if (this.orientation == Orientation.PORTRAIT) {
				return getAvailPortraitWidth();
			} else {
				// can't happen
				return 0;
			}
		} else {
			// kann Breite nicht bestimmen
			return 0;
		}
	}

	/**
	 * berechnet die Gesamtbreite der Seite in mm (incl. Rand)
	 *
	 * @return
	 */
	public int getPageWidth() {
		if (this.pagesize == PageSize.A4 && this.orientation != Orientation.DEFAULT) {
			if (this.orientation == Orientation.LANDSCAPE) {
				return PAGE_MM_A4_LANDSCAPE;
			} else if (this.orientation == Orientation.PORTRAIT) {
				return PAGE_MM_A4_PORTRAIT;
			} else {
				// can't happen
				return 0;
			}
		} else {
			// kann Breite nicht bestimmen
			return 0;
		}
	}

	/**
	 * berechnet die Gesamthöhe der Seite in mm (incl. Rand)
	 *
	 * @return
	 */
	public int getPageHeight() {
		if (this.pagesize == PageSize.A4 && this.orientation != Orientation.DEFAULT) {
			if (this.orientation == Orientation.LANDSCAPE) {
				// bei landscape (Breite) entspricht die Höhe der Breite von portrait und umgekehrt ;-)
				return PAGE_MM_A4_PORTRAIT;
			} else if (this.orientation == Orientation.PORTRAIT) {
				return PAGE_MM_A4_LANDSCAPE;
			} else {
				// can't happen
				return 0;
			}
		} else {
			// kann Höhe nicht bestimmen
			return 0;
		}
	}

	/**
	 * falls wir A4 haben, können wir die verfügbare Seitenbreite berechnen
	 *
	 * @return A4-Seitenbreite Hochformat abz. Rand
	 */
	public int getAvailPortraitWidth() {
		if (this.pagesize == PageSize.A4) {
			return PAGE_MM_A4_PORTRAIT - this.rightBorder - this.leftBorder;
		} else {
			// wir wissen die Breite nicht
			return 0;
		}
	}

	/**
	 * falls wir A4 haben, können wir die verfügbare Seitenbreite berechnen
	 *
	 * @return A4-Seitenbreite Querformat abz. Rand
	 */
	public int getAvailLandscapeWidth() {
		if (this.pagesize == PageSize.A4) {
			return PAGE_MM_A4_LANDSCAPE - this.rightBorder - this.leftBorder;
		} else {
			// wir wissen die Breite nicht
			return 0;
		}
	}

	/**
	 * berechnet aus der angegebenen Breite, ob die Seite im Hoch- oder Querformat gedruckt werden muss
	 */
	public Orientation calculatePageOrientation(int width) {
		final int availWidth = getAvailPortraitWidth();
		if (availWidth == 0) {
			// wir konnten die Breite nicht bestimmen
			this.orientation = Orientation.DEFAULT;
		}
		if (width <= availWidth) {
			// Hochformat
			this.orientation = Orientation.PORTRAIT;
		} else {
			// Querformat
			this.orientation = Orientation.LANDSCAPE;
		}

		return this.orientation;
	}
}