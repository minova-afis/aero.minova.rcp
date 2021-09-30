package aero.minova.rcp.rcp.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import aero.minova.rcp.preferences.ApplicationPreferences;

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
	 * Liefert einen ImageDescriptor für ein Bild, das im "images" Ordner im aero.minova.rcp.images Plugin liegt. Der ImageDescriptor sollte mit
	 * einem @LocalResourceManager verwendet werden, damit das Image automatisch disposed wird, wenn es nicht mehr benötigt wird. <br>
	 * TODO: Unterschiedliche ImageBundles unterstützen, diese vom Server laden (siehe #237) <br>
	 * TODO: Verschiedene Größen laden (durch Einstellung gesteuert, siehe #399)
	 *
	 * @param filename
	 * @return
	 */
	public static ImageDescriptor getImageDescriptorFromImagesBundle(String filename) {
		Bundle bundle = Platform.getBundle("aero.minova.rcp.images");
		String size = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE).get(ApplicationPreferences.FONT_SIZE, "M").toLowerCase();
		String iconSize = calculateImgaesSize(size, false);
		return ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("images/" + filename + iconSize)));
	}

	/**
	 * Liefert ein ToolbarImage zurück, welches im Vergleich zu dem Menuicon ein wenig größer ist.
	 *
	 * @param filename
	 * @return
	 */
	public static ImageDescriptor getToolBarImageDescriptorFromImagesBundle(String filename) {
		Bundle bundle = Platform.getBundle("aero.minova.rcp.images");
		String size = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE).get(ApplicationPreferences.FONT_SIZE, "M").toLowerCase();

		String iconSize = calculateImgaesSize(size, true);
		return ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("images/" + filename + iconSize)));
	}

	private static String calculateImgaesSize(String size, boolean isToolbar) {
		int calculatedSize = 0;
		switch (size) {
		case "s":
			calculatedSize = isToolbar ? 24 : 16;
			break;
		case "m":
			calculatedSize = isToolbar ? 32 : 24;
			break;
		case "l":
			calculatedSize = isToolbar ? 48 : 32;
			break;
		case "xl":
			calculatedSize = isToolbar ? 64 : 48;
			break;

		default:
			throw new RuntimeException("Angeforderte Größe ist nicht verfügbar!");
		}
		return "/" + calculatedSize + "x" + calculatedSize + ".png";
	}

}
