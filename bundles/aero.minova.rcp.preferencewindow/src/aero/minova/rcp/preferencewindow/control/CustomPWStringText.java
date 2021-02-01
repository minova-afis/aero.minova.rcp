package aero.minova.rcp.preferencewindow.control;

import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWStringText;
import org.eclipse.swt.SWT;

public class CustomPWStringText extends CustomPWText {

	/**
	 * Constructor
	 * 
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public CustomPWStringText(final String label, final String propertyKey) {
		super(label, propertyKey);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#addVerifyListeners()
	 */
	@Override
	public void addVerifyListeners() {
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), "");
		} else {
			if (!(value instanceof String)) {
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey() + "' has to be a String because it is associated to a stringtext");
			}
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#convertValue()
	 */
	@Override
	public Object convertValue() {
		if(text.getText().equals(null)) {
			return "";
		}
		return this.text.getText();
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#getStyle()
	 */
	@Override
	public int getStyle() {
		return SWT.NONE;
	}

}
