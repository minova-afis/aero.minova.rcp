package aero.minova.rcp.workspace.handler;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.osgi.service.datalocation.Location;

/**
 * This class stores workspace definition data. They can be stored in the configuration properties (except the password). The password can only be stored in the
 * workspace istself.
 *
 * @author Wilfried Saak
 */
class WorkspaceData {
	private URL connection;
	private boolean isInBackingStore = false;
	private boolean isSaved = false;
	private String password = "";
	private String profile = "";
	private String username = "";
	private String applicationArea = "";
	private Integer workspaceHash;
	private String message = "";

	public static WorkspaceData[] getWorkspaceData() {
		ISecurePreferences securePreferences = SecurePreferencesFactory.getDefault();
		ISecurePreferences nodeWorkspaces = securePreferences.node("aero.minova.rcp.workspace").node("workspaces");
		WorkspaceData[] workspaces = new WorkspaceData[nodeWorkspaces.childrenNames().length];
		int i = 0;

//		nodeWorkspaces.removeNode();
		for (String nodeName : nodeWorkspaces.childrenNames()) {
			WorkspaceData workspace = new WorkspaceData();
			workspaces[i++] = workspace;
			ISecurePreferences nodeWorkspace = nodeWorkspaces.node(nodeName);
			try {
				workspace.setConnection(new URL(nodeWorkspace.get("url", "N/A")));
			} catch (MalformedURLException e) {} catch (StorageException e) {}
			try {
				workspace.setUsername(nodeWorkspace.get("username", ""));
			} catch (StorageException e) {
				workspace.setUsername("");
			}
			try {
				workspace.setProfile(nodeWorkspace.get("profile", ""));
			} catch (StorageException e) {
				workspace.setProfile("");
			}
		}
		return workspaces;
	}

	public WorkspaceData() {}

	/**
	 * @return Connection to the server.
	 * @see WorkspaceHandler#newInstance(URL)
	 */
	public URL getConnection() {
		return connection;
	}

	public String getDisplayName() {
		return username + "@" + profile;
	}

	/**
	 * @return The password for the connection. Is available after the workspace is active.
	 * @see Platform#getInstanceLocation()
	 * @see Location#set(URL, boolean)
	 */
	public String getPassword() {
		return password;
	}

	public String getProfile() {
		return profile;
	}

	/**
	 * @return The username for the connection.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return Hash of displayName
	 * @see #getDisplayName()
	 */
	public Integer getWorkspaceHash() {
		return workspaceHash;
	}

	/**
	 * @return Hash of displayName
	 * @see #getDisplayName()
	 */
	public String getWorkspaceHashHex() {
		return Integer.toHexString(workspaceHash);
	}

	public boolean isInBackingStore() {
		return isInBackingStore;
	}

	public boolean isSaved() {
		return isSaved;
	}

	public void setConnection(URL connection) {
		if (isInBackingStore()) {
			throw new RuntimeException("WorkspaceData already stored in backing store!");
		}
		this.connection = connection;
	}

	protected void setInBackingStore(boolean isInBackingStore) {
		this.isInBackingStore = isInBackingStore;
	}

	public void setPassword(String password) {
		this.password = password;
		setSaved(false);
	}

	public void setProfile(String profile) {
		if (isInBackingStore()) {
			throw new RuntimeException("WorkspaceData already stored in backing store!");
		}
		this.profile = profile;
		updateWorkspaceHash();
	}

	private void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}

	public void setUsername(String username) {
		if (isInBackingStore()) {
			throw new RuntimeException("WorkspaceData already stored in backing store!");
		}
		this.username = username;
		updateWorkspaceHash();
	}

	private void updateWorkspaceHash() {
		this.workspaceHash = getDisplayName().hashCode();
	}

	public String getApplicationArea() {
		return applicationArea;
	}

	public void setApplicationArea(String applicationArea) {
		this.applicationArea = applicationArea;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
