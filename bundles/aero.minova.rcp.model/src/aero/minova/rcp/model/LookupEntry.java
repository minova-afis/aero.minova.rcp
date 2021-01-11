package aero.minova.rcp.model;

/**
 * Diese Klasse bildet die Daten eines Eintrags in einer LookupComboBox ab.
 * 
 * @author Wilfried Saak
 */
public class LookupEntry {

	public final int keyLong;
	public final String keyText;
	public final String description;

	public LookupEntry(int keyLong, String keyText, String description) {
		this.keyLong = keyLong;
		this.keyText = keyText;
		this.description = description;
	}
}
