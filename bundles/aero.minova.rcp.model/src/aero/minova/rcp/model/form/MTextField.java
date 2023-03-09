package aero.minova.rcp.model.form;

import aero.minova.rcp.model.DataType;

public class MTextField extends MField {

	public MTextField() {
		super(DataType.STRING);
	}

	@Override
	public boolean isValid() {
		if (getValue() == null) {
			return super.isValid();
		}

		int textLength = getValue().getStringValue().length();
		boolean validTest = super.isValid() && textLength <= getMaxTextLength();
		if (!validTest) {
			setInvalidColor();
			setTooltip("@msg.TextTooLong %" + textLength + ">" + getMaxTextLength());
		} else {
			setTooltip(null);
		}
		return validTest;
	}
}