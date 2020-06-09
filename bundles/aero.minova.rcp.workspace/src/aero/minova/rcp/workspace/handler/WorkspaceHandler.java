package aero.minova.rcp.workspace.handler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

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
@SuppressWarnings("restriction")
public abstract class WorkspaceHandler {

	protected final Logger logger;
	protected final WorkspaceData workspaceData;

	/**
	 * Create a new instance for the given protocol. For instance only the following protocols are supported.
	 * <ul>
	 * <li>file: returns a new {@link aero.minova.rcp.workspace.handler.FileWorkspace}</li>
	 * <li>http:</li>
	 * <li>https: returns a new {@link aero.minova.rcp.workspace.handler.SpringBootWorkspace}</li>
	 * </ul>
	 * 
	 * @param connection
	 * @return
	 */
	public static WorkspaceHandler newInstance(URL connection, Logger logger) {
		switch (connection.getProtocol()) {
		case "file":
			return new FileWorkspace(connection, logger);
		case "http":
		case "https":
			return new SpringBootWorkspace(connection, logger);
		default:
			return null;
		}
	}

	private static WorkspaceHandler newInstance(ISecurePreferences node, Logger logger) throws MalformedURLException, StorageException {
		URL connection = new URL(node.get("url", "N/A"));

		WorkspaceHandler instance = newInstance(connection, logger);
		instance.workspaceData.setConnection(connection);
		instance.workspaceData.setProfile(node.get("profile", "unknown"));
		instance.workspaceData.setUsername(node.get("username", ""));
		instance.workspaceData.setInBackingStore(true);
		
		return instance;
	}

	/**
	 * @return a list of all WorkspaceHandler that are store in the preferences. The password is not available before the the workspace is set (#active())
	 */
	public static WorkspaceHandler[] getSavedHandlers(Logger logger) {
		ISecurePreferences securePreferences = SecurePreferencesFactory.getDefault();
		ISecurePreferences workspaceNodes = securePreferences.node("aero.minova.rcp.workspace").node("workspaces");
		String workspaceNames[] = workspaceNodes.childrenNames();
		WorkspaceHandler workspaces[] = new WorkspaceHandler[workspaceNames.length];

		for (int i = 0; i < workspaceNames.length; i++) {
			try {
				workspaces[i] = newInstance(workspaceNodes.node(workspaceNames[i]), logger);
			} catch (MalformedURLException | StorageException e) {
				logger.error(e);
				workspaces[i] = null;
			}
		}

		return workspaces;
	}

	protected WorkspaceHandler(Logger logger) {
		this.logger = logger;
		this.workspaceData = new WorkspaceData();
	}

	/**
	 * prüft, ob ein Verzeichnis existiert und erstellt es ggf.
	 * 
	 * @param workspaceDir
	 */
	protected void checkDir(File workspaceDir, String name) {
		File dataDir = new File(workspaceDir.getAbsolutePath() + "/" + name);
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
	 * @return true, if the connection could be established.
	 */
	public abstract boolean checkConnection(String username, String password) throws WorkspaceException;

	/**
	 * @return Connection String to service, if different from connectionURL of constructor
	 */
	public String getConnectionString() {
		return "";
	}

	/**
	 * @return Profile name of application to display
	 */
	public String getProfile() {
		return workspaceData.getProfile();
	}

	/**
	 * @return username in remote system. This value can differ from the username, used for establishing the connection.
	 */
	public String getRemoteUsername() {
		return "";
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
}
