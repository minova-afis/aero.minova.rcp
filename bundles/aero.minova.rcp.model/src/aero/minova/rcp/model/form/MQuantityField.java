package aero.minova.rcp.model.form;

public class MQuantityField extends MField{

	private String unitFieldName;
	private String originalUnitText;

	public MQuantityField(int decimals, String unitFieldName, String unitText) {
		super(decimals);
		this.unitFieldName = unitFieldName;
		this.originalUnitText = unitText;
	}

	public String getUnitFieldName() {
		return unitFieldName;
	}

	public String getOriginalUnitText() {
		return originalUnitText;
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
