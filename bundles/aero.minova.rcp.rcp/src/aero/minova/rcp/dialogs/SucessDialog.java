package aero.minova.rcp.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SucessDialog extends Dialog {

	private String dialog;

	public SucessDialog(Shell parentShell, String dialog) {
		super(parentShell);
		this.dialog = dialog;

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		Label dialogLabel = new Label(container, 1);
		dialogLabel.setText(dialog);
		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Sucess");
	}
	@Override
	protected Control createButtonBar(Composite parent) {
		return parent;

	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
