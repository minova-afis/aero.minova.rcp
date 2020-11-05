package aero.minova.rcp.workspace;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.translate.lifecycle.Manager;
import aero.minova.rcp.workspace.dialogs.WorkspaceDialog;
import aero.minova.rcp.workspace.handler.FileWorkspace;
import aero.minova.rcp.workspace.handler.SpringBootWorkspace;
import aero.minova.rcp.workspace.handler.WorkspaceHandler;

@SuppressWarnings("restriction")
public class LifeCycle {

	@Inject
	Logger logger;

	@Inject
	UISynchronize sync;
	
	@Inject
	IDataService dataService;

	@PostContextCreate
	void postContextCreate(IEclipseContext workbenchContext) throws IllegalStateException, IOException {
		WorkspaceDialog workspaceDialog;
		WorkspaceHandler workspaceHandler;
		int returnCode;
		
		// Show login dialog to the user
		workspaceDialog = new WorkspaceDialog(null, logger, sync);

		if ((returnCode = workspaceDialog.open()) != 0) {
			logger.info("RecurtnCode: " + returnCode);
			System.exit(returnCode); // sollte nie aufgerufen werden, aber der Benutzer hat keinen Workspace
										// ausgesucht
		}
		
		dataService.setCredentials(workspaceDialog.getUsername(), workspaceDialog.getPassword(), workspaceDialog.getConnection());
		
		Manager manager = new Manager();
		manager.postContextCreate(workbenchContext);

//		workspaceDialog.getWorkspaceData();

//		logger.info("Platform's working directory is set: " + Platform.getInstanceLocation().isSet());
//
//		String userName = "Test1";// get username from login dialog;
//		String workspaceName = "xyz1"; // muss noch ermittelt werden

//		URL connection = new URL("file:///Users/saak/Documents/Entwicklung/MINOVA");
//		switch (connection.getProtocol()) {
//		case "file":
//			workspaceHandler = new FileWorkspace(connection, logger);
//			break;
//		case "http":
//		case "https":
//			workspaceHandler = new SpringBootWorkspace(profile, connection, logger);
//			break;
//		default:
//			workspaceHandler = null;
//			break;
//		}

//		logger.info("Platform's working directory is set: " + Platform.getInstanceLocation().isSet());
//		// check if the instance location is already set,
//		// otherwise setting another one will throw an IllegalStateException
//		if (!Platform.getInstanceLocation().isSet()) {
//			String defaultPath = System.getProperty("user.home");
//
//			// build the desired path for the workspace
//			String path = defaultPath + "/.minwfc/" + workspaceName + "/";
//			URL instanceLocationUrl = new URL("file", null, path);
//			Platform.getInstanceLocation().set(instanceLocationUrl, false);
//			URL workspaceURL = Platform.getInstanceLocation().getURL();
//			File workspaceDir = new File(workspaceURL.getPath());
//			if (!workspaceDir.exists()) {
//				workspaceDir.mkdir();
//				logger.info(MessageFormat.format("Workspace {0} neu angelegt.", workspaceDir));
//			}
//			checkDir(workspaceDir, "config");
//			checkDir(workspaceDir, "data");
//			checkDir(workspaceDir, "i18n");
//			checkDir(workspaceDir, "plugins");
//		}
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
	void preSave(IEclipseContext workbenchContext) {
	}

	@ProcessAdditions
	void processAdditions(IEclipseContext workbenchContext) {
	}

	@ProcessRemovals
	void processRemovals(IEclipseContext workbenchContext) {
	}
}