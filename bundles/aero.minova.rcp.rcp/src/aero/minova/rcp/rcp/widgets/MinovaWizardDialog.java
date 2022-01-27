package aero.minova.rcp.rcp.widgets;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class MinovaWizardDialog extends WizardDialog {

	private TranslationService translationService;

	public MinovaWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
		setTitleAreaColor(new RGB(236, 236, 236));
	}

	@Override
	public void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);

		if (translationService != null) {
			Button finishButton = getButton(IDialogConstants.FINISH_ID);
			finishButton.setText(translationService.translate("@Finish", null));

			Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
			cancelButton.setText(translationService.translate("@Abort", null));
		}
	}

	public void setTranslationService(TranslationService translationService) {
		this.translationService = translationService;
	}
}
