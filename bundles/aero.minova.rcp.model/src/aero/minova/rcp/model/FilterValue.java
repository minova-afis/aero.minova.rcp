package aero.minova.rcp.model;

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

	// public final String filterOperator;
	private final Value filterValue;
	private final String userInput;

	private static final long serialVersionUID = 202102221518L;

	public FilterValue(String operator, Object value, String userInput) {
		super(operator, DataType.FILTER);
		this.userInput = userInput;
		if (value == null) {
			this.filterValue = null;
		} else {
			this.filterValue = new Value(value);
		}
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
		if (this.filterValue == null && v.filterValue == null) {
			if (this.userInput.equals(v.userInput)) {
				return super.equals(obj);
			} else {
				return false;
			}
		} else if (this.filterValue == null && v.filterValue != null || this.filterValue != null && v.filterValue == null) {
			return false;
		}
		return super.equals(obj) && this.filterValue.equals(v.filterValue);
	}

	@Override
	public String toString() {
		return ValueSerializer.serialize(this).toString();
	}

	public Value getFilterValue() {
		return filterValue;
	}

	public String getUserInput() {
		return userInput;
	}

	public String getUserInputWithoutOperator() {
		String regEx = "[";
		for (String operator : Constants.OPERATORS) {
			regEx += "(" + operator + ")";
		}
		regEx += "]";
		return userInput.replaceAll(regEx, "").trim();
	}
}
