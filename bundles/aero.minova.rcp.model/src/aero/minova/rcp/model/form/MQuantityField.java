package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.List;

public class MQuantityField extends MField {

	private String unitFieldName;
	private String originalUnitText;
	private int unitFieldSqlIndex;
	private List<String> validUnits = new ArrayList<String>();

	public MQuantityField(int decimals, String unitFieldName, int unitFieldSqlIndex, String unitText, String additionalUnit) {
		super(decimals);
		this.unitFieldName = unitFieldName;
		this.originalUnitText = unitText;
		this.unitFieldSqlIndex = unitFieldSqlIndex;
		declareValidUnits(additionalUnit);
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

	private void declareValidUnits(String addtionalUnits) {
		validUnits.add("100L");
		validUnits.add("BBLS");
		validUnits.add("cbm");
		validUnits.add("cbme");
		validUnits.add("GAL");
		validUnits.add("KG");
		validUnits.add("L15");
		validUnits.add("LEFF");
		validUnits.add("Stck");
		validUnits.add("to");

		String[] units = addtionalUnits.split(",");
		for (String unit : units) {
			validUnits.add(unit);
		}
	}

	public String getUnitFromEntry(String entry) {
		String validUnit = null;
		for (String unit : validUnits) {
			if (unit.toLowerCase().equals(entry.toLowerCase())) {
				validUnit = unit;
			}
		}

		return validUnit;
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
