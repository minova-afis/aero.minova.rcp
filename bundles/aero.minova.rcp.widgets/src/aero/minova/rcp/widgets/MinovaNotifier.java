package aero.minova.rcp.widgets;

import org.eclipse.jface.notifications.AbstractNotificationPopup;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MinovaNotifier extends AbstractNotificationPopup {

	private String fText;
	private String title;

	private MinovaNotifier(Display display, String text, String title, Shell shell) {
		super(display);
		this.fText = text;
		setParentShell(shell);
		setDelayClose(2000);
		this.title = title;
	}


	public static void show(Shell forShell, String text, String title) {
		new MinovaNotifier(forShell.getDisplay(), text, title, forShell).open();
	}

	@Override
	protected String getPopupShellTitle() {
		return title;
	}

	@Override
	protected void createContentArea(Composite parent) {
		LabelFactory.newLabel(SWT.WRAP).text(fText).create(parent);
	}
}
