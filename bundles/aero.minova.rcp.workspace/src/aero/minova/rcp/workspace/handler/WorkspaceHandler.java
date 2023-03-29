package aero.minova.rcp.workspace.handler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

import aero.minova.rcp.preferences.WorkspaceAccessPreferences;
import aero.minova.rcp.workspace.WorkspaceException;

/**
 * A workspace handler delivers the necessary information, that an application can work.<br/>
 * This includes the access to
 * <ul>
 * <li>plug-ins</li>
 * <li>internationalization</li>
 * <li>configuration data (e.g. forms, reports, menu definition, xbs file)</li>
 * <li>data functions</li>
 * </ul>
 *
 * @author Wilfried Saak
 */
public abstract class WorkspaceHandler {

	static ILog logger = Platform.getLog(WorkspaceHandler.class);
	protected final WorkspaceData workspaceData;

	/**
	 * Create a new instance for the given protocol. For instance only the following protocols are supported.
	 * <ul>
	 * <li>file: returns a new {@link aero.minova.rcp.workspace.handler.FileWorkspace}</li>
	 * <li>http:</li>
	 * <li>https: returns a new {@link aero.minova.rcp.workspace.handler.SpringBootWorkspace}</li>
	 * </ul>
	 *
	 * @param profile
	 * @param connection
	 * @return
	 * @throws MalformedURLException
	 */
	public static WorkspaceHandler newInstance(String profile, String connection) throws WorkspaceException {
		if (connection == null || connection.length() == 0) {
			try {
				List<ISecurePreferences> workspaceAccessDatas = WorkspaceAccessPreferences.getSavedWorkspaceAccessData();
				for (ISecurePreferences store : workspaceAccessDatas) {
					if (profile.equals(store.get(WorkspaceAccessPreferences.PROFILE, null))) {
						connection = store.get(WorkspaceAccessPreferences.URL, "");
						break;
					}
				}
			} catch (StorageException e) {
				logger.error(e.getMessage(), e);
			}
		}
		URL url;
		if (connection == null || connection.isEmpty()) {
			return null;
		}
		try {
			url = new URL(connection);
		} catch (MalformedURLException e) {
			throw new WorkspaceException(e.getMessage());
		}

		switch (url.getProtocol()) {
		case "file":
			return new FileWorkspace(url);
		case "http":
		case "https":
			return new SpringBootWorkspace(profile, url);
		default:
			return null;
		}
	}

	public static WorkspaceHandler newInstance(ISecurePreferences node) throws MalformedURLException, StorageException, WorkspaceException {
		String connection = node.get(WorkspaceAccessPreferences.URL, "N/A");
		String profile = node.get(WorkspaceAccessPreferences.PROFILE, "N/A");

		WorkspaceHandler instance = newInstance(profile, connection);
		if (instance != null) {
			instance.workspaceData.setConnection(new URL(connection));
			instance.workspaceData.setProfile(node.get("profile", "unknown"));
			instance.workspaceData.setUsername(node.get("username", ""));
			instance.workspaceData.setInBackingStore(true);
		}

		return instance;
	}

	protected WorkspaceHandler() {
		this.workspaceData = new WorkspaceData();
	}

	/**
	 * prüft, ob ein Verzeichnis existiert und erstellt es ggf.
	 *
	 * @param workspaceDir
	 */
	protected void checkDir(File workspaceDir, String name) {
		File dataDir = new File(workspaceDir.getAbsolutePath(), name);
		if (!dataDir.exists()) {
			dataDir.mkdir();
			logger.info(MessageFormat.format("Verzeichnis {0} im Workspace {1} neu angelegt.", name, workspaceDir));
		}
	}

	/**
	 * This method verifies, if the connection is accessible.
	 *
	 * @param connection
	 *            The connection URL. For instance there will be a possibility to connect to a web service (http / https) or to a directory (file://).
	 * @param username
	 *            The username for login
	 * @param password
	 *            The passordword for the user to login
	 * @param the
	 *            Application Area to read / store files
	 * @return true, if the connection could be established.
	 */
	public abstract boolean checkConnection(String username, String password, String applicationArea, Boolean saveAsDefault) throws WorkspaceException;

	/**
	 * @return Connection String to service, if different from connectionURL of constructor
	 */
	public String getConnectionString() {
		try {
			return workspaceData.getConnection().toURI().toURL().toString();
		} catch (MalformedURLException | URISyntaxException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public String getProfile() {
		return workspaceData.getProfile();
	}

	public String getUsername() {
		return workspaceData.getUsername();
	}

	public String getPassword() {
		return workspaceData.getPassword();
	}

	public String getApplicationArea() {
		return workspaceData.getApplicationArea();
	}

	/**
	 * Activate this workspace, if no workspace is set
	 *
	 * @exception WorkspaceException
	 *                if the Platform has alreadry a workspace
	 */
	public abstract void open() throws WorkspaceException;

	public String getDisplayName() {
		return workspaceData.getDisplayName();
	}

	public String getMessage() {
		return workspaceData.getMessage();
	}

}
