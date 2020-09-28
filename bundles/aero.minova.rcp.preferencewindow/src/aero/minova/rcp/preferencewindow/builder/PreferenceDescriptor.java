package aero.minova.rcp.preferencewindow.builder;

public class PreferenceDescriptor {

	final String key;
	String label;
	double order;
	PreferenceAccessor valueAccessor;
	DisplayType displayType;
	private Object[] possibleValues;
	

	public PreferenceDescriptor(String key, String label, double order, DisplayType displayType, Object... possibleValues) {
		this.key = key;
		this.label = label;
		this.order = order;
		this.displayType = displayType;
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

	public double getOrder() {
		return order;
	}

	public PreferenceAccessor getValue() {
		return valueAccessor;
	}
	
	public Object[] getPossibleValues() {
		return possibleValues;
	}



}
