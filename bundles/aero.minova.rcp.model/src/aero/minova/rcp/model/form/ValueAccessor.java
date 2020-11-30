package aero.minova.rcp.model.form;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;

public interface ValueAccessor {

	public void setValue(Value value, boolean user);

	public void setValue(Row row);
}
