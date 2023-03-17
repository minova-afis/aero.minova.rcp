package aero.minova.rcp.model.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import aero.minova.rcp.form.model.xsd.Unit;
import aero.minova.rcp.model.Value;

public class MQuantityField extends MField {

	private String unitFieldName;
	private String originalUnitText;
	private int unitFieldSqlIndex;
	private HashMap<String, String> validUnits = new HashMap<>();

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

	@Override
	protected void checkDataType(Value value) {
		if (value == null || (value.getQuantityValue() == null)) {
			super.checkDataType(value);
		}
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
		validUnits.put("KGV", "KGV");

		for (Unit unit : additionalUnit) {
			validUnits.put(unit.getUnitKeyText(), unit.getUnitText());
		}
	}

	public String getUnitFromEntry(String entry) {
		String validUnit = null;
		for (Entry<String, String> e : validUnits.entrySet()) {
			if (e.getKey().equalsIgnoreCase(entry)) {
				validUnit = e.getValue();
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
