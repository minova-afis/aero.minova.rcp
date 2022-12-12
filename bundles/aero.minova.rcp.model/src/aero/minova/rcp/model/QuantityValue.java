package aero.minova.rcp.model;

import java.text.MessageFormat;

public class QuantityValue extends Value {
	private static final long serialVersionUID = 202212081413L;
	public final Double number;
	public String unit;

	public QuantityValue(Object number, String unit) {
		super(Double.valueOf(number.toString()));
		this.number = Double.valueOf(number.toString());
		this.unit = unit == null ? "" : unit;
	}

	public Double getNumber() {
		return number;
	}

	public String getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		return MessageFormat.format("QuantityValue [type=DOUBLE, value={0},unit={1}]", number, unit);
	}
}
