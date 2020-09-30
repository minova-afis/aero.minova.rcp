package aero.minova.rcp.preferencewindow.builder;

public interface PreferenceAccessor {
	
	public void flush(PreferenceSectionDescriptor section, Object value);
	
	public Object getValue(PreferenceSectionDescriptor section);


	public Object putValue(PreferenceSectionDescriptor section, Object value);

}
