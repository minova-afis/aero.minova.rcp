package aero.minova.rcp.preferencewindow.builder;

public class PreferenceDescriptor {

	String id;
	String label;
	double order;
	PreferenceSectionDescriptor section;
	PreferenceAccessor valueAccessor;
	DisplayType displayType;
	
	public DisplayType getDisplayType() {
		return displayType;
	}

	public PreferenceDescriptor(PreferenceSectionDescriptor section, String id, String label, double order, DisplayType displayType) {
		super();
		this.section = section;
		this.id = id;
		this.label = label;
		this.order = order;
		this.displayType = displayType;
	}

	public String getLabel() {
		return label;
	}

	public double getOrder() {
		return order;
	}

	public PreferenceSectionDescriptor getSection() {
		return section;
	}

	public PreferenceAccessor getValueAccessor() {
		return valueAccessor;
	}
	
	public void setValueAccessor(PreferenceAccessor valueAccessor) {
		this.valueAccessor = valueAccessor;
	}

	public String getId() {
		return id;
	}
	

}
