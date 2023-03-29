package aero.minova.rcp.preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Stores preferences that are required in order to access a workspace and therefore to set the current platform.
 *
 * @author avots
 */
public class WorkspaceAccessPreferences {
	public static final String USER = "user";
	public static final String URL = "url";
	public static final String PASSWORD = "password";
	public static final String PROFILE = "profile";
	public static final String APPLICATION_AREA = "applicationArea";

	private static final String AERO_MINOVA_RCP_WORKSPACE = "aero.minova.rcp.workspace";
	private static final String WORKSPACES = "aero.minova.rcp.workspace";
	public static final String IS_PRIMARY_WORKSPACE = "isPrimaryWorkspace";

	static ILog logger = Platform.getLog(WorkspaceAccessPreferences.class);

	private WorkspaceAccessPreferences() {}

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
			workspacePrefs.putBoolean(IS_PRIMARY_WORKSPACE, isPrimaryWorksace, false);
			workspacePrefs.put(PROFILE, profile, false);
			workspacePrefs.put(APPLICATION_AREA, applicationArea, false);
			workspacePrefs.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void deleteSavedWorkspace(String name) {
		final ISecurePreferences workspaces = SecurePreferencesFactory.getDefault()//
				.node(AERO_MINOVA_RCP_WORKSPACE)//
				.node(WORKSPACES);
		if (workspaces.nodeExists(name)) {
			ISecurePreferences node = workspaces.node(name);
			node.clear();
			node.removeNode();
			try {
				node.flush();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static List<ISecurePreferences> getSavedWorkspaceAccessData() {
		final ISecurePreferences workspaces = SecurePreferencesFactory.getDefault()//
				.node(AERO_MINOVA_RCP_WORKSPACE)//
				.node(WORKSPACES);
		final List<ISecurePreferences> savedWorkspaceHandlers = new ArrayList<>();
		for (String workspaceName : workspaces.childrenNames()) {
			try {
				savedWorkspaceHandlers.add(workspaces.node(workspaceName));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return savedWorkspaceHandlers;
	}

	public static void resetDefaultWorkspace() {
		WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData().ifPresent(prefs -> {
			try {
				prefs.putBoolean(WorkspaceAccessPreferences.IS_PRIMARY_WORKSPACE, false, false);
				prefs.flush();
			} catch (StorageException | IOException e1) {
				logger.info("Could not reset default workspace.", e1);
			}
		});
	}

	public static Optional<ISecurePreferences> getSavedPrimaryWorkspaceAccessData() {
		return getSavedWorkspaceAccessData().stream()//
				.filter(w -> {
					try {
						return w.getBoolean(IS_PRIMARY_WORKSPACE, false);
					} catch (StorageException e) {
						throw new RuntimeException(e);
					}
				}).findFirst();
	}
}
