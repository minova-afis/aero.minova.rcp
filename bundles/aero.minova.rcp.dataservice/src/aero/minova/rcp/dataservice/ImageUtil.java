package aero.minova.rcp.dataservice;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

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
	public static ImageDescriptor getImageDescriptorFromImagesBundle(String filename, boolean isToolBar) {
		Bundle bundle = Platform.getBundle("aero.minova.rcp.images");
		String size = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE).get(ApplicationPreferences.FONT_SIZE, "M").toLowerCase();
		String iconSize = calculateImagesSize(size, isToolBar);
		return ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("images/" + filename + "/" + iconSize)));
	}

	private static String calculateImagesSize(String size, boolean isToolbar) {
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
		return calculatedSize + "x" + calculatedSize + ".png";
	}

	public static String retrieveIcon(String icon, boolean isToolBar) {
		String iconWithoutExtension = icon.replace(".png", "").replace(".ico", "").toLowerCase();
		//
		// im Falle der Unit tests haben wir keinen bundle context
		Bundle bundle = FrameworkUtil.getBundle(ImageUtil.class);
		ServiceReference<IDataService> serviceReference = bundle.getBundleContext().getServiceReference(IDataService.class);
		IDataService service = bundle.getBundleContext().getService(serviceReference);
		Objects.requireNonNull(service);
		java.nio.file.Path storagePath = service.getStoragePath().resolve("Images");
		String size = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE).get(ApplicationPreferences.FONT_SIZE, "M").toLowerCase();

		if (storagePath != null && storagePath.toFile().isDirectory()) {
			File[] listFiles = storagePath.toFile().listFiles();
			java.nio.file.Path resolve = null;
			if (listFiles != null) {
				for (File file : listFiles) {
					if (file.getName().toLowerCase().equals(iconWithoutExtension)) {
						resolve = file.toPath().resolve(calculateImagesSize(size, isToolBar));
					}
				}
			}
			if (resolve != null) {
				System.out.println(resolve.toUri());
				return resolve.toUri().toString();
			}
		}
		return "ERROR! Icon not found: " + icon;

	}

	/**
	 * Updated die Größe der Model-Icons (Icons der Parts und der Part-Toolbars) entsprechend der Einstellungen
	 * 
	 * @param element
	 * @param modelService
	 */
	public static void updateModelIcons(MUIElement element, EModelService modelService) {

		// Finden der Parts
		List<MPart> mParts = modelService.findElements(element, null, MPart.class);
		for (MPart mPart : mParts) {
			// Part-Icons
			mPart.setIconURI(getNewIconString(mPart.getIconURI()));

			// Part-Toolbars
			if (mPart.getToolbar() != null) {
				List<MToolBarElement> children = mPart.getToolbar().getChildren();
				for (MToolBarElement mToolBarElement : children) {
					MHandledToolItem ti = (MHandledToolItem) mToolBarElement;
					ti.setIconURI(getNewIconString(ti.getIconURI()));
				}
			}
		}
	}

	/**
	 * Ersetzt im Icon String die Größe entsprechend der Einstellungen
	 * 
	 * @param oldIconString
	 * @return
	 */
	private static String getNewIconString(String oldIconString) {
		String size = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE).get(ApplicationPreferences.FONT_SIZE, "M").toLowerCase();

		int indexOfLastSlash = oldIconString.lastIndexOf("/");
		String newIconString = oldIconString.substring(0, indexOfLastSlash + 1);
		return newIconString + calculateImagesSize(size, false);
	}

}
