package aero.minova.rcp.rcp.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ImageUtil {

	private ImageUtil() {}

	/**
	 * Liefert einen ImageDescriptor für ein Bild, das im "icons" Ordner im aero.minova.rcp.rcp Plugin liegt. Der ImageDescriptor sollte mit
	 * einem @LocalResourceManager verwendet werden, damit das Image automatisch disposed wird, wenn es nicht mehr benötigt wird.
	 * 
	 * @param filename
	 * @return
	 */
	public static ImageDescriptor getImageDefault(final String filename) {
		final Bundle bundle = FrameworkUtil.getBundle(ImageUtil.class);
		final URL url = FileLocator.find(bundle, new Path("icons/" + filename), null);
		return ImageDescriptor.createFromURL(url);
	}

	/**
	 * Liefert einen ImageDescriptor für ein Bild, das im "images/icons" Ordner im aero.minova.rcp.images Plugin liegt. Der ImageDescriptor sollte mit
	 * einem @LocalResourceManager verwendet werden, damit das Image automatisch disposed wird, wenn es nicht mehr benötigt wird. <br>
	 * TODO: Unterschiedliche ImageBundles unterstützen, diese vom Server laden (siehe #237) <br>
	 * TODO: Verschiedene Größen laden (durch Einstellung gesteuert, siehe #399)
	 * 
	 * @param filename
	 * @return
	 */
	public static ImageDescriptor getImageDescriptorFromImagesBundle(String filename) {
		final Bundle bundle = Platform.getBundle("aero.minova.rcp.images");
		final URL url = FileLocator.find(bundle, new Path("images/icons/" + filename + "/24x24.png"), null);
		return ImageDescriptor.createFromURL(url);
	}
}
