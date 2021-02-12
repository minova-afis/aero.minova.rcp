package aero.minova.rcp.model.form;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

public class MLookupField extends MField {

	public MLookupField() {
		super(DataType.INTEGER);
	}

	private String keyText;
	private String description;
	private Table options;

	public String getKeyText() {
		return keyText;
	}

	public void setKeyText(String keyText) {
		this.keyText = keyText;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Table getOptions() {
		return options;
	}

	public void setOptions(Table options) {
		this.options = options;
	}

	@Override
	protected void checkDataType(Value value) {
		if (value == null || (value.getIntegerValue() == null && value.getStringValue() == null)) {
			super.checkDataType(value);
		}
	}
}