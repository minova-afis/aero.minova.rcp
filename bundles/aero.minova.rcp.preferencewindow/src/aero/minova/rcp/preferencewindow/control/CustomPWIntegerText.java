package aero.minova.rcp.preferencewindow.control;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CustomPWIntegerText extends CustomPWText {

	String completeLabel;

	/**
	 * Constructor
	 *
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated key
	 */
	public CustomPWIntegerText(final String label, @Optional String tooltip, final String propertyKey) {
		super(label.contains("[") ? label.substring(0, label.lastIndexOf("[") - 1) : label, tooltip, propertyKey);
		completeLabel = label;
	}

	@Override
	public Control build(final Composite parent) {

		buildLabel(parent, GridData.CENTER);

		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new GridLayout(3, false));
		addControl(cmp);

		text = new Text(cmp, SWT.BORDER | SWT.RIGHT | getStyle());
		final GridData textGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		textGridData.widthHint = 150;
		text.setLayoutData(textGridData);
		text.setToolTipText(getTooltip());
		addControl(text);

		addVerifyListeners();
		text.setText(PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey()).toString());
		text.addListener(SWT.Modify, event -> PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), convertValue()));

		// Wenn nötig Einheit hinzufügen
		if (completeLabel.contains("[")) {
			final Label unit = new Label(cmp, SWT.NONE);
			unit.setText(completeLabel.substring(completeLabel.lastIndexOf("[") + 1).replace("]", ""));
			final GridData unitGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
			unit.setLayoutData(unitGridData);
			addControl(unit);
		}

		Label icon = new Label(cmp, SWT.NONE);
		if (getTooltip() != null) {
			createTooltipInfoIcon(icon);
		}

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
				throw new UnsupportedOperationException(
						"The property '" + getCustomPropertyKey() + "' has to be an Integer because it is associated to a integer text widget");
			}
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#convertValue()
	 */
	@Override
	public Object convertValue() {
		if (text.getText().equals("")) {
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
