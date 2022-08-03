package aero.minova.rcp.preferencewindow.control;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;

public class CustomPWPasswordText extends CustomPWStringText {
	/**
	 * Constructor
	 * 
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated key
	 */
	public CustomPWPasswordText(final String label, @Optional String tooltip, final String propertyKey) {
		super(label, tooltip, propertyKey);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#getStyle()
	 */
	@Override
	public int getStyle() {
		return SWT.PASSWORD;
	}
}
