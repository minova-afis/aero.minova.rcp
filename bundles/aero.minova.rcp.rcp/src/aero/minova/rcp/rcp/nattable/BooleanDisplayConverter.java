package aero.minova.rcp.rcp.nattable;

import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;

public class BooleanDisplayConverter extends DefaultBooleanDisplayConverter {

	@Override
	public Object canonicalToDisplayValue(Object displayValue) {
		if (displayValue == null) {
			return null;
		} else {
			return displayValue;
		}
	}
}
