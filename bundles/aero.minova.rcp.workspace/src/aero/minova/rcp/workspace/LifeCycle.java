package aero.minova.rcp.workspace;

import java.io.IOException;
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
import org.eclipse.equinox.security.storage.StorageException;

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
		int returnCode;

		// Show login dialog to the user
		workspaceDialog = new WorkspaceDialog(null, logger, sync);

		if (!WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).isEmpty()) {
			ISecurePreferences sPrefs = WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).get();
			try {
				if (!Platform.getInstanceLocation().isSet()) {
					Platform.getInstanceLocation().set(new URL(sPrefs.get(WorkspaceAccessPreferences.APPLICATION_AREA, null)), false);
				}
			} catch (StorageException e) {
				logger.error(e);
			}
		} else {
			if ((returnCode = workspaceDialog.open()) != 0) {
				logger.info("ReturnCode: " + returnCode);
				System.exit(returnCode); // sollte nie aufgerufen werden, aber der Benutzer hat keinen Workspace
											// ausgesucht
			}
		}
		String workspaceLocation = null;
		try {
			workspaceLocation = Platform.getInstanceLocation().getURL().toURI().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		Objects.requireNonNull(workspaceLocation);
		dataService.setCredentials(workspaceDialog.getUsername(), workspaceDialog.getPassword(),
				workspaceDialog.getConnection(), workspaceLocation);

		Manager manager = new Manager();
		manager.postContextCreate(workbenchContext);

	}
}