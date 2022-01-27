package aero.minova.rcp.preferencewindow.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CustomPWButton extends CustomPWWidget {
	private final SelectionListener listener;

	/**
	 * Constructor
	 * 
	 * @param label
	 *            associated label
	 * @param listener
	 *            selection listener
	 */
	public CustomPWButton(final String label, final SelectionListener listener) {
		super(label, null, null, 1, true);
		this.listener = listener;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		final Button button = new Button(parent, SWT.PUSH);

		addControl(button);

		if (getLabel() == null) {
			throw new UnsupportedOperationException("You need to set a label for a button");
		} else {
			button.setText(getLabel());
		}
		if (this.listener != null) {
			button.addSelectionListener(this.listener);
		}

		return button;

	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {}
}
