package aero.minova.rcp.workspace.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public abstract class MinovaWorkspaceDialog extends Dialog {

	protected MinovaWorkspaceDialog(Shell parentShell) {
		super(parentShell);
	}

	public abstract String getUsername();

	public abstract String getPassword();

	public abstract String getConnection();

	public abstract String getProfile();

	public abstract void setDefaultConnectionString(String defaultConnectionString);

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btnOK = createButton(parent, IDialogConstants.OPEN_ID, IDialogConstants.OPEN_LABEL, true);
		btnOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
		});
	}

}
