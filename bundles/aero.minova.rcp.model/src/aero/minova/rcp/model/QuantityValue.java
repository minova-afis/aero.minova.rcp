package aero.minova.rcp.model;

import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Objects;

import aero.minova.rcp.model.util.NumberFormatUtil;

public class QuantityValue extends Value {
	private static final long serialVersionUID = 202212081413L;
	private String unit;

	public QuantityValue(Number number, String unit) {
		super(number);
		this.unit = unit == null ? "" : unit;
	}

	public QuantityValue(String number, String unit, DataType dataType, DecimalFormatSymbols dfs) {
		super(NumberFormatUtil.getNumberObjectFromString(number, dataType, dfs));
		this.unit = unit == null ? "" : unit;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Value v = (Value) obj;

		if (getValue() == null && v.getValue() != null) {
			return false;
		} else if (getValue() == null && v.getValue() == null) {
			return getType() == v.getType();
		}

		return (getType() == v.getType() && Objects.equals(this.getValue(), v.getValue()));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
		result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return MessageFormat.format("QuantityValue [type= {2} , value={0},unit={1},type={2}]", getValue(), unit, getType());
	}

}
