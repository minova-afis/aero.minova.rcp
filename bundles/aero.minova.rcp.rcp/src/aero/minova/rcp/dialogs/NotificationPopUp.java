package aero.minova.rcp.dialogs;

import org.eclipse.jface.notifications.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class NotificationPopUp extends AbstractNotificationPopup {

	private String fText;
	private String title;

	public NotificationPopUp(Display display, String text, String title, Shell shell) {
		super(display);
		this.fText = text;
		setParentShell(shell);
		// TODO: Einstellung in die Preferences übernehmen?
		setDelayClose(2000);
		this.title = title;
	}

	@Override
	protected String getPopupShellTitle() {
		return title;
	}

	@Override
	protected void createContentArea(Composite parent) {
		Label label = new Label(parent, SWT.WRAP);
		label.setText(fText);
	}
}
