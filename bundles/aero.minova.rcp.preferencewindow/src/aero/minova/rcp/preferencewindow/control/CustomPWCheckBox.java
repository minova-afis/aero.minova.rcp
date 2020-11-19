package aero.minova.rcp.preferencewindow.control;

import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class CustomPWCheckBox extends CustomPWWidget {

	/**
	 * Constructor
	 *
	 * @param label       associated label
	 * @param propertyKey associated key
	 */
	public CustomPWCheckBox(final String label, final String propertyKey) {
		super(label, propertyKey, 1, true);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		if (getLabel() == null) {
			throw new UnsupportedOperationException("Please specify a label for a checkbox");
		}
		final Button button = new Button(parent, SWT.CHECK);
		addControl(button);
		final GridData buttonGridData = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
		buttonGridData.horizontalIndent = getIndent();
		buttonGridData.widthHint = 100;
		button.setText(getLabel());
		final boolean originalSelection = (Boolean) PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		button.setSelection(originalSelection);
		button.setLayoutData(buttonGridData);
		
		final Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		final GridData textGridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
		textGridData.horizontalIndent = getIndent();
		textGridData.widthHint = 150;
		text.setLayoutData(textGridData);

		button.addListener(SWT.Selection, e -> {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), button.getSelection());
			if (button.getSelection() == true) {
				text.setText("Funktioniert");
			} else {
				text.setText("");
			}
		});
		

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
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey()
						+ "' has to be a Boolean because it is associated to a checkbox");
			}
		}
	}

}
