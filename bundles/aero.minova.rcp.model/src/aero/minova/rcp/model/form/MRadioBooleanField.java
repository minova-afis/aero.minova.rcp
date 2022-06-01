package aero.minova.rcp.model.form;

import java.util.ArrayList;

import aero.minova.rcp.model.DataType;

public class MRadioBooleanField extends MField{

	private ArrayList<MBooleanField> radiobuttons;

	protected MRadioBooleanField() {
		super(DataType.STRING);
	}

	public ArrayList<MBooleanField> getRadiobuttons() {
		return radiobuttons;
	}

	public void setRadiobuttons(ArrayList<MBooleanField> radiobuttons) {
		this.radiobuttons = radiobuttons;
	}

}
