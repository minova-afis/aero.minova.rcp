package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;
import aero.minova.rcp.perspectiveswitcher.handler.SwitchPerspectiveHandler;

public class ClosePerspectiveHandler extends SwitchPerspectiveHandler {

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

		List<MHandledToolItem> toolitems = modelService.findElements(toolbar,
				"aero.minova.rcp.rcp.handledtoolitem." + perspectiveID, MHandledToolItem.class);
		MHandledToolItem toolitem = (toolitems == null || toolitems.size() == 0) ? null : toolitems.get(0);

//		MUIElement closeToolbar = modelService.find("aero.minova.rcp.rcp.toolbar.close", application);
//		MUIElement closeToolitem = modelService.find("aero.minova.rcp.rcp.handledtoolitem.closeperspective",
//				closeToolbar);

		List<MHandledToolItem> keepPerspectives = modelService.findElements(toolbar,
				"aero.minova.rcp.rcp.handledtoolitem.keepperspective", MHandledToolItem.class);
		MHandledToolItem keepPerspectiveItem = keepPerspectives.get(0);

		if (keepPerspectiveItem.isSelected()) {
			System.out.println("Item behalten");
			toolitem.setSelected(false);
		} else {
			toolitem.getParent().getChildren().remove(toolitem);
		}

		modelService.resetPerspectiveModel(currentPerspective, window);
		currentPerspective.getParent().getChildren().remove(currentPerspective);
		List<MPerspective> perspectiveList = modelService.findElements(application, null, MPerspective.class);

		this.switchTo(window.getContext(), perspectiveList.get(0), perspectiveList.get(0).getElementId(), window);

		List<MHandledToolItem> htoolitems = modelService.findElements(toolbar,
				"aero.minova.rcp.rcp.handledtoolitem." + perspectiveList.get(0).getElementId(), MHandledToolItem.class);
		MHandledToolItem htoolitem = (htoolitems == null || htoolitems.size() == 0) ? null : htoolitems.get(0);

		MPerspective newCurrentPerspective = modelService.getActivePerspective(window);

		if (newCurrentPerspective.getElementId() == perspectiveList.get(0).getElementId()) {
			htoolitem.setSelected(true);
		} else {
			htoolitem.setSelected(false);
		}

	}

}
