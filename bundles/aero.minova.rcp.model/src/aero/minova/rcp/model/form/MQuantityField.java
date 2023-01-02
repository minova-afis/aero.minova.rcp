package aero.minova.rcp.model.form;

import java.util.HashMap;
import java.util.List;

import aero.minova.rcp.form.model.xsd.Unit;

public class MQuantityField extends MField {

	private String unitFieldName;
	private String originalUnitText;
	private int unitFieldSqlIndex;
	private HashMap<String, String> validUnits = new HashMap<String, String>();

	public MQuantityField(int decimals, String unitFieldName, int unitFieldSqlIndex, String unitText, List<Unit> additionalUnit) {
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

	private void declareValidUnits(List<Unit> additionalUnit) {
		validUnits.put("L100", "100L");
		validUnits.put("B", "BBLS");
		validUnits.put("BLS", "BBLS");
		validUnits.put("BBLS", "BBLS");
		validUnits.put("c", "cbm");
		validUnits.put("cbm", "cbm");
		validUnits.put("ce", "cbme");
		validUnits.put("cbme", "cbme");
		validUnits.put("G", "GAL");
		validUnits.put("GAL", "GAL");
		validUnits.put("K", "KG");
		validUnits.put("KG", "KG");
		validUnits.put("L", "L15");
		validUnits.put("L15", "L15");
		validUnits.put("LE", "LEFF");
		validUnits.put("LEFF", "LEFF");
		validUnits.put("St", "Stck");
		validUnits.put("Stck", "Stck");
		validUnits.put("t", "to");
		validUnits.put("to", "to");

		for (Unit unit : additionalUnit) {
			validUnits.put(unit.getUnitKeyText(), unit.getUnitText());
		}
	}

	public String getUnitFromEntry(String entry) {
		String validUnit = null;
		for (String unitKeyText : validUnits.keySet()) {
			if (unitKeyText.equalsIgnoreCase(entry.toLowerCase())) {
				validUnit = validUnits.get(unitKeyText);
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
