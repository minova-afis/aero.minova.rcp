package aero.minova.rcp.workspace.handler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Stores preferences that are required in order to access a workspace and therefore to set the current platform.
 * 
 * @author avots
 */
@SuppressWarnings("restriction")
public class WorkspaceAccessPreferences {
	public static final String USER = "user";
	public static final String URL = "url";
	public static final String PASSWORD = "password";
	public static final String PROFILE = "profile";
	public static final String APPLICATION_AREA = "applicationArea";

	private static final String AERO_MINOVA_RCP_WORKSPACE = "aero.minova.rcp.workspace";
	private static final String WORKSPACES = "aero.minova.rcp.workspace";
	private static final String IS_PRIMARY_WORKSPACE = "isPrimaryWorkspace";

	public WorkspaceAccessPreferences() {
		throw new UnsupportedOperationException();
	}

	public static void storeWorkspaceAccessData(String workspaceName, String url, String userName, String password, String profile, String applicationArea,
			boolean isPrimaryWorksace) {
		final ISecurePreferences workspaces = SecurePreferencesFactory.getDefault()//
				.node(AERO_MINOVA_RCP_WORKSPACE)//
				.node(WORKSPACES);
		try {
			if (isPrimaryWorksace) {
				for (String otherWorkspaceName : workspaces.childrenNames()) {
					if (!Objects.equals(otherWorkspaceName, workspaceName)) {
						final ISecurePreferences otherWorkspacePrefs = workspaces.node(otherWorkspaceName);
						otherWorkspacePrefs.putBoolean(IS_PRIMARY_WORKSPACE, false, false);
						otherWorkspacePrefs.flush();
					}

				}
			}
			final ISecurePreferences workspacePrefs = workspaces.node(workspaceName);
			workspacePrefs.put(USER, userName, false);
			workspacePrefs.put(URL, url, false);
			workspacePrefs.put(PASSWORD, password, true);
			workspacePrefs.putBoolean(IS_PRIMARY_WORKSPACE, isPrimaryWorksace, false);
			workspacePrefs.put(PROFILE, profile, false);
			workspacePrefs.put(APPLICATION_AREA, applicationArea, false);
			workspacePrefs.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return a list of all WorkspaceHandler that are store in the preferences. The password is not available before the the workspace is set (#active())
	 */
	public static List<ISecurePreferences> getSavedWorkspaceAccessData(Logger logger) {
		final ISecurePreferences workspaces = SecurePreferencesFactory.getDefault()//
				.node(AERO_MINOVA_RCP_WORKSPACE)//
				.node(WORKSPACES);
		final List<ISecurePreferences> savedWorkspaceHandlers = new ArrayList<>();
		for (String workspaceName : workspaces.childrenNames()) {
			try {
				savedWorkspaceHandlers.add(workspaces.node(workspaceName));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return savedWorkspaceHandlers;
	}

	public static Optional<ISecurePreferences> getSavedPrimaryWorkspaceAccessData(Logger logger) {
		return getSavedWorkspaceAccessData(logger).stream()//
				.filter(w -> {
					try {
						return w.getBoolean(IS_PRIMARY_WORKSPACE, false);
					} catch (StorageException e) {
						throw new RuntimeException(e);
					}
				}).findFirst();
	}
}
