package aero.minova.rcp.dataservice;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import aero.minova.rcp.preferences.ApplicationPreferences;

public class ImageUtil {

	private ImageUtil() {}

	/**
	 * Liefert einen ImageDescriptor für ein Bild, das im "icons" Ordner im aero.minova.rcp.images Plugin liegt. Diese gibt es nur ein einer Größe<br>
	 * Der ImageDescriptor sollte mit einem @LocalResourceManager verwendet werden, damit das Image automatisch disposed wird, wenn es nicht mehr benötigt wird.
	 *
	 * @param filename
	 * @return
	 */
	public static ImageDescriptor getImageDefault(final String filename) {
		Bundle bundle = Platform.getBundle("aero.minova.rcp.images");
		final URL url = FileLocator.find(bundle, new Path("icons/" + filename), null);
		return ImageDescriptor.createFromURL(url);
	}

	/**
	 * Liefert einen ImageDescriptor für das Bild. <br>
	 * Zuerst wird im "images" Ordner im aero.minova.rcp.images Plugin gesucht. Wird es dort nicht gefunden wird im vom Server geladenen Ordner gesucht. <br>
	 * Der ImageDescriptor sollte mit einem @LocalResourceManager verwendet werden, damit das Image automatisch disposed wird, wenn es nicht mehr benötigt wird.
	 * <br>
	 *
	 * @param filename
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String filename, boolean isToolBar) {
		if (filename == null || filename.equals("")) {
			return ImageDescriptor.createFromURL(null);
		}

		String location = retrieveIcon(filename, isToolBar);

		try {
			URL url = URI.create(location).toURL();
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException | IllegalArgumentException e) {
			System.err.println("Invalid URI " + location + " for icon " + filename);
		}
		return ImageDescriptor.createFromURL(null);
	}

	/**
	 * Liefert einen String mit der Location des Bildes. <br>
	 * Zuerst wird im "images" Ordner im aero.minova.rcp.images Plugin gesucht. Wird es dort nicht gefunden wird im vom Server geladenen Ordner gesucht.
	 *
	 * @param filename
	 * @return
	 */
	public static String retrieveIcon(String icon, boolean isToolBar) {

		if (icon == null) {
			return null;
		}

		String iconWithoutExtension = icon.toLowerCase().replace(".png", "").replace(".ico", "");
		String size = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE).get(ApplicationPreferences.FONT_ICON_SIZE, "M").toLowerCase();

		// Zuerst im Images-Ordner des aero.minova.rcp.images Plugins suchen
		Bundle bundle = Platform.getBundle("aero.minova.rcp.images");
		String iconSize = calculateImagesSize(size, isToolBar);
		URL localURL = FileLocator.find(bundle, new Path("images/" + icon + "/" + iconSize));
		if (localURL != null) {
			try {
				return localURL.toURI().toString();
			} catch (URISyntaxException e) {}
		}

		// Ansonsten im heruntergelandenen Images Ordner suchen
		bundle = FrameworkUtil.getBundle(ImageUtil.class);
		ServiceReference<IDataService> serviceReference = bundle.getBundleContext().getServiceReference(IDataService.class);
		IDataService service = bundle.getBundleContext().getService(serviceReference);
		Objects.requireNonNull(service);
		java.nio.file.Path storagePath = service.getStoragePath().resolve("images");

		if (storagePath != null && storagePath.toFile().isDirectory()) {
			File[] listFiles = storagePath.toFile().listFiles();
			java.nio.file.Path resolve = null;
			if (listFiles != null) {
				for (File file : listFiles) {
					if (file.getName().toLowerCase().equals(iconWithoutExtension)) {
						resolve = file.toPath().resolve(iconSize);
					}
				}
			}
			if (resolve != null) {
				return resolve.toUri().toString();
			}
		}

		// Wenn nichts gefunden wurde ursprünglichen Text zurückgeben
		return icon;
	}

	/**
	 * Updated die Größe der Model-Icons (Icons der Parts und der Part-Toolbars) entsprechend der Einstellungen
	 * 
	 * @param element
	 * @param modelService
	 */
	public static void updateIconsForToolbarItems(MToolBar toolbar) {
		for (MToolBarElement mToolBarElement : toolbar.getChildren()) {
			MHandledToolItem ti = (MHandledToolItem) mToolBarElement;

			// Menüs der Part-Toolbar-Items
			if (ti.getMenu() != null) {
				for (MMenuElement mMenuElement : ti.getMenu().getChildren()) {
					mMenuElement.setIconURI(getNewIconString(mMenuElement.getIconURI()));
				}
			}
			String newIconString = getNewIconString(ti.getIconURI());
			ti.setIconURI(newIconString);
		}
	}

	/**
	 * Updated die Größe der Model-Icons (Icons der Parts und der Part-Toolbars) entsprechend der Einstellungen
	 * 
	 * @param element
	 * @param modelService
	 */
	public static void updateIconsForPart(MPart part) {
		part.setIconURI(getNewIconString(part.getIconURI()));
	}

	/**
	 * Ersetzt im Icon String die Größe entsprechend der Einstellungen
	 * 
	 * @param oldIconString
	 * @return
	 */
	private static String getNewIconString(String oldIconString) {
		if (oldIconString == null || oldIconString.isBlank()) {
			return oldIconString;
		}

		String size = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE).get(ApplicationPreferences.FONT_ICON_SIZE, "M").toLowerCase();

		int indexOfLastSlash = oldIconString.lastIndexOf("/");
		String newIconString = oldIconString.substring(0, indexOfLastSlash + 1);
		return newIconString + calculateImagesSize(size, false);
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

		// Skalierung unter Windows beachten
		if ("win32".equals(SWT.getPlatform())) {
			calculatedSize = scale(calculatedSize);
		}

		return calculatedSize + "x" + calculatedSize + ".png";
	}

	/**
	 * Unter Windows müssen die Bildgrößen angepasst werden, je nachdem welche Skalierung genutzt wird
	 * 
	 * @param calculatedSize
	 * @return
	 */
	private static int scale(int calculatedSize) {
		try {
			int dpi = Display.getCurrent().getDPI().x;

			if (dpi == 144 || dpi == 168) {
				switch (calculatedSize) {
				case 16:
					calculatedSize = 24;
					break;
				case 24:
					calculatedSize = 32;
					break;
				case 32:
					calculatedSize = 48;
					break;
				case 48:
					calculatedSize = 64;
					break;
				}
			} else if (dpi == 192) {
				switch (calculatedSize) {
				case 16:
					calculatedSize = 32;
					break;
				case 24:
					calculatedSize = 48;
					break;
				case 32:
				case 48:
					calculatedSize = 64;
					break;
				}
			}
		} catch (NullPointerException e) {
			// Tritt in UI Tests auf
		}
		return calculatedSize;
	}

}
