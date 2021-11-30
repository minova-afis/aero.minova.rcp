package aero.minova.rcp.preferencewindow.control;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class CustomPWChooser extends CustomPWWidget {

	TranslationService translationService;

	/**
	 * Constructor
	 *
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated key
	 */
	public CustomPWChooser(final String label, final String tooltip, final String propertyKey, @Optional TranslationService translationService) {
		super(label, tooltip, propertyKey, 3, false);
		this.translationService = translationService;
		setGrabExcessSpace(false);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(getLabel());
		label.setToolTipText(getTooltip());
		addControl(label);
		final GridData labelGridData = new GridData(SWT.END, SWT.CENTER, false, false);
		labelGridData.horizontalIndent = 25;
		label.setLayoutData(labelGridData);

		final Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		text.setToolTipText(getTooltip());
		addControl(text);
		final GridData textGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		text.setLayoutData(textGridData);

		final Button button = new Button(parent, SWT.PUSH);
		addControl(button);
		final GridData buttonGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		button.setText(translationService.translate("@Preferences.Choose", null) + "...");
		button.setLayoutData(buttonGridData);

		setButtonAction(text, button);

		return button;

	}

	/**
	 * Code executed when the user presses the button
	 *
	 * @param text
	 *            text box
	 * @param button
	 *            associated button
	 */
	protected abstract void setButtonAction(Text text, Button button);

}
