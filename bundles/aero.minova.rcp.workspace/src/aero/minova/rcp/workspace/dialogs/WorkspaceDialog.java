package aero.minova.rcp.workspace.dialogs;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.workspace.WorkspaceException;
import aero.minova.rcp.workspace.handler.WorkspaceHandler;

@SuppressWarnings("restriction")
public class WorkspaceDialog extends Dialog {

	private Text username;
	private Text password;
	private Text text;
	private Button btnOK;
	private Button btnConnect;
	private Text message;
	private Text connectionString;
	private Text remoteUsername;
	private Combo profile;

	private WorkspaceHandler workspaceHandler;
	private Logger logger;

	public WorkspaceDialog(Shell parentShell, Logger logger) {
		super(parentShell);
		this.logger = logger;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = new GridLayout(3, false);
		layout.marginRight = 5;
		layout.marginLeft = 10;
		container.setLayout(layout);

		// Layout data für die Labels
		GridDataFactory labelGridData = GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER);
		
		LabelFactory labelFactory = LabelFactory.newLabel(SWT.NONE).supplyLayoutData(labelGridData::create);
		
		labelFactory.text("Profile").create(container);

		profile = new Combo(container, SWT.NONE);
		profile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		profile.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = profile.getSelectionIndex();
				logger.info("Item " + i + " selected");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		new Label(container, SWT.NONE);

		labelFactory.text("Username").create(container);

		username = new Text(container, SWT.BORDER);
		username.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
// TODO
		// username.setText(workspaceData.getUsername());
		username.addModifyListener(e -> {
			Text textWidget = (Text) e.getSource();
			String userText = textWidget.getText();
			// TODO
			// workspaceData.setUsername(userText);
		});

		Label lblPassword = new Label(container, SWT.NONE);
		labelGridData.applyTo(lblPassword);
		lblPassword.setText("Password");

		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		// TODO
		// password.setText(workspaceData.getPassword());
		password.addModifyListener(e -> {
			Text textWidget = (Text) e.getSource();
			String passwordText = textWidget.getText();
			// TODO
			// workspaceData.setPassword(passwordText);
		});

		Label lblApplicationArea = new Label(container, SWT.NONE);
		labelGridData.applyTo(lblPassword);
		lblApplicationArea.setText("Application Area");

		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnNewButton = new Button(container, SWT.ARROW | SWT.DOWN);
		btnNewButton.setText("List Applications");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblMessage = new Label(container, SWT.NONE);
		labelGridData.applyTo(lblMessage);
		lblMessage.setText("Message");

		message = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
//		message.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		message.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 2));
		new Label(container, SWT.NONE);

		Label lblConnectionString = new Label(container, SWT.NONE);
		labelGridData.applyTo(lblConnectionString);
		lblConnectionString.setText("Connection String");

		connectionString = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		connectionString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);

		Label lblRemoteUsername = new Label(container, SWT.NONE);
		labelGridData.applyTo(lblRemoteUsername);
		lblRemoteUsername.setText("Remote Username");

		remoteUsername = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		remoteUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);

		updateProfiles();

		return container;
	}

	private void updateProfiles() {
		// TODO
		// workspaces = WorkspaceData.getWorkspaceData();
//		String profileNames[] = new String[workspaces.length];
//		for (int i = 0; i < workspaces.length; i++) {
//			profileNames[i] = workspaces[i].getDisplayName();
//		}
	}

	private void checkWorkspace() {
		workspaceHandler = null;
		try {
			message.setText("");
			workspaceHandler = WorkspaceHandler.newInstance(new URL(text.getText()), logger);
			btnOK.setEnabled(workspaceHandler.checkConnection(username.getText(), password.getText()));
		} catch (MalformedURLException | WorkspaceException e1) {
			logger.error(e1);
			message.setText(e1.getMessage());
			btnOK.setEnabled(false);
		}
		if (workspaceHandler != null) {
			connectionString.setText(workspaceHandler.getConnectionString());
			remoteUsername.setText(workspaceHandler.getRemoteUsername());
			profile.setText(workspaceHandler.getDisplayName());
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		text.setText("file:/Users/erlanger/Documents/MINOVA");
		password.setText("Minova+0");
		username.setText("sa");
		btnOK = createButton(parent, IDialogConstants.OPEN_ID, IDialogConstants.OPEN_LABEL, true);
		btnConnect = createButton(parent, IDialogConstants.RETRY_ID, "Check", false);
		btnOK.setEnabled(false);
		btnConnect.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				checkWorkspace();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		btnOK.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}

	@Override
	protected Point getInitialSize() {
		return new Point(900, 600);

	}

	@Override
	protected void okPressed() {
		// TODO
		// WorkspaceData wd = new WorkspaceData();
//		try {
//			wd.setConnection(new URL(text.getText()));
//		} catch (MalformedURLException e) {
//			// kann nicht kommen
//		}
//		wd.setProfile(workspaceHandler.getProfile());
//		wd.setUsername(username.getText());
//		wd.setPassword(password.getText());
//		workspaceHandler.open();
		super.okPressed();
	}

	public String getUsername() {
		// TODO
		// return workspaceData.getUsername();
		return "";
	}

	public void setUsername(String username) {
		// TODO
		// workspaceData.setUsername(username);
	}

	public String getPassword() {
		// TODO
		// return workspaceData.getPassword();
		return "";
	}

	public void setPassword(String password) {
		// TODO
		// workspaceData.setPassword(password);
	}

}
