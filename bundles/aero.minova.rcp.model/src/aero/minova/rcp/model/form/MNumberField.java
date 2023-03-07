package aero.minova.rcp.model.form;

import aero.minova.rcp.model.DataType;

public class MNumberField extends MField {

	public MNumberField(int decimals) {
		super(decimals);
	}

	public MNumberField(int decimals, DataType dataType) {
		super(decimals, dataType);
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

		String tooltip = null;
		boolean maxFits = true;
		if (getMaximumValue() != null) {
			maxFits = numberValue <= getMaximumValue();
			tooltip = maxFits ? tooltip : "@msg.NumberTooHigh %" + numberValue + ">" + getMaximumValue();
		}
		boolean minFits = true;
		if (getMinimumValue() != null) {
			minFits = numberValue >= getMinimumValue();
			tooltip = minFits ? tooltip : "@msg.NumberTooLow %" + numberValue + "<" + getMinimumValue();
		}

		boolean validTest = super.isValid() && minFits && maxFits;
		if (!validTest) {
			setInvalidColor();
		}
		setTooltip(tooltip);
		return validTest;
	}
}
