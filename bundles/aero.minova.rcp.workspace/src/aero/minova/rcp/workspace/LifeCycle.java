package aero.minova.rcp.workspace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.preferences.WorkspaceAccessPreferences;
import aero.minova.rcp.translate.lifecycle.Manager;
import aero.minova.rcp.workspace.dialogs.WorkspaceDialog;
import aero.minova.rcp.workspace.handler.WorkspaceHandler;

@SuppressWarnings("restriction")
public class LifeCycle {

	@Inject
	Logger logger;

	@Inject
	UISynchronize sync;

	@Inject
	IDataService dataService;

	@PostContextCreate
	void postContextCreate(IEclipseContext workbenchContext) throws IllegalStateException {
		URI workspaceLocation = null;

		// Bei -clearPersistedState müssen unsere Einstellungen auch gelöscht werden
		boolean deletePrefs = false;
		for (String string : Platform.getApplicationArgs()) {
			if (string.equals("-clearPersistedState")) {
				deletePrefs = true;
			}
		}

		// Versuchen über Commandline-Argumente einzuloggen, für UI-Tests genutzt
		boolean loginCommandLine = loginViaCommandLine(workbenchContext);

		// Ansonsten Default Profil oder manuelles Eingeben der Daten
		if (!loginCommandLine) {

			WorkspaceDialog workspaceDialog = new WorkspaceDialog(null, logger, sync);

			if (!WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).isEmpty()) {
				// Wenn Default-Workspace gesetzt ist diesen nutzen
				workspaceLocation = loginDefaultWorkspace(workspaceLocation, workspaceDialog);
			} else {
				// Ansonsten sofort Login-Dialog öffnen
				workspaceLocation = loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
			}

			// Das darf für UI-Tests nicht ausgeführt werden!
			checkModelVersion(workspaceLocation);
			if (deletePrefs) {
				deleteCustomPrefs(workspaceLocation);
			}
		}

		Manager manager = new Manager();
		manager.postContextCreate(workbenchContext);
	}

	/**
	 * Versuchen über Default-Daten einzuloggen. Bei Fehlschlag wird Login-Dialog geöffnet
	 * 
	 * @param workspaceLocation
	 * @param workspaceDialog
	 * @return
	 */
	private URI loginDefaultWorkspace(URI workspaceLocation, WorkspaceDialog workspaceDialog) {
		try {
			ISecurePreferences sPrefs = WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).get();
			if (!Platform.getInstanceLocation().isSet()) {
				Platform.getInstanceLocation().set(new URL(sPrefs.get(WorkspaceAccessPreferences.APPLICATION_AREA, null)), false);

				try {
					workspaceLocation = Platform.getInstanceLocation().getURL().toURI();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

				if (workspaceLocation == null) {
					WorkspaceAccessPreferences.resetDefaultWorkspace(logger);
					workspaceLocation = loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
				} else {
					workspaceLocation = checkDefaultWorkspace(workspaceLocation, workspaceDialog, sPrefs);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			workspaceLocation = loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
		}
		return workspaceLocation;
	}

	/**
	 * Überprüfen, ob die Default-Login-Daten gültig sind. Ansonsten Öffnen des Default-Workspaces
	 * 
	 * @param workspaceLocation
	 * @param workspaceDialog
	 * @param sPrefs
	 * @return
	 * @throws StorageException
	 */
	private URI checkDefaultWorkspace(URI workspaceLocation, WorkspaceDialog workspaceDialog, ISecurePreferences sPrefs) throws StorageException {
		String username = sPrefs.get(WorkspaceAccessPreferences.USER, null);
		String pw = sPrefs.get(WorkspaceAccessPreferences.PASSWORD, null);
		String url = sPrefs.get(WorkspaceAccessPreferences.URL, null);

		try {
			WorkspaceHandler workspaceHandler = WorkspaceHandler.newInstance(sPrefs.name(), url, logger);
			workspaceHandler.checkConnection(username, pw, workspaceLocation.toString(), true);
			workspaceHandler.open();

			dataService.setLogger(logger);
			dataService.setCredentials(username, pw, url, workspaceLocation);
		} catch (WorkspaceException e) {
			workspaceDialog = new WorkspaceDialog(null, logger, sync, sPrefs.name());
			workspaceLocation = loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
		}

		return workspaceLocation;

	}

	/**
	 * Versucht User, PW und URL aus commandline auszulesen. Wenn erfolgreich, werden diese genutzt und es öffnet sich kein Login-Dialog
	 * 
	 * @param workbenchContext
	 * @return
	 */
	private boolean loginViaCommandLine(IEclipseContext workbenchContext) {

		String argUser = null;// "admin";
		String argPW = null;// "rqgzxTf71EAx8chvchMi";
		String argURL = null;// "http://publictest.minova.com:17280/cas";

		// Auslesen der übergabenen ProgrammArgumente
		for (String string : Platform.getApplicationArgs()) {
			if (string.startsWith("-user=")) {
				argUser = string.substring(string.indexOf("=") + 1);
				// In UI-Tests darf sich der "UI wird wiederhergestellt" Dialog nicht öffnen
				workbenchContext.set(Constants.NEVER_SHOW_RESTORING_UI_MESSAGE, true);
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
				dataService.setLogger(logger);
				dataService.setCredentials(argUser, argPW, argURL, workspaceLocation);
				return true;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Vergleicht die ModelVersion mit der Datei die in der Application mitgeliefert wird. Ist diese Datei in der Workspacelocation nicht vorhanden, müssen wir
	 * in jedem Fall clearPersistedState aufrufen. Ist die Datei vorhanden aber hat eine zu alte Version gilt das Gleiche. Andernfalls machen nichts!
	 */
	private void checkModelVersion(URI workspaceLocation) {
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
				// neue Versionsnummer schreiben
				Files.writeString(resolve, modelVersionPlugin);
				Files.deleteIfExists(Path.of(workspaceLocation).resolve(".metadata/.plugins/org.eclipse.e4.workbench/workbench.xmi"));
				deleteCustomPrefs(workspaceLocation);
				showUserDialog();
			} catch (IOException e) {
				e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return "";
	}

	private void showUserDialog() {
		MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Reset Workspace",
				"Due to structural changes, the application area to be loaded is reset!");

	}

	private URI loadWorkspaceConfigManually(WorkspaceDialog workspaceDialog, URI workspaceLocation) {
		int returnCode;
		if ((returnCode = workspaceDialog.open()) != 0) {
			logger.info("ReturnCode: " + returnCode);
			System.exit(returnCode); // sollte nie aufgerufen werden, aber der Benutzer hat keinen Workspace
										// ausgesucht
		}
		try {
			workspaceLocation = Platform.getInstanceLocation().getURL().toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		Objects.requireNonNull(workspaceLocation);
		dataService.setLogger(logger);
		dataService.setCredentials(workspaceDialog.getUsername(), //
				workspaceDialog.getPassword(), //
				workspaceDialog.getConnection(), //
				workspaceLocation);

		return workspaceLocation;
	}
}