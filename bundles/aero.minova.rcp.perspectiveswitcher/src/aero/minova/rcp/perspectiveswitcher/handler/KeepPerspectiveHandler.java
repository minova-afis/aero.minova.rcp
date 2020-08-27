package aero.minova.rcp.perspectiveswitcher.handler;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class KeepPerspectiveHandler extends SwitchPerspectiveHandler {

	@Inject
	MApplication application;

	@Inject
	EModelService modelService;

	@Inject
	EPartService partService;

	@Execute
	public void execute(MWindow window) {
		
		MPerspective currentPerspective = modelService.getActivePerspective(window);
		
		currentPerspective.getTags().add("KeepIt");
		
		/*
		 * Wechselt zur Perspektive, die in der PerspektiveList den Index 0 hat.
		 */
		List<MPerspective> perspectiveList = modelService.findElements(application, null, MPerspective.class);
		switchTo(window.getContext(), perspectiveList.get(0), perspectiveList.get(0).getElementId(), window);

	
	}

}
