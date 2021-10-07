package aero.minova.rcp.constants;

public enum GridChangeType {
	/**
	 * Ein einzelner Wert wurde geändert
	 */
	UPDATE, //
	/**
	 * Eine neue Zeile wurde eingefügt
	 */
	INSERT, //
	/**
	 * Eine Zeile wurde gelöscht
	 */
	DELETE, //
	/**
	 * Die Tabelle wurde komplett neu gesetzt
	 */
	RESET
}