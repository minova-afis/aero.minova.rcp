package aero.minova.rcp.workspace;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.processing.FilerException;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

@SuppressWarnings("restriction")
public class LifeCycle {

	@Inject
	Logger logger;

	@PostContextCreate
	void postContextCreate(IEclipseContext workbenchContext) throws IllegalStateException, IOException {
		// Show login dialog to the user
		String userName = "Test";// get username from login dialog;

		Preferences prefs = ConfigurationScope.INSTANCE.getNode("aero.minova.rcp.rcp.server");

		LocalDateTime now = LocalDateTime.now();
		logger.trace("Hallo Welt " + now.format(DateTimeFormatter.ISO_DATE_TIME));
		logger.debug("Hallo Welt " + now.format(DateTimeFormatter.ISO_DATE_TIME));
		logger.info("Hallo Welt " + now.format(DateTimeFormatter.ISO_DATE_TIME));
		logger.warn("Hallo Welt " + now.format(DateTimeFormatter.ISO_DATE_TIME));
		logger.error("Hallo Welt " + now.format(DateTimeFormatter.ISO_DATE_TIME));
		logger.error(new FilerException("nix da"), "Meilne Meldung");

		try {
			if (!prefs.nodeExists("Test")) {
				Preferences test = prefs.node("Test");
				test.put("user", "saak");
				test.put("url", "http://localhost");
				test.put("password", "valuee");
				test.flush();
			}
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// check if the instance location is already set,
		// otherwise setting another one will throw an IllegalStateException
		if (!Platform.getInstanceLocation().isSet()) {
			String defaultPath = System.getProperty("user.home");

			// build the desired path for the workspace
			String path = defaultPath + "/" + userName + "/workspace/";
			URL instanceLocationUrl = new URL("file", null, path);
			Platform.getInstanceLocation().set(instanceLocationUrl, false);
		}
	}

	@PreSave
	void preSave(IEclipseContext workbenchContext) {}

	@ProcessAdditions
	void processAdditions(IEclipseContext workbenchContext) {}

	@ProcessRemovals
	void processRemovals(IEclipseContext workbenchContext) {}
}
