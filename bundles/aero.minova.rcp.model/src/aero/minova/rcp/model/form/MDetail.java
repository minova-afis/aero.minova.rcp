package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Das Modell für den Detailbereich
 * 
 * @author saak
 */
public class MDetail {

	private HashMap<String, MField> fields = new HashMap<>();

	private List<MField> fieldList = new ArrayList<MField>();

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

	public void addField(MField field) {
		if (field == null) return;
		if (!fieldList.contains(field)) {
			fieldList.add(field);
		}
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
		return fields.get(name);
	}

	public Collection<MField> getFields() {
		return fields.values();
	}

	public List<MField> getFieldList() {
		return fieldList;
	}
}
