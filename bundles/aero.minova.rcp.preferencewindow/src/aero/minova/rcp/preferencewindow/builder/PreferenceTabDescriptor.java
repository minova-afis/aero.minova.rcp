package aero.minova.rcp.preferencewindow.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.graphics.Image;

import aero.minova.rcp.dataservice.ImageUtil;

public class PreferenceTabDescriptor {
	ImageDescriptor imageDescriptor;
	String id;
	String label;
	double order;
	List<PreferenceSectionDescriptor> sections = new ArrayList<>();

	private LocalResourceManager resManager;

	public PreferenceTabDescriptor(String iconURI, String id, String label, double order) {
		imageDescriptor = ImageUtil.getImageDescriptor(iconURI, false);
		this.id = id;
		this.label = label;
		this.order = order;

		resManager = new LocalResourceManager(JFaceResources.getResources());
	}

	public Image getImage() {
		return resManager.createImage(imageDescriptor);
	}

	/**
	 * Liefert die interne ID für dieses TAB. Sie kann von anderen Plugins verwendet werden
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
	 * Definiert den Platz in der Reihe. Dieser Wert muss zwischen >0 und <1 liegen.
	 * 
	 * @return
	 */
	public double getOrder() {
		return order;
	}

	public List<PreferenceSectionDescriptor> getSections() {
		return sections;
	}

	// TODO Überprüfung ob doppelte Id, sortieren nach order
	public void add(PreferenceSectionDescriptor psd) {
		sections.add(psd);
	}

}
