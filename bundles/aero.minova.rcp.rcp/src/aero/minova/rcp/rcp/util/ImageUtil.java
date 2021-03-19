package aero.minova.rcp.rcp.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ImageUtil {

	public static Image getImageDefault(final String file) {
		final Bundle bundle = FrameworkUtil.getBundle(ImageUtil.class);
		final URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		final ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}

}
