package aero.minova.rcp.preferencewindow.control;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
		super(label, tooltip, propertyKey, 2, false);
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

		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new GridLayout(3, false));
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		cmp.setLayoutData(gd);
		addControl(cmp);

		final Text text = new Text(cmp, SWT.BORDER | SWT.READ_ONLY);
		text.setToolTipText(getTooltip());
		addControl(text);
		final GridData textGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		text.setLayoutData(textGridData);

		final Button button = new Button(cmp, SWT.PUSH);
		addControl(button);
		button.setText(translationService.translate("@Preferences.Choose", null) + "...");

		setButtonAction(text, button);

		Label icon = new Label(cmp, SWT.NONE);
		if (getTooltip() != null) {
			createTooltipInfoIcon(icon);
		}

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
