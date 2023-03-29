package aero.minova.rcp.preferencewindow.control;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.nebula.widgets.opal.commons.StringUtil;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;

public class CustomPWFloatText extends CustomPWText {

	/**
	 * Constructor
	 *
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated key
	 */
	public CustomPWFloatText(final String label, @Optional String tooltip, final String propertyKey) {
		super(label, tooltip, propertyKey);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#addVerifyListeners()
	 */
	@Override
	public void addVerifyListeners() {
		text.addListener(SWT.Verify, e -> {
			if (e.character != 0 && !Character.isDigit(e.character) && e.keyCode != SWT.BS && e.keyCode != SWT.DEL && e.character != '.'
					&& e.character != ',') {
				e.doit = false;
				return;
			}

			e.doit = verifyEntry(e.text, e.keyCode);
		});

	}

	/**
	 * Check if an entry is a float
	 *
	 * @param entry
	 *            text typed by the user
	 * @param keyCode
	 *            key code
	 * @return true if the user typed a float value, false otherwise
	 */
	private boolean verifyEntry(final String entry, final int keyCode) {
		final String work;
		if (keyCode == SWT.DEL) {
			work = StringUtil.removeCharAt(text.getText(), text.getCaretPosition());
		} else if (keyCode == SWT.BS && text.getCaretPosition() == 0) {
			work = StringUtil.removeCharAt(text.getText(), text.getCaretPosition() - 1);
		} else if (keyCode == 0) {
			work = entry;
		} else {
			work = StringUtil.insertString(text.getText(), entry, text.getCaretPosition());
		}

		try {
			Double.parseDouble(work.replace(',', '.'));
		} catch (final NumberFormatException nfe) {
			return false;
		}

		return true;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), Float.valueOf(0));
		} else {
			if (!(value instanceof Float)) {
				throw new UnsupportedOperationException(
						"The property '" + getCustomPropertyKey() + "' has to be a Float because it is associated to a float text widget");
			}
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#convertValue()
	 */
	@Override
	public Object convertValue() {
		if (text.getText().equals("")) {
			return 0.0;
		}
		return Float.parseFloat(text.getText());
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#getStyle()
	 */
	@Override
	public int getStyle() {
		return SWT.NONE;
	}

}
