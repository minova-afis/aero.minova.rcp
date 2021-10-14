package aero.minova.rcp.model.form;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;

public interface IValueAccessor {

	boolean isFocussed();

	/**
	 * @param value
	 *            der anzuzeigende Wert
	 * @param user
	 *            true, wenn die Veränderung durch eine Benutzereingabe erfolgt
	 * @return der Wert, der gerade im Control angezeigt wird
	 */
	Value setValue(Value value, boolean user);

	void setValue(Row row);

	void setMessageText(String message);

	void setEditable(boolean editable);

	void setCSSClass(String classname);

	void updateSaveButton();
}
