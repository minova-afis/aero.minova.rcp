package aero.minova.rcp.preferencewindow.builder;

import java.util.List;
import java.util.Vector;

public class CustomPreferences {
	
	List<PreferenceTabDescriptor> tabs = new Vector<PreferenceTabDescriptor>();
	

	public List<PreferenceTabDescriptor> getTabs() {
		return tabs;
	}
	
	public void add(PreferenceTabDescriptor ptd) {
		tabs.add(ptd);
	}
}
