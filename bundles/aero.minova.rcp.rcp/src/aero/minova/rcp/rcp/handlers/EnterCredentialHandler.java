package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.dialogs.PasswordDialog;

public class EnterCredentialHandler {
	
	@SuppressWarnings("unused")
	@Execute
	public void execute(Shell shell) {
		PasswordDialog dialog = new PasswordDialog(shell);
		
		if(dialog.open() == Window.OK ) {
			String user = dialog.getUser();
			String pw = dialog.getPassword();
		}
	}

}
