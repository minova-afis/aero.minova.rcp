package aero.minova.rcp.preferencewindow.builder;

public class PreferenceDescriptor {

	final String key;
	String label;
	String tooltip;
	double order;
	PreferenceAccessor valueAccessor;
	DisplayType displayType;
	private Object[] possibleValues;
	Object defaultValue;

	public PreferenceDescriptor(String key, String label, String tooltip, double order, DisplayType displayType, Object defaultValue,
			Object... possibleValues) {
		this.key = key;
		this.label = label;
		this.tooltip = tooltip;
		this.order = order;
		this.displayType = displayType;
		this.defaultValue = defaultValue;
		this.possibleValues = possibleValues;
	}

	public String getKey() {
		return key;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}

	public String getLabel() {
		return label;
	}

	public String getTooltip() {
		return tooltip;
	}

	public double getOrder() {
		return order;
	}

	public PreferenceAccessor getValue() {
		return valueAccessor;
	}

	public Object[] getPossibleValues() {
		return possibleValues;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

}
