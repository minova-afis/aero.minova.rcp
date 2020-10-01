package aero.minova.rcp.dialogs;

import org.eclipse.jface.notifications.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

	public class NotificationPopUp extends AbstractNotificationPopup {

		private String fText;

		public NotificationPopUp(Display display, String text, Shell shell) {
			super(display);
			this.fText = text;
			setParentShell(shell);
			setDelayClose(200);
		}

		@Override
		protected String getPopupShellTitle() {
			return "Notification";
		}

		@Override
		protected void createContentArea(Composite parent) {
			Label label = new Label(parent, SWT.WRAP);
			label.setText(fText);
		}
	}
