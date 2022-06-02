package aero.minova.rcp.model.form;

import java.util.List;

import aero.minova.rcp.model.DataType;

public class MRadioField extends MField {

	private List<MBooleanField> radiobuttons;

	protected MRadioField() {
		super(DataType.STRING);
	}

	public List<MBooleanField> getRadiobuttons() {
		return radiobuttons;
	}

	public void setRadiobuttons(List<MBooleanField> radiobuttons) {
		this.radiobuttons = radiobuttons;
	}
}
