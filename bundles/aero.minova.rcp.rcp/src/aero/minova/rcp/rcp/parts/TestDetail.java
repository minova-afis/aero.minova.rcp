package aero.minova.rcp.rcp.parts;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;

public class TestDetail extends Composite {
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TestDetail(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Label lblNewlabel = new Label(this, SWT.NONE);
		formToolkit.adapt(lblNewlabel, true, true);
		lblNewlabel.setText("NewLabel");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
