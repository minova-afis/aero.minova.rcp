package aero.minova.rcp.model.form;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.PeriodValue;

public class MPeriodField extends MField {

	protected MPeriodField() {
		super(DataType.PERIOD);
	}

	/**
	 * Liefert true, wenn der Value null ist ODER sowohl das Ausgangsdatum, als auch der UserInput und der Fälligkeitstermin null sind
	 * 
	 * @return
	 */
	public boolean isNullValue() {
		if (super.getValue() == null) {
			return true;
		}

		PeriodValue pv = (PeriodValue) super.getValue();
		return (pv.getBaseValue() == null && pv.getUserInput() == null && (pv.getDueDate() == null || pv.getDueDate().getInstantValue() == null));
	}
}
