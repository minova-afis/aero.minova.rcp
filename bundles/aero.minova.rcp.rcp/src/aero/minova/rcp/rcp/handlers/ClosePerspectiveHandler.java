package aero.minova.rcp.rcp.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;
import aero.minova.rcp.perspectiveswitcher.handler.SwitchPerspectiveHandler;

public class ClosePerspectiveHandler extends SwitchPerspectiveHandler {

	@Inject
	TranslationService translationService;

	@Execute
	public void execute(MWindow window, @Optional @Named(E4WorkbenchParameterConstants.FORM_NAME) String perspectiveId) {

		// Finden der Perspektive
		List<MPerspective> perspectives = modelService.findElements(application, perspectiveId, MPerspective.class);
		MPerspective perspective = perspectives.get(0);
		boolean activePerspective = perspective == modelService.getActivePerspective(window);

		// Hat die Perspektive Änderungen, die verworfen werden sollen?
		boolean discard = true;
		@SuppressWarnings("unchecked")
		List<MPerspective> changedPerspectives = ((List<MPerspective>) application.getContext().get(Constants.DIRTY_PERSPECTIVES));
		changedPerspectives = changedPerspectives == null ? new ArrayList<>() : changedPerspectives;
		if (changedPerspectives.contains(perspective)) {
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), translationService.translate("@msg.ChangesDialog", null), null,
					translationService.translate("@msg.New.DirtyMessage", null), MessageDialog.CONFIRM,
					new String[] { translationService.translate("@Action.Discard", null), translationService.translate("@Abort", null) }, 0);
			discard = dialog.open() == 0;
		}
		if (!discard) {
			return;
		} else {
			changedPerspectives.remove(perspective);
		}

		// Entfernt die Perspektive
		modelService.deleteModelElement(perspective);

		// Wechselt zur Perspektive, die in der PerspektiveList den Index 0 hat, wenn geschlossene Perspektive die aktive war
		if (activePerspective) {
			List<MPerspective> perspectiveList = modelService.findElements(application, null, MPerspective.class);
			if (!perspectiveList.isEmpty()) {
				switchTo(perspectiveList.get(0), perspectiveList.get(0).getElementId());
			}
		}
	}

}
