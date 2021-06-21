package aero.minova.rcp.rcp.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ImageUtil {

	private ImageUtil() {}

	public static Image getImageDefault(final String file) {
		final Bundle bundle = FrameworkUtil.getBundle(ImageUtil.class);
		final URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		final ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}

	// TODO: Unterschiedliche ImageBundles unterstützen, diese vom Server laden (siehe #237)
	// TODO: Verschiedene Größen laden (durch Einstellung gesteuert, siehe #399)
	public static ImageDescriptor getImageDescriptorFromImagesBundle(String name) {
		final Bundle bundle = Platform.getBundle("aero.minova.rcp.images");
		final URL url = FileLocator.find(bundle, new Path("images/icons/" + name + "/24x24.png"), null);
		return ImageDescriptor.createFromURL(url);
	}
}
