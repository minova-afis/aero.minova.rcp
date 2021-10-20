package aero.minova.rcp.rcp.gridvalidation;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.nattable.edit.config.DialogErrorHandling;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.swt.widgets.Display;

public class CrossValidationDialogErrorHandling extends DialogErrorHandling {

	private TranslationService translationService;

	public CrossValidationDialogErrorHandling(boolean allowCommit, TranslationService translationService) {
		super(allowCommit);
		this.translationService = translationService;

	}

	@Override
	public void showWarningDialog(String dialogMessage, String dialogTitle) {
		if (!isWarningDialogActive() && dialogMessage != null) {
			MessageDialog warningDialog = new MessageDialog(Display.getCurrent().getActiveShell(), dialogTitle, null,
					translationService.translate(dialogMessage, null), MessageDialog.WARNING,
					new String[] { getChangeButtonLabel(), getDiscardButtonLabel(), "Commit" }, 0);

			// if discard was selected close the editor
			int returnCode = warningDialog.open();
			if (returnCode == 1) {
				this.editor.close();
			} else if (returnCode == 2) {
				this.editor.commit(MoveDirectionEnum.NONE, true, true);
			}
		}
	}
}