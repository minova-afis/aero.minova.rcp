package aero.minova.rcp.model.form;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;

public class MDateTimeField extends MField {
	public MDateTimeField() {
		super(DataType.INSTANT);
		setDateTimeType(DateTimeType.DATETIME);
	}
}