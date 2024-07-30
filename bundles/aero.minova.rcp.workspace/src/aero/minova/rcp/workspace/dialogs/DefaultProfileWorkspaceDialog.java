package aero.minova.rcp.workspace.dialogs;

import static org.eclipse.jface.widgets.WidgetFactory.label;

import java.util.Properties;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.preferences.WorkspaceAccessPreferences;
import aero.minova.rcp.util.OSUtil;
import aero.minova.rcp.workspace.WorkspaceException;
import aero.minova.rcp.workspace.handler.WorkspaceHandler;

/**
 * Dialog für Default Profile, siehe #1579
 */
public class DefaultProfileWorkspaceDialog extends MinovaWorkspaceDialog {

	private Text username;
	private Text password;
	private Text applicationArea;
	private Text message;
	private Text connectionString;
	private Text profile;

	private String usernameText;
	private String pwText;
	private String connectionText;

	ILog logger = Platform.getLog(this.getClass());

	private IApplicationContext applicationContext;
	private Properties properties;

	public DefaultProfileWorkspaceDialog(Shell parentShell, IApplicationContext applicationContext, Properties properties) {
		super(parentShell);
		this.applicationContext = applicationContext;
		this.properties = properties;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(6, false));

		// Layout data für die Labels
		GridDataFactory labelGridData = GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER);

		/**
		 * PROFILE
		 */
		label(SWT.NONE).text("Profile").supplyLayoutData(labelGridData::create).create(container);
		profile = new Text(container, SWT.BORDER);
		profile.setLayoutData(GridDataFactory.fillDefaults().hint(400, SWT.DEFAULT).span(5, 1).create());
		profile.setText(properties.getProperty("ProfileName"));
		profile.setEnabled(false);

		/**
		 * USERNAME
		 */
		label(SWT.NONE).text("Username").supplyLayoutData(labelGridData::create).create(container);
		username = new Text(container, SWT.BORDER);
		username.setLayoutData(GridDataFactory.fillDefaults().hint(130, SWT.DEFAULT).span(2, 1).create());
		if (WorkspaceAccessPreferences.getWorkspaceAccessDataByName(properties.getProperty("ProfileName")).isPresent()) {
			try {
				username.setText(WorkspaceAccessPreferences.getWorkspaceAccessDataByName(properties.getProperty("ProfileName")).get()
						.get(WorkspaceAccessPreferences.USER, ""));
			} catch (StorageException e) {
				logger.error(e.getMessage(), e);
			}
		}

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
		connectionString.setText(properties.getProperty("ConnectionString"));
		connectionString.setEnabled(false);

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

		return container;
	}

	@Override
	protected void okPressed() {
		WorkspaceHandler workspaceHandler = null;
		try {
			message.setText("");
			workspaceHandler = WorkspaceHandler.newInstance(profile.getText(), connectionString.getText());
			workspaceHandler.checkConnection(username.getText(), password.getText(), applicationArea.getText(), false);
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

	@Override
	public void setDefaultConnectionString(String defaultConnectionString) {
		// Es gibt eh nur ein Profil
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
		return properties.getProperty("ProfileName");
	}
}