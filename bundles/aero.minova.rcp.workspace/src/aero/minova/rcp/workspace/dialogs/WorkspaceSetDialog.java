package aero.minova.rcp.workspace.dialogs;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

public class WorkspaceSetDialog extends MessageDialog {

	ILog logger = Platform.getLog(this.getClass());

	public WorkspaceSetDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, int defaultIndex,
			String[] dialogButtonLabels) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, defaultIndex, dialogButtonLabels);
	}

	@Override
	protected Control createCustomArea(Composite parent) {
		Link link = new Link(parent, SWT.WRAP);
		link.setText("See also RCP Issue <a>#1152</a>.");

		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
						String uri = "https://github.com/minova-afis/aero.minova.rcp/issues/1152";
						Desktop.getDesktop().browse(new URI(uri));
					}
				} catch (IOException | URISyntaxException e1) {
					logger.error("Error opening Link", e1);
				}
			}
		});

		return link;
	}
}
