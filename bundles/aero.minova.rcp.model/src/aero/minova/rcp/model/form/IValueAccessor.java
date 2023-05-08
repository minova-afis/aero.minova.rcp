package aero.minova.rcp.model.form;

import java.util.Comparator;
import java.util.function.Predicate;

import aero.minova.rcp.model.LookupValue;
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

	/**
	 * Setzt einen Filter für den Content Provider des Lookups. Für Nicht-Lookup Felder hat die Methode keine Auswirkung
	 * 
	 * @param filter
	 */
	void setFilterForLookupContentProvider(Predicate<LookupValue> filter);

	/**
	 * Setzt einen Comparator, mit dem die Lookup-Values, die angezeigt werden, sortiert werden. Ist kein Comparator gegeben oder setzt man ihn auf null, so
	 * wird wieder der "Standard" verwendet (alpabethisch ignorierend Groß-/Kleinschreibung, genauer Match auf KeyText als Erstes). Für Nicht-Lookup Felder hat
	 * die Methode keine Auswirkung
	 * 
	 * @param comparator
	 */
	void setComparatorForLookupContentProvider(Comparator<LookupValue> comparator);

	void setTooltip(String tooltip);

}
