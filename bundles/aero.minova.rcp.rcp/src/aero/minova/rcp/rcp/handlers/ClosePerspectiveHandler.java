package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;
import aero.minova.rcp.perspectiveswitcher.handler.SwitchPerspectiveHandler;

/**
 *
 */
public class ClosePerspectiveHandler extends SwitchPerspectiveHandler {

	@Inject
	MApplication application;

	@Inject
	EModelService modelService;

	@Inject
	EPartService partService;

	@Execute
	public void execute(MWindow window, @Optional @Named(E4WorkbenchParameterConstants.FORM_NAME) String perspectiveId) {

		/*
		 * Entfernt die aktuelle Perspektive.
		 */
		List<MPerspective> perspective = modelService.findElements(application, perspectiveId, MPerspective.class);
		boolean activePerspective = perspective.get(0) == modelService.getActivePerspective(window);
		modelService.deleteModelElement(perspective.get(0));

		/*
		 * Wechselt zur Perspektive, die in der PerspektiveList den Index 0 hat, wenn geschlossene Perspektive die aktive war
		 */
		if (activePerspective) {
			List<MPerspective> perspectiveList = modelService.findElements(application, null, MPerspective.class);
			if (!perspectiveList.isEmpty()) {
				switchTo(perspectiveList.get(0), perspectiveList.get(0).getElementId(), window);
			}
		}
	}

}
