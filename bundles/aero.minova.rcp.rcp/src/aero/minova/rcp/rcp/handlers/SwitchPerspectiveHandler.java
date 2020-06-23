package aero.minova.rcp.rcp.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class SwitchPerspectiveHandler {
	private static final String COMMAND_PARAMETER = "aero.minova.rcp.rcp.commandparameter.perspectiveId";

	@Execute
	public void execute(MWindow window, EPartService partService, EModelService modelService, @Named(COMMAND_PARAMETER) String perspectiveId) {
		MUIElement element = modelService.find(perspectiveId, window);
		if (element instanceof MPerspective) {
			partService.switchPerspective((MPerspective) element);
		}
	}
}