package aero.minova.rcp.preferencewindow.control;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class CustomPWCheckbox extends CustomPWWidget {

	/**
	 * Constructor
	 *
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated key
	 */
	public CustomPWCheckbox(final String label, @Optional String tooltip, final String propertyKey) {
		super(label, tooltip, propertyKey, 2, false);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		
		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new GridLayout(2, false));
		GridData gd = new GridData();
		gd.horizontalIndent = 25;
		gd.horizontalSpan = 2;
		cmp.setLayoutData(gd);
		addControl(cmp);

		if (getLabel() == null) {
			throw new UnsupportedOperationException("Please specify a label for a checkbox");
		}
		final Button button = new Button(cmp, SWT.CHECK);
		button.setToolTipText(getTooltip());
		button.setText(getLabel());

		GridData buttonGD = new GridData();
		buttonGD.horizontalAlignment = GridData.FILL;
		button.setLayoutData(buttonGD);

		final boolean originalSelection = (Boolean) PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		button.setSelection(originalSelection);

		button.addListener(SWT.Selection, e -> {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), button.getSelection());
		});

		Label icon = new Label(cmp, SWT.NONE);
		if (getTooltip() != null) {
			createTooltipInfoIcon(icon);
		}

		return button;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), Boolean.valueOf(false));
		} else {
			if (!(value instanceof Boolean)) {
				throw new UnsupportedOperationException(
						"The property '" + getCustomPropertyKey() + "' has to be a Boolean because it is associated to a checkbox");
			}
		}
	}
}
