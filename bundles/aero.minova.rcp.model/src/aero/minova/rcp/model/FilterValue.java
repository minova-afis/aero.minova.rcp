package aero.minova.rcp.model;

import java.util.Objects;

import aero.minova.rcp.constants.Constants;

/**
 * Gültige Werte
 * <ul>
 * <li>">-Wert"</li>
 * <li>"<-Wert"</li>
 * <li>"=-Wert"</li>
 * <li>"<>-Wert"</li>
 * <li>"~-Wert"</li>
 * <li>"!~-Wert"</li>
 * <li>"null"</li>
 * <li>"!null"</li>
 * </ul>
 * <br/>
 * Heute ist der 22.02.2021 <br/>
 * Heute Eingabe im Feld "0" -> "f-=-i-2021-02-22 00:00.00" <br/>
 * Gestern Eingabe im Feld "-" -> "f-=-i-2021-02-21 00:00.00"<br/>
 * Gestern Eingabe im Feld "210221" -> "f-=-i-2021-02-21 00:00.00" <br/>
 * <br/>
 * Bei Eingabe von "null" oder "!null" wird kein weiterer Wert akzeptiert, this.filterValue ist null<br/>
 * "null" -> "f-null"
 *
 * @author saak
 */
public class FilterValue extends Value {

	private final Value valueToFilterBy;
	private final String userInput;

	private static final long serialVersionUID = 202102221518L;

	public FilterValue(String operator, Object value, String userInput) {
		super(operator, DataType.FILTER);
		this.userInput = userInput;
		if (value == null) {
			this.valueToFilterBy = null;
		} else {
			this.valueToFilterBy = new Value(value);
		}
	}

	@Override
	public String toString() {
		return ValueSerializer.serialize(this).toString();
	}

	public Value getFilterValue() {
		return valueToFilterBy;
	}

	public String getUserInput() {
		return userInput;
	}

	public String getUserInputWithoutOperator() {
		StringBuilder regEx = new StringBuilder();
		regEx.append("[");
		for (String operator : Constants.getOperators()) {
			regEx.append("(" + operator + ")");
		}
		regEx.append("]");
		return userInput.replaceAll(regEx.toString(), "").trim();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userInput == null) ? 0 : userInput.hashCode());
		result = prime * result + ((valueToFilterBy == null) ? 0 : valueToFilterBy.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		FilterValue v = null;
		if (obj instanceof FilterValue) {
			v = (FilterValue) obj;
		}
		if (v == null) {
			return false;
		}
		if (this.valueToFilterBy == null && v.valueToFilterBy == null) {
			if (this.userInput.equals(v.userInput)) {
				return super.equals(obj);
			} else {
				return false;
			}
		} else if (this.valueToFilterBy == null && v.valueToFilterBy != null || this.valueToFilterBy != null && v.valueToFilterBy == null) {
			return false;
		}
		return super.equals(obj) && Objects.equals(this.valueToFilterBy, v.valueToFilterBy);
	}
}
