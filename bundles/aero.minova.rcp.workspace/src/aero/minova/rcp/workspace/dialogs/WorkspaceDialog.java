package aero.minova.rcp.workspace.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

public class WorkspaceDialog extends Dialog {
	private Text txtPassword;
	private FileDialog fdApplicationArea;
	private String username = "";
	private String userpassword = "";
	private Button btnOK;
	private Button btnConnect;
	private Combo comboUser;

	public WorkspaceDialog(Shell parentShell) {
		super(parentShell);

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(3, false);
		layout.marginRight = 5;
		layout.marginLeft = 10;
		container.setLayout(layout);

		Label lblUser = new Label(container, SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUser.setText("Benutzername");
		
		comboUser = new Combo(container, SWT.NONE);
		comboUser.setItems(new String[] {"bauer", "postgres"});
		comboUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboUser.setText(username);
		comboUser.addModifyListener(e -> {
			Combo comboBox = (Combo) e.getSource();
			String userText = comboBox.getText();
			username = userText;
		});
		new Label(container, SWT.NONE);

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Passwort");

		txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPassword.setText(userpassword);
		txtPassword.addModifyListener(e -> {
			Text textWidget = (Text) e.getSource();
			String passwordText = textWidget.getText();
			userpassword = passwordText;
		});
		new Label(container, SWT.NONE);

		Label lblApplicationArea = new Label(container, SWT.NONE);
		lblApplicationArea.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblApplicationArea.setText("Anwendung");
		
		Combo combo = new Combo(container, SWT.NONE);
		combo.setItems(new String[] {"SIS", "AFIS", "TTA"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);

		
		
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		btnOK = createButton(parent, IDialogConstants.OK_ID, "Login", true);
		btnConnect = createButton(parent, IDialogConstants.OPEN_ID, "Connect", false);
		btnOK.setEnabled(false);
		btnConnect.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				DatabaseConnChecker checker = new DatabaseConnChecker();
				btnOK.setEnabled(checker.checkConnection(null, comboUser.getText(), txtPassword.getText()));
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);

	}

	@Override
	protected void okPressed() {
		username = comboUser.getText();
		userpassword = txtPassword.getText();
		super.okPressed();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return userpassword;
	}

	public void setPassword(String password) {
		this.userpassword = password;
	}

}
