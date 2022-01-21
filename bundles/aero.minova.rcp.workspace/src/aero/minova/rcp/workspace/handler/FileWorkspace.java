package aero.minova.rcp.workspace.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.log.Logger;

import aero.minova.rcp.workspace.LifeCycle;
import aero.minova.rcp.workspace.WorkspaceException;

@SuppressWarnings("restriction")
public class FileWorkspace extends WorkspaceHandler {

	private String connectionString = "";

	/**
	 * @param connection
	 */
	public FileWorkspace(URL connection, Logger logger) {
		super(logger);
		workspaceData.setConnection(connection);
	}

	@Override
	public boolean checkConnection(String username, String password, String applicationArea, Boolean setAsDefault) throws WorkspaceException {
		// Verbindungswerte zurücksetzen
		workspaceData.setUsername(username);
		workspaceData.setPassword(password);

		try {
			// Existenz des Hauptverzeichnisses prüfen
			File configDir = new File(workspaceData.getConnection().toURI());
			if (!configDir.exists()) {
				throw new WorkspaceException(MessageFormat.format("Path {0} does not exist!", configDir.getAbsolutePath()));
			}
			if (!configDir.isDirectory()) {
				throw new WorkspaceException(MessageFormat.format("File {0} is not a directory!", configDir.getAbsolutePath()));
			}

			// Unterverzeichnis für die Anwendung überprüfen
			File appDir = new File(configDir.getAbsolutePath() + "/Program Files/application");
			if (!appDir.exists()) {
				throw new WorkspaceException(MessageFormat.format("Application Folder {0} does not exist!", appDir.getAbsolutePath()));
			}

			// Anwendungsdefinition prüfen
			File applicationXbs = new File(appDir.getAbsolutePath() + "/application.xbs");
			if (!applicationXbs.exists()) {
				try {
					// Wir schreiben mal eine Beispieldatei
					Preferences sysNode = Preferences.userRoot();
					sysNode = sysNode.node("/aero.minova/application");
					sysNode.put("profile", "EXAMPLE");
					FileOutputStream applicationOS = new FileOutputStream(applicationXbs);
					sysNode.exportNode(applicationOS);
					applicationOS.close();
				} catch (IOException | BackingStoreException e) {
					e.printStackTrace();
				}
				throw new WorkspaceException(MessageFormat.format("application.xbs does not exist in folder {0}!", appDir.getAbsolutePath()));
			}

			// Anwendungsdefinition einlesen
			Preferences prefs = Preferences.systemRoot();
			try {
				InputStream prefsIS = new FileInputStream(applicationXbs);
				Preferences.importPreferences(prefsIS);
			} catch (IOException | InvalidPreferencesFormatException e) {
				e.printStackTrace();
			}
			prefs = prefs.node("/aero.minova/application");
			workspaceData.setProfile(prefs.get("profile", "N/A"));

			// Datenbankdefinition prüfen
			File connectionXbs = new File(appDir.getAbsolutePath() + "/connection.xbs");
			if (!connectionXbs.exists()) {
				try {
					// Wir schreiben mal eine Beispieldatei
					Preferences sysNode = Preferences.userRoot();
					sysNode = sysNode.node("/aero.minova/connection");
					sysNode.put("url", "jdbc:jtds:sqlserver://localhost/SIS");
					sysNode.put("driver", "net.sourceforge.jtds.jdbc.Driver");
					FileOutputStream connectionOS = new FileOutputStream(connectionXbs);
					sysNode.exportNode(connectionOS);
					connectionOS.close();
				} catch (IOException | BackingStoreException e) {
					e.printStackTrace();
				}
				throw new WorkspaceException(MessageFormat.format("connection.xbs does not exist in folder {0}!", appDir.getAbsolutePath()));
			}

			// Datenbankdefinition einlesen
			prefs = Preferences.userRoot();
			try {
				InputStream prefsIS = new FileInputStream(connectionXbs);
				Preferences.importPreferences(prefsIS);
			} catch (IOException | InvalidPreferencesFormatException e) {
				e.printStackTrace();
			}
			prefs = prefs.node("aero.minova/connection");

			// Datenbanktreiber sicherheitshalber nochmals laden
			String driverClassname = prefs.get("driver", null);
			if (driverClassname != null) {
				try {
					Class.forName(driverClassname);
				} catch (ClassNotFoundException e) {
					throw new WorkspaceException("Driver class exception", e);
				}
			}

			// Verbindungeinstallung prüfen
			String connectionString = prefs.get("url", null);
			if (connectionString == null) {
				throw new WorkspaceException("No url defined");
			}
			setConnectionString(connectionString);

			// Verbindung mit Benutzername und Passwort versuchen
			Connection connection = null;
			try {
				if (username == null || username.length() == 0) {
					connection = DriverManager.getConnection(connectionString);
				} else {
					connection = DriverManager.getConnection(connectionString, username, password);
				}
			} catch (SQLException e) {
				throw new WorkspaceException(MessageFormat.format("No connection to server {0} for user {1} possible ", connectionString, username));
			}

			// Benutzername in der DB auslesen
			PreparedStatement statement;
			String sqlQuery = "select current_user username";
			try {
				statement = connection.prepareStatement(sqlQuery);
				ResultSet resultSet = statement.executeQuery();
//				if (resultSet.next()) {
//					setRemoteUsername(resultSet.getString("username"));
//				}
				statement.close();
				connection.close();
			} catch (SQLException e) {
				throw new WorkspaceException(MessageFormat.format("Error retrieving username please try statement ' {0}'", sqlQuery));
			}

			// Wir sind erfolgreich an der Datenabnk angemeldet
			return true;
		} catch (URISyntaxException e) {
			throw new WorkspaceException("Error connecting to application", e);
		}
	}

	@Override
	public String getConnectionString() {
		return connectionString;
	}

	private void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	@Override
	public void open() throws WorkspaceException {
		if (!Platform.getInstanceLocation().isSet()) {
			String defaultPath = System.getProperty("user.home");

			// build the desired path for the workspace
			String path = defaultPath + "/" + LifeCycle.DEFAULT_CONFIG_FOLDER + "/" + workspaceData.getWorkspaceHashHex() + "/";
			URL instanceLocationUrl;
			try {
				instanceLocationUrl = new URL("file", null, path);
				Platform.getInstanceLocation().set(instanceLocationUrl, false);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			URL workspaceURL = Platform.getInstanceLocation().getURL();
			File workspaceDir = new File(workspaceURL.getPath());
			if (!workspaceDir.exists()) {
				workspaceDir.mkdir();
				logger.info(MessageFormat.format("Workspace {0} neu angelegt.", workspaceDir));
			}
			checkDir(workspaceDir, "config");
			checkDir(workspaceDir, "data");
			checkDir(workspaceDir, "i18n");
			checkDir(workspaceDir, "plugins");
		}
	}
}
