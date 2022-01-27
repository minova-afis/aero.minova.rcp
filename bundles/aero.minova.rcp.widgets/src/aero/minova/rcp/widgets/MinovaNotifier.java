package aero.minova.rcp.widgets;

import org.eclipse.jface.notifications.AbstractNotificationPopup;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
		Label create = LabelFactory.newLabel(SWT.WRAP).text(fText).create(parent);

		// Wenn der Text zu lang ist muss der widthHint gesetzt werden, damit der Text nicht abgeschnitten wird
		if (fText.length() >= 40) {
			GridData gd = new GridData();
			gd.widthHint = 400;
			create.setLayoutData(gd);
		}
	}
}
