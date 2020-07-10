package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class ClosePerspectiveHandler {

	@Inject
	MApplication application;

	@Inject
	EModelService modelService;

	@Inject
	EPartService partService;

	@Execute
	public void execute(MWindow window) {

		MUIElement toolbar = modelService.find("aero.minova.rcp.rcp.toolbar.perspectiveswitchertoolbar", application);
		MPerspective currentPerspective = modelService.getActivePerspective(window);
		String perspectiveID = currentPerspective.getElementId();
		MUIElement toolitem = modelService.find("aero.minova.rcp.rcp.handledtoolitem." + perspectiveID, toolbar);

		currentPerspective.setToBeRendered(false);
		currentPerspective.getParent().getChildren().remove(currentPerspective);

		toolitem.setToBeRendered(false);
		toolitem.getParent().getChildren().remove(toolitem);

		partService.switchPerspective("aero.minova.rcp.rcp.perspective.home");

	}

}
