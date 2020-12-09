package aero.minova.rcp.rcp.nattable;

import java.util.Locale;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.rcp.util.TimeUtil;

public class ShortDateDisplayConverter extends DisplayConverter {

	private Locale locale;

	public ShortDateDisplayConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		return TimeUtil.getTimeString(((Value) canonicalValue).getInstantValue(), locale);
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		return null;
	}

}
