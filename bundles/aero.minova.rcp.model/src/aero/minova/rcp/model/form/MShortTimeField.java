package aero.minova.rcp.model.form;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;

public class MShortTimeField extends MField {

	public MShortTimeField() {
		super(DataType.INSTANT);
		setDateTimeType(DateTimeType.TIME);
	}
}