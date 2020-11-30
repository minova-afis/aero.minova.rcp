package aero.minova.rcp.model.form;

import java.util.HashMap;

/**
 * Das Modell für den Detailbereich
 * 
 * @author saak
 */
public class MDetail {

	private HashMap<String, MField> fields = new HashMap<>();

	/**
	 * Ein neues Feld dem Detail hinzufügen. Dabei muss selbst auf die Eindeutigkeit geachtet werden. Z.B.
	 * <ul>
	 * <li>"KeyLong" = Das Feld KeyLong der Detail-Maske</li>
	 * <li>"CustomerUserCode.UserCode" = Das Feld UserCode in der Maske CustomerUserCode.op.xml</li>
	 * </ul>
	 * 
	 * @param name
	 *            Name / ID des Feldes
	 * @param field
	 *            das eigentliche Feld
	 */
	public void putField(MField field) {
		if (field == null) return;
		fields.put(field.getName(), field);
	}

	/**
	 * Liefert das Feld mit dem Namen. Felder im Detail haben kein Präfix. Felder in einer OptionPage haben das Präfix aus der XBS. z.B.
	 * <ul>
	 * <li>"KeyLong" = Das Feld KeyLong der Detail-Maske</li>
	 * <li>"CustomerUserCode.UserCode" = Das Feld UserCode in der Maske CustomerUserCode.op.xml</li>
	 * </ul>
	 * 
	 * @param name
	 *            Name des Feldes
	 * @return Das Feld
	 */
	public MField getField(String name) {
		return null;
	}
}
