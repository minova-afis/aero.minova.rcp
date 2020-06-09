package aero.minova.rcp.workspace;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.swt.widgets.Display;

import aero.minova.rcp.workspace.dialogs.WorkspaceDialog;
import aero.minova.rcp.workspace.handler.FileWorkspace;
import aero.minova.rcp.workspace.handler.SpringBootWorkspace;
import aero.minova.rcp.workspace.handler.WorkspaceHandler;

@SuppressWarnings("restriction")
public class LifeCycle {

	@Inject
	Logger logger;

	@PostContextCreate
	void postContextCreate(IEclipseContext workbenchContext) throws IllegalStateException, IOException {
		WorkspaceDialog workspaceDialog;
		WorkspaceHandler workspaceHandler;
		int returnCode;

		// Show login dialog to the user
		workspaceDialog = new WorkspaceDialog(Display.getDefault().getActiveShell(), logger);

		if ((returnCode = workspaceDialog.open()) != 0) {
			logger.info("RecurtnCode: " + returnCode);
			System.exit(returnCode); // sollte nie aufgerufen werden, aber der Benutzer hat keinen Workspace ausgesucht
		}

//		workspaceDialog.getWorkspaceData();
		
		logger.info("sss " + Platform.getInstanceLocation().isSet());

		String userName = "Test1";// get username from login dialog;
		String workspaceName = "xyz1"; // muss noch ermittelt werden

		URL connection = new URL("file:///Users/saak/Documents/Entwicklung/MINOVA");
		switch (connection.getProtocol()) {
		case "file":
			workspaceHandler = new FileWorkspace(connection, logger);
			break;
		case "http":
		case "https":
			workspaceHandler = new SpringBootWorkspace(connection, logger);
			break;
		default:
			workspaceHandler = null;
			break;
		}

//		Preferences serverPrefs = ConfigurationScope.INSTANCE.getNode("aero.minova.rcp.workspace.server");

		logger.info("sss " + Platform.getInstanceLocation().isSet());

		ISecurePreferences sprefs = SecurePreferencesFactory.getDefault();
		ISecurePreferences sNode = sprefs.node("aero.minova.rcp.workspace").node("workspaces");

//		logger.info("sss " + Platform.getInstanceLocation().isSet());
//		try {
////			if (!sNode.nodeExists(workspaceName)) {
////			}
//			ISecurePreferences test = sNode.node(workspaceName);
//			logger.info("sss " + Platform.getInstanceLocation().isSet());
//			test.put("user", userName, false);
//			logger.info("sss " + Platform.getInstanceLocation().isSet());
//			test.put("url", "http://localhost", false);
//			logger.info("sss " + Platform.getInstanceLocation().isSet());
//			test.put("password", "valuee", true); // setzt auch den Workspace
//			logger.info("sss " + Platform.getInstanceLocation().isSet());
//			test.flush();
//		} catch (StorageException e) {
//			logger.error(e, "Error storing access data ");
//		}

		logger.info("sss " + Platform.getInstanceLocation().isSet());
		// check if the instance location is already set,
		// otherwise setting another one will throw an IllegalStateException
		if (!Platform.getInstanceLocation().isSet()) {
			String defaultPath = System.getProperty("user.home");

			// build the desired path for the workspace
			String path = defaultPath + "/.minwfc/" + workspaceName + "/";
			URL instanceLocationUrl = new URL("file", null, path);
			Platform.getInstanceLocation().set(instanceLocationUrl, false);
			URL workspaceURL = Platform.getInstanceLocation().getURL();
			File workspaceDir = new File(workspaceURL.getPath());
			if (!workspaceDir.exists()) {
				workspaceDir.mkdir();
				logger.info(MessageFormat.format("Workspace {0} neu angelegt.", workspaceDir));
			}
			checkDir(workspaceDir, "config");
			checkDir(workspaceDir, "data");
			checkDir(workspaceDir, "i18n");
			checkDir(workspaceDir, "plugins");
		}
	}

	/**
	 * prüft, ob ein Verzeichnis existiert und erstellt es ggf.
	 * 
	 * @param workspaceDir
	 */
	private void checkDir(File workspaceDir, String name) {
		File dataDir = new File(workspaceDir.getAbsolutePath() + "/" + name);
		if (!dataDir.exists()) {
			dataDir.mkdir();
			logger.info(MessageFormat.format("Verzeichnis {0} im Workspace {1} neu angelegt.", name, workspaceDir));
		}
	}

	@PreSave
	void preSave(IEclipseContext workbenchContext) {}

	@ProcessAdditions
	void processAdditions(IEclipseContext workbenchContext) {}

	@ProcessRemovals
	void processRemovals(IEclipseContext workbenchContext) {}
}