package aero.minova.rcp.model.form;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;

public class MShortDateField extends MField {

	public MShortDateField() {
		super(DataType.INSTANT);
		setDateTimeType(DateTimeType.DATE);
	}
}