package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.parts.WFCDetailPart;

public class DeleteDetailHandler {

	@Inject
	IEventBroker broker;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.SHOW_DELETE_WARNING)
	boolean showDiscardDialogIndex;

	@Inject
	TranslationService translationService;

	@CanExecute
	public boolean canExecute(MPart part, @Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object selection) {
		if (part.getObject() instanceof WFCDetailPart) {
			MDetail detail = ((WFCDetailPart) part.getObject()).getDetail();
			return detail.getField("KeyLong").getValue() != null;
		}
		return false;
	}

	@Execute
	public void execute(@Optional MPerspective perspective) {
		if (perspective == null) {
			return;
		}

		MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), translationService.translate("@msg.DeleteWarningTitle", null), null,
				translationService.translate("@msg.DeleteWarningMessage", null), MessageDialog.WARNING,
				new String[] { translationService.translate("@Action.Delete", null), translationService.translate("@Abort", null) }, 0);

		if (!showDiscardDialogIndex || dialog.open() == 0) {
			broker.post(Constants.BROKER_DELETEENTRY, perspective);
		}
	}
}
