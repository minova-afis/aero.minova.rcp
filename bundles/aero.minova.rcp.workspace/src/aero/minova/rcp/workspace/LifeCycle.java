package aero.minova.rcp.workspace;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.ImageUtil;
import aero.minova.rcp.preferences.WorkspaceAccessPreferences;
import aero.minova.rcp.translate.lifecycle.Manager;
import aero.minova.rcp.util.OSUtil;
import aero.minova.rcp.workspace.dialogs.DefaultProfileWorkspaceDialog;
import aero.minova.rcp.workspace.dialogs.MinovaWorkspaceDialog;
import aero.minova.rcp.workspace.dialogs.WorkspaceDialog;
import aero.minova.rcp.workspace.dialogs.WorkspaceSetDialog;
import aero.minova.rcp.workspace.handler.WorkspaceHandler;

public class LifeCycle {

	private static final String FREE_TABLES_APPLICATION = "FreeTables.Application";
	public static final String DEFAULT_CONFIG_FOLDER = ".minwfc";

	ILog logger = Platform.getLog(this.getClass());

	@Inject
	UISynchronize sync;

	@Inject
	IDataService dataService;

	@Inject
	IApplicationContext applicationContext;

	String defaultConnectionString;

	@PostContextCreate
	void postContextCreate(IEclipseContext workbenchContext) throws IllegalStateException, IOException {

		URI workspaceLocation = null;

		// Bei -clearPersistedState müssen unsere Einstellungen auch gelöscht werden
		boolean deletePrefs = false;
		for (String string : Platform.getApplicationArgs()) {
			if (string.equals("-clearPersistedState")) {
				deletePrefs = true;
			}
		}

		// settings.properties einlesen wenn vorhanden und im Context ablegen
		final Path settingsPath = Paths.get(System.getProperty("user.home")).resolve(LifeCycle.DEFAULT_CONFIG_FOLDER).resolve(Constants.SETTINGS_FILE_NAME);
		Properties settings = new Properties();
		if (settingsPath.toFile().exists()) {
			try (BufferedInputStream targetStream = new BufferedInputStream(new FileInputStream(settingsPath.toFile()))) {
				settings.load(targetStream);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		workbenchContext.set(Constants.SETTINGS_PROPERTIES, settings);
		defaultConnectionString = settings.getProperty(Constants.SETTINGS_DEFAULT_CONNECTION_STRING);

		ImageDescriptor imageDefault = ImageUtil.getImageDescriptor(FREE_TABLES_APPLICATION, 16);
		ImageDescriptor imageDefault2 = ImageUtil.getImageDescriptor(FREE_TABLES_APPLICATION, 32);
		ImageDescriptor imageDefault3 = ImageUtil.getImageDescriptor(FREE_TABLES_APPLICATION, 64);
		ImageDescriptor imageDefault4 = ImageUtil.getImageDescriptor(FREE_TABLES_APPLICATION, 256);
		Window.setDefaultImages(
				new Image[] { imageDefault.createImage(), imageDefault2.createImage(), imageDefault3.createImage(), imageDefault4.createImage() });

		// Versuchen über Commandline-Argumente einzuloggen, für UI-Tests genutzt
		boolean loginCommandLine = loginViaCommandLine();

		// Ist in der Debug-Konfiguration eine "Location" gesetzt (und handelt es sich nicht um Tests) nicht starten, siehe #1152
		if (Platform.getInstanceLocation().isSet() && !loginCommandLine) {
			String message = "It seems like you have set a location in your debug configuration. Please remove it so the application can work properly.";
			logger.error(message);
			WorkspaceSetDialog wst = new WorkspaceSetDialog(null, "Workspace Location is set", null, message, MessageDialog.ERROR, 0,
					new String[] { IDialogConstants.OK_LABEL });
			wst.open();
			System.exit(1);
		}

		// Ansonsten Default Profil oder manuelles Eingeben der Daten
		if (!loginCommandLine) {

			MinovaWorkspaceDialog workspaceDialog = getWorkspaceDialog(null);
			workspaceDialog.setDefaultConnectionString(defaultConnectionString);

			if (workspaceDialog instanceof DefaultProfileWorkspaceDialog && //
					!WorkspaceAccessPreferences.getWorkspaceAccessDataByName(workspaceDialog.getProfile()).isEmpty()) {
				// Wenn Default-Profil passt dieses verwenden
				workspaceLocation = loginWorkspaceFromPrefs(workspaceLocation, workspaceDialog,
						WorkspaceAccessPreferences.getWorkspaceAccessDataByName(workspaceDialog.getProfile()).get());
			} else if (!WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData().isEmpty()) {
				// Wenn Default-Workspace gesetzt ist diesen nutzen
				workspaceLocation = loginWorkspaceFromPrefs(workspaceLocation, workspaceDialog,
						WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData().get());
			} else {
				// Ansonsten sofort Login-Dialog öffnen
				workspaceLocation = loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
			}

			// Das darf für UI-Tests nicht ausgeführt werden!
			checkResetUI(workspaceLocation);
			checkModelVersion(workspaceLocation, workbenchContext);
			if (deletePrefs) {
				deleteCustomPrefs(workspaceLocation);
			}
		}

		Manager manager = new Manager();
		manager.postContextCreate(workbenchContext);
	}

	private MinovaWorkspaceDialog getWorkspaceDialog(String profileName) {

		// Wenn es die Datei "DefaultProfile.properties" gibt entsprechenden Dialog öffnen, siehe #1579
		try {
			File propertiesFile = new File(OSUtil.isMac() ? "../../../DefaultProfile.properties" : "../DefaultProfile.properties");
			if (propertiesFile.exists()) {
				Properties properties = new Properties();
				try (BufferedInputStream targetStream = new BufferedInputStream(new FileInputStream(propertiesFile))) {
					properties.load(targetStream);
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
				return new DefaultProfileWorkspaceDialog(null, applicationContext, properties);
			}
		} catch (Exception e) {
			logger.error("Error finding DefaultProfile.properties file", e);
		}

		// Ansonsten den "normalen" Dialog
		return new WorkspaceDialog(null, applicationContext, profileName);
	}

	/**
	 * Versuchen über Default-Daten einzuloggen. Bei Fehlschlag wird Login-Dialog geöffnet
	 *
	 * @param workspaceLocation
	 * @param workspaceDialog
	 * @return
	 */
	private URI loginWorkspaceFromPrefs(URI workspaceLocation, MinovaWorkspaceDialog workspaceDialog, ISecurePreferences sPrefs) {
		try {
			if (!Platform.getInstanceLocation().isSet()) {
				Platform.getInstanceLocation().set(new URL(sPrefs.get(WorkspaceAccessPreferences.APPLICATION_AREA, null)), false);

				workspaceLocation = parseURI(workspaceLocation);

				if (workspaceLocation == null) {
					WorkspaceAccessPreferences.resetDefaultWorkspace();
					workspaceLocation = loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
				} else {
					workspaceLocation = checkWorkspace(workspaceLocation, sPrefs);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			workspaceLocation = loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
		}
		return workspaceLocation;
	}

	private URI parseURI(URI workspaceLocation) {
		try {
			workspaceLocation = Platform.getInstanceLocation().getURL().toURI();
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}
		return workspaceLocation;
	}

	/**
	 * Überprüfen, ob die gewählten Login-Daten gültig sind. Ansonsten Login-Dialog öffnen
	 * 
	 * @param workspaceLocation
	 * @param workspaceDialog
	 * @param sPrefs
	 * @return
	 * @throws StorageException
	 */
	private URI checkWorkspace(URI workspaceLocation, ISecurePreferences sPrefs) throws StorageException {
		String username = sPrefs.get(WorkspaceAccessPreferences.USER, null);
		String pw = sPrefs.get(WorkspaceAccessPreferences.PASSWORD, null);
		String url = sPrefs.get(WorkspaceAccessPreferences.URL, null);

		try {
			WorkspaceHandler workspaceHandler = WorkspaceHandler.newInstance(sPrefs.name(), url);
			workspaceHandler.checkConnection(username, pw, workspaceLocation.toString(),
					sPrefs.getBoolean(WorkspaceAccessPreferences.IS_PRIMARY_WORKSPACE, false));
			workspaceHandler.open();

			dataService.setCredentials(username, pw, url, workspaceLocation);
		} catch (WorkspaceException e) {
			MinovaWorkspaceDialog defaultDialog = getWorkspaceDialog(sPrefs.name());
			defaultDialog.setDefaultConnectionString(defaultConnectionString);
			workspaceLocation = loadWorkspaceConfigManually(defaultDialog, workspaceLocation);
		}

		return workspaceLocation;
	}

	/**
	 * Versucht User, PW und URL aus commandline auszulesen. Wenn erfolgreich, werden diese genutzt und es öffnet sich kein Login-Dialog
	 * 
	 * @param workbenchContext
	 * @return
	 */
	private boolean loginViaCommandLine() {

		String argUser = null;// "admin";
		String argPW = null;// "rqgzxTf71EAx8chvchMi";
		String argURL = null;// "https://publictest.minova.com/cas/";

		// Auslesen der übergebenen Programm Argumente
		for (String string : Platform.getApplicationArgs()) {
			if (string.startsWith("-user=")) {
				argUser = string.substring(string.indexOf("=") + 1);
			}
			if (string.startsWith("-pw=")) {
				argPW = string.substring(string.indexOf("=") + 1);
			}
			if (string.startsWith("-url=")) {
				argURL = string.substring(string.indexOf("=") + 1);
			}
		}

		if (argPW != null && argURL != null && argUser != null) {
			try {
				URI workspaceLocation = Platform.getInstanceLocation().getURL().toURI();
				dataService.setCredentials(argUser, argPW, argURL, workspaceLocation);
				return true;
			} catch (URISyntaxException e) {
				logger.error(e.getMessage(), e);
			}
		}

		return false;
	}

	/**
	 * Vergleicht die ModelVersion mit der Datei die in der Application mitgeliefert wird. Ist diese Datei in der Workspacelocation nicht vorhanden, müssen wir
	 * in jedem Fall clearPersistedState aufrufen. Ist die Datei vorhanden aber hat eine zu alte Version gilt das Gleiche. Andernfalls machen nichts!
	 */
	private void checkModelVersion(URI workspaceLocation, IEclipseContext workbenchContext) {
		// lese WorkSpaceFile aus der WorkSpaceLocation
		String readString = null;
		Path resolve = Path.of(workspaceLocation).resolve("ModelVersion.txt");
		String modelVersionPlugin = checkModelVersionFromPlugin();

		try {
			readString = Files.readString(resolve);
		} catch (IOException e) {
			// es gibt keins oder kann nicht gelesen werden!
		}

		if (!modelVersionPlugin.equals(readString)) {
			try {
				Files.deleteIfExists(resolve);
				Files.createFile(resolve);
				Files.writeString(resolve, modelVersionPlugin);

				// Wenn das ModelVersion.txt file nicht existiert ist es ein neuer Workspace ->
				// readString == null -> Meldung nicht anzeigen und workspace nicht
				// löschen
				if (readString != null) {
					Files.deleteIfExists(Path.of(workspaceLocation).resolve(".metadata/.plugins/org.eclipse.e4.workbench/workbench.xmi"));
					deleteCustomPrefs(workspaceLocation);
					workbenchContext.set(Constants.SHOW_WORKSPACE_RESET_MESSAGE, true);
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Wenn die UI zurückgesetzt wird, soll die workbench.xmi Datei gelöscht werden. Die anderen Einstellungen sollen erhalten bleiben, siehe #1371
	 * 
	 * @param workspaceLocation
	 */
	private void checkResetUI(URI workspaceLocation) {
		Path resolve = Path.of(workspaceLocation).resolve(Constants.RESET_UI_FILE_NAME);
		if (Files.exists(resolve)) {
			try {
				Files.delete(resolve);
				Files.deleteIfExists(Path.of(workspaceLocation).resolve(".metadata/.plugins/org.eclipse.e4.workbench/workbench.xmi"));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Wir löschen auch die Einstellungen, die für das persistieren der angehefteten Toolbars zuständig sind, da es sonst bei -clearPersistedState und einer
	 * Änderung der ModelVersion Probleme gibt (Siehe Issue #703)
	 *
	 * @param workspaceLocation
	 */
	private void deleteCustomPrefs(URI workspaceLocation) {
		try {
			Files.deleteIfExists(Path.of(workspaceLocation)
					.resolve(".metadata/.plugins/org.eclipse.core.runtime/.settings/aero.minova.rcp.preferences.keptperspectives.prefs"));
			Files.deleteIfExists(
					Path.of(workspaceLocation).resolve(".metadata/.plugins/org.eclipse.core.runtime/.settings/aero.minova.rcp.preferences.toolbarorder.prefs"));
			Files.deleteIfExists(Path.of(workspaceLocation)
					.resolve(".metadata/.plugins/org.eclipse.core.runtime/.settings/aero.minova.rcp.preferences.detailsections.prefs"));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Lädt aus dem aero.minova.rcp.workspaceplugin den Inhalt aus der ModelVersion.txt. Diese stellt die aktuelle Modelversion bereit.
	 *
	 * @return
	 */
	private String checkModelVersionFromPlugin() {
		final Bundle bundle = FrameworkUtil.getBundle(LifeCycle.class);
		final URL url = FileLocator.find(bundle, new org.eclipse.core.runtime.Path("ModelVersion.txt"), null);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
			String readOut = null;
			readOut = in.readLine();
			return readOut;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * Öffnet den Workspace Dialog und initialisiert nach Klick auf OK den DataService mit den eingegebenen Daten
	 * 
	 * @param workspaceDialog
	 * @param workspaceLocation
	 * @return
	 */
	private URI loadWorkspaceConfigManually(MinovaWorkspaceDialog workspaceDialog, URI workspaceLocation) {
		int returnCode;
		if ((returnCode = workspaceDialog.open()) != 0) {
			logger.info("ReturnCode: " + returnCode);
			System.exit(returnCode);
		}

		try {
			workspaceLocation = Platform.getInstanceLocation().getURL().toURI();
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}

		Objects.requireNonNull(workspaceLocation);
		dataService.setCredentials(workspaceDialog.getUsername(), //
				workspaceDialog.getPassword(), //
				workspaceDialog.getConnection(), //
				workspaceLocation);

		return workspaceLocation;
	}
}