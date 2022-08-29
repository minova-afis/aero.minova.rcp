package aero.minova.rcp.css;

/**
 * @author Wilfried Saak
 */
public enum CssType {
	/**
	 * Beschreibungslabel eines Feldes
	 */
	LABEL,
	/**
	 * Label für die Einheit. Dieses Label kann nur nach Zahlen erscheinen.
	 */
	UNIT_LABEL,
	/**
	 * Label für die Beschreibung von Lookup Werte
	 */
	DESCRIPTION_LABEL,
	/**
	 * Reines Datum
	 */
	DATE_FIELD,
	/**
	 * Reines Zeitpunkt (Datum und Uhrzeit)
	 */
	DATE_TIME_FIELD,
	/**
	 * Zahlenfelder
	 */
	NUMBER_FIELD,
	/**
	 * das eigentliche Datenfeld. Controls dieses Typs haben in den Daten ein {@link aero.minova.rcp.model.form.MField}
	 */
	TEXT_FIELD,
	/**
	 * Zeitfelder
	 */
	TIME_FIELD,
	/**
	 * Label Text Felder
	 */
	LABEL_TEXT_FIELD,
	/**
	 * Label Text Felder FETT
	 */
	LABEL_TEXT_BOLD_FIELD,
	/**
	 * Radio-Felder
	 */
	RADIO_FIELD;
}
