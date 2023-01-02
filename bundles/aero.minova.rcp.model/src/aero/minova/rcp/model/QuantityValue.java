package aero.minova.rcp.model;

import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;

public class QuantityValue extends Value {
	private static final long serialVersionUID = 202212081413L;
	private final String number;
	private String unit;
	private DataType dataType;

	public QuantityValue(String number, String unit, DataType dataType, DecimalFormatSymbols dfs) {
		super(getNumberObjectFromString(number, dataType, dfs));
		this.number = number;
		this.unit = unit == null ? "" : unit;
		this.dataType = dataType;
	}

	public String getNumber() {
		return number;
	}

	public String getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		return MessageFormat.format("QuantityValue [type=" + dataType.toString() +  ", value={0},unit={1}]", number, unit);
	}
	
	/**
	 * Diese Methode liefert ein Object zurück, für den übergebenen String.
	 * 
	 * @param text
	 *            Wert aus dem das VAlue gebildet werden soll
	 * @param negative
	 *            true - negative Zahl
	 * @param type
	 *            DataType des Fields
	 * @param dfs
	 *            DecimalFormatSymbols
	 * @return Value für den entsprechenden DataType
	 */
	public static Object getNumberObjectFromString(String text, DataType type, DecimalFormatSymbols dfs) {
		switch (type) {
		case INTEGER:
			return Integer.parseInt(text);
		case DOUBLE:
		case BIGDECIMAL:
			return Double.parseDouble(text.replace(dfs.getDecimalSeparator(), '.'));
		default:
			return null;
		}
	}
}
