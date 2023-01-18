package aero.minova.rcp.util;

import java.io.File;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ScreenshotUtil {

	private ScreenshotUtil() {}

	public static void menuDetectAction(MenuDetectEvent event, Control control, String filename, TranslationService translationService) {
		final Menu menu = new Menu(control);

		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText(translationService.translate("@Action.Screenshot", null));
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ScreenshotUtil.createScreenshot(control, filename, translationService);
			}
		});

		Point p = new Point(event.x, event.y);
		menu.setLocation(p);
		menu.setVisible(true);
		menu.addMenuListener(new MenuListener() {
			@Override
			public void menuShown(MenuEvent e) {
				// do nothing
			}

			@Override
			public void menuHidden(MenuEvent e) {
				control.getDisplay().asyncExec(menu::dispose);
			}
		});
	}

	public static void createScreenshot(Control control, String filename, TranslationService translationService) {
		// Inhalt des Controls in Image speichern
		final Point controlSize = control.getSize();
		final GC gc = new GC(control);
		Image image = new Image(Display.getCurrent(), controlSize.x, controlSize.y);
		gc.copyArea(image, 0, 0);
		gc.dispose();

		// Letzen Speicherort herausfinden
		final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("aero.minova.rcp.screenshot");
		String directory = prefs.get("directory", null);

		// Dialog öffnen
		final FileDialog fd = new FileDialog(Display.getCurrent().getShells()[0], SWT.SAVE);
		fd.setText(translationService.translate("@SaveScreenshot.Title", null));
		fd.setFilterExtensions(new String[] { "*.png" });
		fd.setFilterNames(new String[] { "Bilddateien(*.png)" });
		fd.setFilterPath(directory);
		fd.setOverwrite(true);
		fd.setFileName(filename.toLowerCase());
		final String selected = fd.open();

		if (selected != null) {
			// Verzeichnis merken
			final File f = new File(selected);
			prefs.put("directory", f.getParent());

			// Datei Speichern
			final ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { image.getImageData() };
			loader.save(selected, SWT.IMAGE_PNG);
		}

		image.dispose();
	}

}
