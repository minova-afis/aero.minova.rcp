package aero.minova.rcp.model.form;

import java.util.ArrayList;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;

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

	@Override
	public void setValue(Value value, boolean user) {
		// Boolean Felder dürfen keinen null-Value haben, weil dies nicht dargestellt werden kann
		value = new Value(new String());
		for (MBooleanField b : radiobuttons) {
			if (b.getValue().getBooleanValue()) {
				super.setValue(new Value(b.getName()), user);
				return;
			}
		}
		super.setValue(value, user);
	}

}
