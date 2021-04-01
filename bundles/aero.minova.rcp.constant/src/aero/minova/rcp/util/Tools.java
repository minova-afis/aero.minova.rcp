package aero.minova.rcp.util;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.Locale;

public class Tools {

	public static void openURL(String url) throws Exception {
		if (url == null || url.isEmpty()) {
			throw new Exception("Cannot open empty/null location");
		}

		// FIXME hier wird AWT verwendet
		// vl. sollten wir lieber so was in der Art verwenden:
		// Runtime.getRuntime().exec("cmd.exe /c start url");
		// das ist aber betriebssystemabhängig

		// Desktop.isDesktopSupported() wirft meistens beim 1. Versuch eine XC, ab dann funktionierts
		// Um dem Nutzer die Fehlermeldung zu ersparen, probieren wirs gleich mehrmals
		Boolean isDesktopSupported = null;
		int tries = 3;
		while (isDesktopSupported == null) {
			if (tries-- > 0) {
				try {
					isDesktopSupported = Desktop.isDesktopSupported();
				} catch (final Throwable t) {}
			} else {
				// letzter Versuch ohne try/catch
				isDesktopSupported = Desktop.isDesktopSupported();
			}
		}

		if (isDesktopSupported) {
			try {
				if (url.toLowerCase(Locale.ENGLISH).startsWith("http") || url.toLowerCase(Locale.ENGLISH).startsWith("www")) {
					// Browser
					final URL toBrowse = new URL(url);
					Desktop.getDesktop().browse(toBrowse.toURI());
				} else {
					// Datei öffnen
					final File f = new File(url);
					if (!f.exists()) {
						throw new Exception("File not found " + url);
					}
					Desktop.getDesktop().open(f);
				}
			} catch (final Exception e) {
				throw new Exception("Error occured during the view:\r\n" + e.getMessage(), e);
			}
		} else {
			throw new Exception("Desktop not supported");
		}
	}
}
