package aero.minova.rcp.workspace;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;

import aero.minova.rcp.dataservice.IDataService;
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

		if ((returnCode = workspaceDialog.open()) != 0) {
			logger.info("RecurtnCode: " + returnCode);
			System.exit(returnCode); // sollte nie aufgerufen werden, aber der Benutzer hat keinen Workspace
										// ausgesucht
		}
		dataService.setCredentials(workspaceDialog.getUsername(), workspaceDialog.getPassword(),
				workspaceDialog.getConnection());

		Manager manager = new Manager();
		manager.postContextCreate(workbenchContext);

	}
}