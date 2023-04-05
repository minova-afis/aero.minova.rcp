package aero.minova.rcp.preferencewindow.control;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.dialog.Dialog;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;

public class CustomPWURLText extends CustomPWText {
	/**
	 * Constructor
	 *
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated key
	 */
	public CustomPWURLText(final String label, String tooltip, final String propertyKey) {
		super(label, tooltip, propertyKey);
		setWidth(200);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#addVerifyListeners()
	 */
	@Override
	public void addVerifyListeners() {
		text.addListener(SWT.FocusOut, event -> {
			try {
				new URL(CustomPWURLText.this.text.getText());
			} catch (final MalformedURLException e) {
				Dialog.error(ResourceManager.getLabel(ResourceManager.APPLICATION_ERROR), ResourceManager.getLabel(ResourceManager.VALID_URL));
				event.doit = false;
				CustomPWURLText.this.text.forceFocus();
			}
		});

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
				throw new UnsupportedOperationException(
						"The property '" + getCustomPropertyKey() + "' has to be a String because it is associated to a URL text box");
			}

			final String str = (String) value;
			if (str.equals("")) {
				PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), "");
				return;
			}

			try {
				new URL(str);
			} catch (final MalformedURLException e) {
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey() + "' has a value (" + value + ") that is not an URL");
			}
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#convertValue()
	 */
	@Override
	public Object convertValue() {
		return text.getText();
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#getStyle()
	 */
	@Override
	public int getStyle() {
		return SWT.NONE;
	}

}
