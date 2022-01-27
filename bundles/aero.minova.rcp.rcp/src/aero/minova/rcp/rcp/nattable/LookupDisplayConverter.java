package aero.minova.rcp.rcp.nattable;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

import aero.minova.rcp.model.LookupValue;

public class LookupDisplayConverter extends DisplayConverter {

	private GridLookupContentProvider contentProvider;

	public LookupDisplayConverter(GridLookupContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		if (canonicalValue instanceof LookupValue) {
			for (LookupValue lv : contentProvider.getValues()) {
				if (lv.getKeyLong().equals(((LookupValue) canonicalValue).keyLong)) {
					return lv.getKeyText();
				}
			}
		} else if (canonicalValue instanceof Integer) {
			for (LookupValue lv : contentProvider.getValues()) {
				if (lv.getKeyLong().equals(canonicalValue)) {
					return lv.getKeyText();
				}
			}
		}
		return "";
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		if (displayValue instanceof Integer) {
			for (LookupValue lv : contentProvider.getValues()) {
				if (lv.getKeyLong().equals(displayValue)) {
					return lv;
				}
			}
		} else if (displayValue instanceof String) {
			for (LookupValue lv : contentProvider.getValues()) {
				if (lv.getKeyText().equalsIgnoreCase((String) displayValue)) {
					return lv;
				}
			}
		}
		return null;
	}
}
