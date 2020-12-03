package aero.minova.rcp.model.form;

import aero.minova.rcp.model.DataType;

public class MLookupField extends MField {

	public MLookupField() {
		super(DataType.INTEGER);
	}

	private String keyText;
	private String description;

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

}