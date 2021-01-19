package aero.minova.rcp.preferencewindow.control;

import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWIntegerText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class CustomPWIntegerText extends CustomPWText {

	
	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public CustomPWIntegerText(final String label, final String propertyKey) {
		super(label, propertyKey);
	}
	
	@Override
	public Control build(final Composite parent) {
		buildLabel(parent, GridData.CENTER);
		text = new Text(parent, SWT.BORDER | SWT.RIGHT |getStyle());
		addControl(text);
		addVerifyListeners();
		text.setText(PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey()).toString());
		text.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), convertValue());
		});
		return text;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#addVerifyListeners()
	 */
	@Override
	public void addVerifyListeners() {
		text.addListener(SWT.Verify, e -> {
			final String string = e.text;
			final char[] chars = new char[string.length()];
			string.getChars(0, chars.length, chars, 0);
			for (int i = 0; i < chars.length; i++) {
				if (!('0' <= chars[i] && chars[i] <= '9') && e.keyCode != SWT.BS && e.keyCode != SWT.DEL) {
					e.doit = false;
					return;
				}
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
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), Integer.valueOf(0));
		} else {
			if (!(value instanceof Integer)) {
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey() + "' has to be an Integer because it is associated to a integer text widget");
			}
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#convertValue()
	 */
	@Override
	public Object convertValue() {
		if(text.getText().equals("")) {
			return 0;
		}
		return Integer.parseInt(text.getText());
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#getStyle()
	 */
	@Override
	public int getStyle() {
		return SWT.NONE;
	}

}
