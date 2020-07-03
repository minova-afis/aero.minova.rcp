package aero.minova.rcp.workspace.handler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Stores preferences that are required in order to access a workspace and
 * therefore to set the current platform.
 * 
 * @author avots
 *
 */
@SuppressWarnings("restriction")
public class WorkspaceAccessPreferences {
	private static final String AERO_MINOVA_RCP_WORKSPACE = "aero.minova.rcp.workspace";
	private static final String WORKSPACES = "aero.minova.rcp.workspace";

	public WorkspaceAccessPreferences() {
		throw new UnsupportedOperationException();
	}

	public static void storeWorkspaceAccessData(String workspaceName, String url, String userName, String password) {
		final ISecurePreferences workspaces = SecurePreferencesFactory.getDefault()//
				.node(AERO_MINOVA_RCP_WORKSPACE)//
				.node(WORKSPACES);
		try {
			final ISecurePreferences workspacePrefs = workspaces.node(workspaceName);
			workspacePrefs.put("user", userName, false);
			workspacePrefs.put("url", url, false);
			workspacePrefs.put("password", password, true);
			workspacePrefs.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return a list of all WorkspaceHandler that are store in the preferences. The password is not available before the the workspace is set (#active())
	 */
	public static List<WorkspaceHandler> getSavedWorkspaceHandlers(Logger logger) {
		final ISecurePreferences workspaceNodes = SecurePreferencesFactory.getDefault()//
				.node(AERO_MINOVA_RCP_WORKSPACE)//
				.node(WORKSPACES);
		final List<WorkspaceHandler> savedWorkspaceHandlers = new ArrayList<>();
		for (String workspaceName : workspaceNodes.childrenNames()) {
			try {
				savedWorkspaceHandlers.add(//
						WorkspaceHandler.newInstance(workspaceNodes.node(workspaceName), logger));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return savedWorkspaceHandlers;
	}
}
