package aero.minova.rcp.preferencewindow.control;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ExplanationLabelForPWCheckbox extends CustomPWWidget {

	PreferenceWindow pwindow;

	TranslationService translationService;

	/**
	 * Constructor
	 *
	 * @param label       associated label
	 * @param propertyKey associated key
	 */
	public ExplanationLabelForPWCheckbox(final String label, final String propertyKey,
			TranslationService translationService) {
		super(label, propertyKey, 2, true);
		this.translationService = translationService;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {

		Label explanation = new Label(parent, SWT.NONE);
		explanation.setText(getLabel());
		GridData explanationGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		explanationGridData.horizontalSpan = 2;
		explanationGridData.horizontalIndent = getIndent();
		explanation.setLayoutData(explanationGridData);

		return explanation;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		// Methode wird nicht benötigt, da das Widget keinen Wert übergibt, der geprüft
		// werden muss. Die Methode wird von der Parent Klasse vererbt und kann nicht
		// gelöscht werden.

	}
}
