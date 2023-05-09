package aero.minova.rcp.model.form;

import java.util.Comparator;
import java.util.function.Predicate;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

public class MLookupField extends MField {

	public MLookupField() {
		super(DataType.INTEGER);
	}

	private String keyText;
	private String description;
	private Table options;
	private String writtenText;

	public String getKeyText() {
		return keyText;
	}

	public void setKeyText(String keyText) {
		this.keyText = keyText;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Table getOptions() {
		return options;
	}

	public void setOptions(Table options) {
		this.options = options;
	}

	@Override
	protected void checkDataType(Value value) {
		if (value == null || (value.getIntegerValue() == null && value.getStringValue() == null)) {
			super.checkDataType(value);
		}
	}

	/**
	 * Diese Methode liefert den Text zurück, welcher in dem textAssist (Text) Feld angezeigt wird
	 *
	 * @return
	 */
	public String getWrittenText() {
		return writtenText;
	}

	public void setWrittenText(String writtenText) {
		this.writtenText = writtenText;
	}

	/**
	 * Setzt einen Filter für den Content-Provider, mit dem Werte vom Anzeigen ausgeschlossen werden können
	 * 
	 * @param filter
	 */
	public void setFilterForContentProvider(Predicate<LookupValue> filter) {
		this.getValueAccessor().setFilterForLookupContentProvider(filter);
	}

	/**
	 * Setzt einen Comparator, mit dem die Lookup-Values, die angezeigt werden, sortiert werden. Ist kein Comparator gegeben oder setzt man ihn auf null, so
	 * wird wieder der "Standard" verwendet (alpabethisch ignorierend Groß-/Kleinschreibung, genauer Match auf KeyText als Erstes). Für Nicht-Lookup Felder hat
	 * die Methode keine Auswirkung
	 * 
	 * @param comparator
	 */
	public void setComparatorForLookupContentProvider(Comparator<LookupValue> comparator) {
		this.getValueAccessor().setComparatorForLookupContentProvider(comparator);
	}
}