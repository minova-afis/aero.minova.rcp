package aero.minova.rcp.workspace.dialogs;

import static org.eclipse.jface.widgets.WidgetFactory.label;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.preferences.WorkspaceAccessPreferences;
import aero.minova.rcp.util.OSUtil;
import aero.minova.rcp.workspace.WorkspaceException;
import aero.minova.rcp.workspace.handler.WorkspaceHandler;

public class WorkspaceDialog extends MinovaWorkspaceDialog {

	private Text username;
	private Text password;
	private Text applicationArea;
	private Button btnDefault;
	private Text message;
	private Text connectionString;
	private Combo profile;
	private String profileName;

	private String usernameText;
	private String pwText;
	private String connectionText;

	ILog logger = Platform.getLog(this.getClass());

	private boolean loadedProfile = false;
	private String defaultConnectionString;
	private IApplicationContext applicationContext;

	public WorkspaceDialog(Shell parentShell, IApplicationContext applicationContext) {
		super(parentShell);
		this.applicationContext = applicationContext;
	}

	public WorkspaceDialog(Shell parentShell, IApplicationContext applicationContext, String profileName) {
		this(parentShell, applicationContext);
		this.profileName = profileName;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(6, false));

		// Layout data für die Labels
		GridDataFactory labelGridData = GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER);

		label(SWT.NONE).text("Profile").supplyLayoutData(labelGridData::create).create(container);
		profile = new Combo(container, SWT.NONE);
		profile.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false, 3, 1));
		fillProfiles();
		profile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				username.setText("");
				loadedProfile = true;
				password.setText("xxxxxxxxxxxxxxxxxxxx");
				connectionString.setText("");
				applicationArea.setText("");
				loadProfileData();
			}
		});
		profile.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				int i = profile.getSelectionIndex();
				try {
					if (profile.getText().isEmpty() || !loadedProfile || i == -1) {
						password.setText("");
					} else {
						username.setText("");
						password.setText("xxxxxxxxxxxxxxxxxxxx");
						connectionString.setText("");
						applicationArea.setText("");
					}
					loadProfileData();
				} catch (NullPointerException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		});

		btnDefault = new Button(container, SWT.CHECK);
		btnDefault.setText("Default");
		btnDefault.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).hint(75, SWT.DEFAULT).create());

		CLabel deleteProfile = new CLabel(container, SWT.CENTER | SWT.VERTICAL);
		deleteProfile.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.FILL).grab(false, true).hint(25, SWT.DEFAULT).create());
		deleteProfile.setText("\uD83D\uDDD1\uFE0F");
		deleteProfile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

				String profileNameToDelete = profile.getText();
				logger.info("Deleting Profile \"" + profileNameToDelete + "\"");
				if (profileNameToDelete == null || profileNameToDelete.isBlank()) {
					logger.info("No profile selected, not deleting");
					return;
				}

				WorkspaceAccessPreferences.deleteSavedWorkspace(profileNameToDelete);
				try {
					// Workspace Ordner löschen
					FileUtils.deleteDirectory(new File(applicationArea.getText().replace("file:", "")));
				} catch (IOException e1) {
					logger.error(e1.getMessage(), e1);
				}
				profile.clearSelection();
				profile.removeAll();
				loadedProfile = false;
				fillProfiles();
				deleteDialogEntries();
			}
		});

		/**
		 * USERNAME
		 */
		label(SWT.NONE).text("Username").supplyLayoutData(labelGridData::create).create(container);
		username = new Text(container, SWT.BORDER);
		username.setLayoutData(GridDataFactory.fillDefaults().hint(130, SWT.DEFAULT).span(2, 1).create());

		/**
		 * PASSWORD
		 */
		label(SWT.NONE).text("Password").supplyLayoutData(labelGridData::create).create(container);
		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(GridDataFactory.fillDefaults().hint(130, SWT.DEFAULT).span(2, 1).create());

		/**
		 * CONNECTION STRING
		 */
		label(SWT.NONE).text("Connection String").layoutData(new GridData(SWT.FILL, SWT.RIGHT, false, false, 1, 1)).create(container);
		connectionString = new Text(container, SWT.BORDER);
		connectionString.setLayoutData(GridDataFactory.fillDefaults().hint(400, SWT.DEFAULT).span(5, 1).create());

		/**
		 * APPLICATION AREA
		 */
		label(SWT.NONE).text("Application Area").layoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1)).create(container);
		applicationArea = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		applicationArea.setLayoutData(GridDataFactory.fillDefaults().hint(365, SWT.DEFAULT).span(5, 1).create());
		applicationArea.setEnabled(false);

		/**
		 * MESSAGE
		 */
		label(SWT.NONE).text("Message").supplyLayoutData(labelGridData::create).create(container);
		message = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		message.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(365, SWT.DEFAULT).span(5, 2).create());
		message.setEnabled(false);
		// Wird benötigt, damit 2 Zeilen für das Message-Feld angezeigt werden.
		new Label(container, SWT.NONE);

		// initialisieren --> alle Felder leer setzen
		deleteDialogEntries();

		// Wenn profileName gegeben wurde dieses laden
		setProfile(profileName);
		loadProfileData();

		// Wenn noch keine Profile gegeben sind, Connection-String aus settings.properties nutzen
		if (profile.getItemCount() == 0 && defaultConnectionString != null) {
			connectionString.setText(defaultConnectionString);
		}

		return container;
	}

	/**
	 * Leert die Textfelder aus dem Dialog
	 */
	private void deleteDialogEntries() {
		username.setText("");
		password.setText("");
		connectionString.setText("");
		applicationArea.setText("");
		message.setText("");
		loadProfileData();
	}

	/**
	 * Füllt die ComboBox mit den vorhandenen Profilen in alphabetischer Reihenfolge
	 */
	private void fillProfiles() {
		List<String> prefNames = WorkspaceAccessPreferences.getSavedWorkspaceAccessData().stream().map(pref -> {
			try {
				return pref.get(WorkspaceAccessPreferences.PROFILE, "");
			} catch (StorageException e) {
				logger.error(e.getMessage(), e);
				return "";
			}
		}).toList();

		prefNames.stream().sorted(Comparator.comparing(String::toString, String.CASE_INSENSITIVE_ORDER)).forEach(s -> profile.add(s));
	}

	/**
	 * Prüft die eingetragenen Daten. Wenn das Profil vorhanden ist, werden die Dialog-Felder entsprechend der gespeicherten Werte gesetzt. Passwort wird nicht
	 * gesetzt!
	 */
	private void loadProfileData() {
		try {
			WorkspaceHandler workspaceHandler = WorkspaceHandler.newInstance(profile.getText(), connectionString.getText());
			if (workspaceHandler != null) {
				workspaceHandler.checkConnection(username.getText(), password.getText(), applicationArea.getText(), btnDefault.getSelection());
				profile.setText(workspaceHandler.getProfile());
				username.setText(workspaceHandler.getUsername());
				password.setText(workspaceHandler.getPassword());
				applicationArea.setText(workspaceHandler.getApplicationArea());
				connectionString.setText(workspaceHandler.getConnectionString());
				message.setText(workspaceHandler.getMessage());
			}
		} catch (WorkspaceException e1) {
			logger.error(e1.getMessage(), e1);
			message.setText(e1.getMessage());
		}

	}

	@Override
	protected void okPressed() {
		WorkspaceHandler workspaceHandler = null;
		try {
			message.setText("");
			workspaceHandler = WorkspaceHandler.newInstance(profile.getText(), connectionString.getText());
			workspaceHandler.checkConnection(username.getText(), password.getText(), applicationArea.getText(), btnDefault.getSelection());
			workspaceHandler.open();
		} catch (WorkspaceException e) {
			message.setText(e.getMessage());
			return;
		} catch (NullPointerException ex) {
			message.setText(ex.getMessage());
			return;
		}
		usernameText = username.getText();
		pwText = workspaceHandler.getPassword();
		connectionText = connectionString.getText();
		super.okPressed();
	}

	@Override
	public String getUsername() {
		return usernameText;
	}

	@Override
	public String getPassword() {
		return pwText;
	}

	@Override
	public String getConnection() {
		return connectionText;
	}

	private void setProfile(String profileName) {
		for (int i = 0; i < profile.getItemCount(); i++) {
			if (profile.getItem(i).equals(profileName)) {
				profile.select(i);
				break;
			}
		}
	}

	@Override
	public void setDefaultConnectionString(String defaultConnectionString) {
		this.defaultConnectionString = defaultConnectionString;
	}

	@Override
	public int open() {
		if (OSUtil.isLinux()) {
			// Unter Linux den Splash-Screen sofort schließen, siehe #1487
			applicationContext.applicationRunning();
		}
		return super.open();
	}

	@Override
	public String getProfile() {
		return profileName;
	}

}