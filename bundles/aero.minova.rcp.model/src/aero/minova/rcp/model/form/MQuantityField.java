package aero.minova.rcp.model.form;

public class MQuantityField extends MField {

	private String unitFieldName;
	private String originalUnitText;
	private int unitFieldSqlIndex;

	public MQuantityField(int decimals, String unitFieldName, int unitFieldSqlIndex, String unitText) {
		super(decimals);
		this.unitFieldName = unitFieldName;
		this.originalUnitText = unitText;
		this.unitFieldSqlIndex = unitFieldSqlIndex;
	}

	public String getUnitFieldName() {
		return unitFieldName;
	}

	public int getUnitFieldSqlIndex() {
		return unitFieldSqlIndex;
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
