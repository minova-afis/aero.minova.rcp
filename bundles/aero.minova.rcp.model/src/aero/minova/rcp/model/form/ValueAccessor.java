package aero.minova.rcp.model.form;

import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;

public interface ValueAccessor {

	public boolean isFocussed();

	/**
	 * @param value
	 *            der anzuzeigende Wert
	 * @param user
	 *            true, wenn die Veränderung durch eine Benutzereingabe erfolgt
	 * @return der Wert, der gerade im Control angezeigt wird
	 */
	public Value setValue(Value value, boolean user);

	public void setValue(Row row);

	public Control getControl();
}
