package aero.minova.rcp.model.form;

import aero.minova.rcp.model.DataType;

public class MQuantityField extends MField {

	private String unitField;

	public MQuantityField(int decimals, String unitField) {
		super(decimals);
		this.unitField = unitField;
	}

	public MQuantityField(int decimals, DataType dataType, String unitField) {
		super(decimals, dataType);
		this.unitField = unitField;
	}

	public String getUnitField() {
		return unitField;
	}

	@Override
	public boolean isValid() {
		if (getValue() == null) {
			return super.isValid();
		}

		double numberValue = 0;
		if (getValue().getDoubleValue() != null) {
			numberValue = getValue().getDoubleValue();
		} else if (getValue().getIntegerValue() != null) {
			numberValue = getValue().getIntegerValue();
		}

		boolean maxFits = true;
		if (getMaximumValue() != null) {
			maxFits = numberValue <= getMaximumValue();
		}
		boolean minFits = true;
		if (getMinimumValue() != null) {
			minFits = numberValue >= getMinimumValue();
		}

		boolean validTest = super.isValid() && minFits && maxFits;
		if (!validTest) {
			setInvalidColor();
		}
		return validTest;
	}

}
