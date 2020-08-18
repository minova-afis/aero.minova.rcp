package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
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

		MUIElement closeToolbar = modelService.find("aero.minova.rcp.rcp.toolbar.close", application);
		MUIElement closeToolitem = modelService.find("aero.minova.rcp.rcp.handledtoolitem.closeperspective",
				closeToolbar);

		List<MHandledToolItem> keepPerspectives = modelService.findElements(toolbar,
				"aero.minova.rcp.rcp.handledtoolitem.keepperspective", MHandledToolItem.class);
		MHandledToolItem keepPerspectiveItem = keepPerspectives.get(0);


		if (keepPerspectiveItem.isSelected()) {
			System.out.println("Item behalten");
		} else {
			toolitem.getParent().getChildren().remove(toolitem);
		}

		currentPerspective.getParent().getChildren().remove(currentPerspective);
		partService.switchPerspective("aero.minova.rcp.rcp.perspective.stundenerfassung");

		if (closeToolitem.isToBeRendered()) {

			closeToolitem.getParent().getChildren().remove(closeToolitem);

		}

	}

}
