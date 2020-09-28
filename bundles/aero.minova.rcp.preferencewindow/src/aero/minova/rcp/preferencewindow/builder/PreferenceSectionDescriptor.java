package aero.minova.rcp.preferencewindow.builder;

import java.util.ArrayList;
import java.util.List;

public class PreferenceSectionDescriptor {

	public PreferenceSectionDescriptor(String id, String label, double order) {
		super();
		this.id = id;
		this.label = label;
		this.order = order;
	}

	String id;
	String label;
	double order;
	List<PreferenceDescriptor> preferences = new ArrayList<>();
	
	/**
	 * Liefert die interne ID für diese Section. Sie kann von anderen Plugins verwendet
	 * werden
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Liefert den Text, der angezeigt wird. Dieser Text muss übersetzt werden.
	 * 
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Definiert die Zeile. Dieser Wert muss zwischen >0 und <1 liegen.
	 * @return
	 */
	public double getOrder() {
		return order;
	}
	
	public List<PreferenceDescriptor> getPreferences() {
		return preferences;
	}

	public void add(PreferenceDescriptor pd) {
		preferences.add(pd);
		
	}
	
	
}
