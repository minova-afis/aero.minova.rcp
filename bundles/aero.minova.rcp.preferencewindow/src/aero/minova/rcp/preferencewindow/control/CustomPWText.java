package aero.minova.rcp.preferencewindow.control;

import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public abstract class CustomPWText extends CustomPWWidget {
	protected Text text;

	/**
	 * Constructor
	 *
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated property key
	 */
	public CustomPWText(final String label, final String tooltip, final String propertyKey) {
		super(label, tooltip, propertyKey, label == null ? 1 : 2, false);
		setGrabExcessSpace(true);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		buildLabel(parent, GridData.CENTER);
		text = new Text(parent, SWT.BORDER | getStyle());
		addControl(text);
		addVerifyListeners();
		text.setText(PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey()).toString());
		text.setToolTipText(getTooltip());
		text.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), convertValue());
		});
		return text;
	}

	/**
	 * Add the verify listeners
	 */
	public abstract void addVerifyListeners();

	/**
	 * @return the value of the data typed by the user in the correct format
	 */
	public abstract Object convertValue();

	/**
	 * @return the style (SWT.NONE or SWT.PASSWORD)
	 */
	public abstract int getStyle();
}
