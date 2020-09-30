package aero.minova.rcp.preferencewindow.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class PreferenceTabDescriptor {
	ImageDescriptor image;

	String id;
	String label;
	double order;
	List<PreferenceSectionDescriptor> sections = new ArrayList<>();

	public PreferenceTabDescriptor(String imageBundle, String imagePath, String id, String label, double order) {
		Bundle bundle = Platform.getBundle(imageBundle);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(imagePath)));
		this.id = id;
		this.label = label;
		this.order = order;
		
	}

	public Image getImage() {
		return image.createImage();
	}

	/**
	 * Liefert die interne ID für dieses TAB. Sie kann von anderen Plugins verwendet
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
