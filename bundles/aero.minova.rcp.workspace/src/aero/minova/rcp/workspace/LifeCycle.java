package aero.minova.rcp.workspace;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.security.storage.ISecurePreferences;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.preferences.WorkspaceAccessPreferences;
import aero.minova.rcp.translate.lifecycle.Manager;
import aero.minova.rcp.workspace.dialogs.WorkspaceDialog;

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

		// Show login dialog to the user
		workspaceDialog = new WorkspaceDialog(null, logger, sync);
		URI workspaceLocation = null;

		if (!WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).isEmpty()) {
			try {
				ISecurePreferences sPrefs = WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).get();
				if (!Platform.getInstanceLocation().isSet()) {
					Platform.getInstanceLocation().set(new URL(sPrefs.get(WorkspaceAccessPreferences.APPLICATION_AREA, null)), false);
					try {
						workspaceLocation = Platform.getInstanceLocation().getURL().toURI();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					if (workspaceLocation == null) {
						WorkspaceAccessPreferences.resetDefaultWorkspace(logger);
						loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
					} else {
						dataService.setCredentials(sPrefs.get(WorkspaceAccessPreferences.USER, null), sPrefs.get(WorkspaceAccessPreferences.PASSWORD, null),
								sPrefs.get(WorkspaceAccessPreferences.URL, null), workspaceLocation);
						dataService.setLogger(logger);
					}
				}
			} catch (Exception e) {
				logger.error(e);
				loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
			}
		} else {
			loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
		}

		Manager manager = new Manager();
		manager.postContextCreate(workbenchContext);

	}

	private void loadWorkspaceConfigManually(WorkspaceDialog workspaceDialog, URI workspaceLocation) {
		int returnCode;
		if ((returnCode = workspaceDialog.open()) != 0) {
			logger.info("ReturnCode: " + returnCode);
			System.exit(returnCode); // sollte nie aufgerufen werden, aber der Benutzer hat keinen Workspace
										// ausgesucht
		}
		try {
			workspaceLocation = Platform.getInstanceLocation().getURL().toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		Objects.requireNonNull(workspaceLocation);
		dataService.setCredentials(workspaceDialog.getUsername(), //
				workspaceDialog.getPassword(), //
				workspaceDialog.getConnection(), //
				workspaceLocation);
		dataService.setLogger(logger);
	}
}