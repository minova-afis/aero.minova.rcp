package aero.minova.rcp.css;

/**
 * @author Wilfried Saak
 */
public interface ICssStyler {
	int CSS_DATE_WIDTH = 90;
	int CSS_DATE_TIME_WIDTH = 130;
	int CSS_NUMBER_WIDTH = 90;
	int CSS_ROW_HEIGHT = 24;
	int CSS_SECTION_SPACING = 5;
	int CSS_TEXT_WIDTH = 130;
	int CSS_TIME_WIDTH = 65;

	/**
	 * wendet die aktuellen CSS-Parameter für das Layout des gebundenen Objectes an
	 */
	public void style();

	/**
	 * Liefert die Breite von Datumsfeldern.
	 * 
	 * @return Breite von Datumsfeldern in px
	 */
	public int getDateWidth();

	/**
	 * Setzt die Breite von Datumsfeldern.
	 * 
	 * @param width
	 *            Breite von Datumsfeldern in px
	 */
	public void setDateWidth(int width);

	/**
	 * Liefert die Breite von Zeitpunktfeldern (Datum und Uhrzeit).
	 * 
	 * @return Breite von Zeitpunktfeldern in px
	 */
	public int getDateTimeWidth();

	/**
	 * Setzt die Breite von Zeitpunktfeldern (Datum und Uhrzeit).
	 * 
	 * @param width
	 *            Breite von Zeitpunktfeldern (Datum und Uhrzeit) in px
	 */
	public void setDateTimeWidth(int width);

	/**
	 * Liefert die Breite von Zahlenfeldern. Für das mögliche Einheiten-Label steht dann {@link #textWidth} - {@link #getNumberWidth()} px zur Verfügung.
	 * 
	 * @return Breite von Zahlenfeldern in px
	 */
	public int getNumberWidth();

	/**
	 * Setzt die Breite von Zahlenfeldern. Für das mögliche Einheiten-Label steht dann {@link #getTextWidth()} - {@link #getNumberWidth()} px zur Verfügung.
	 * 
	 * @param width
	 *            Breite von Zahlenfeldern in px
	 */
	public void setNumberWidth(int width);

	/**
	 * @return Die Höhe einer Zeile in px.
	 */
	int getRowHeight();

	/**
	 * Die Höhe einer Zeile. Hierin ist der Abstand zur nächsten Zeile berücksichtigt.
	 * 
	 * @param height
	 */
	public void setRowHeight(int height);

	/**
	 * Liefert die Breite zwischen 2 Widgets. Ausnahme bilden das DescriptionLabel und das UnitLabel. Dieser Abstand wird auch als HorizintalMargin verwendet.
	 * 
	 * @return Abstand zwische 2 Widgets (Spalten)
	 */
	public int getSectionSpacing();

	/**
	 * definiert den horizontalen Abstand zwischen 2 Controls auf einer Section mit max. 4 Spalten
	 * 
	 * @param margin
	 *            Abstand in px
	 */
	public void setSectionSpacing(int margin);

	/**
	 * Liefert die Breite der Section. Dieser Wert gilt nur für normale Sections mit 4 Spalten.
	 * 
	 * @return
	 */
	public int getSectionWidth();

	/**
	 * @return Breite von Standard-Spalten und damit auch von einfachen Text Widgets für Texteingaben
	 */
	public int getTextWidth();

	/**
	 * Setzt die Breite von Text-Elementen. Diese Breite wird auch zur Berechnung der Section-Breite verwendet {@link}
	 * 
	 * @param width
	 *            die neue Breite. Sie sollte größer oder gleich mit den Breiten der anderen Felder sein.
	 */
	public void setTextWidth(int width);

	/**
	 * Liefert die Breite von Zeitfeldern.
	 * 
	 * @return Breite von Zeitfeldern in px
	 */
	public int getTimeWidth();

	/**
	 * Setzt die Breite von Zeitfeldern.
	 * 
	 * @param width
	 *            Breite von Zeitfeldern in px
	 */
	public void setTimeWidth(int width);
}
