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

		/*
		 * Holt die in dem Moment aktive Perspektive.
		 */
		MPerspective currentPerspective = modelService.getActivePerspective(window);
		String perspectiveID = currentPerspective.getElementId();

		/*
		 * Holt die UIElemente.
		 */
		List<MHandledToolItem> toolitems = modelService.findElements(toolbar,
				"aero.minova.rcp.rcp.handledtoolitem." + perspectiveID, MHandledToolItem.class);
		MHandledToolItem toolitem = (toolitems == null || toolitems.size() == 0) ? null : toolitems.get(0);

		List<MHandledToolItem> keepPerspectives = modelService.findElements(toolbar,
				"aero.minova.rcp.rcp.handledtoolitem.keepperspective", MHandledToolItem.class);
		MHandledToolItem keepPerspectiveItem = keepPerspectives.get(0);

		/*
		 * Entfernt das Toolitem einer Perspektive, falls der KeepIt Button nicht
		 * gesetzt ist.
		 */
		if (keepPerspectiveItem.isSelected()) {
			toolitem.setSelected(false);
		} else {
			toolitem.getParent().getChildren().remove(toolitem);
		}

		/*
		 * Resetet und Entfernt die aktuelle Perspektive.
		 */
		modelService.resetPerspectiveModel(currentPerspective, window);
		currentPerspective.getParent().getChildren().remove(currentPerspective);

		List<MPerspective> perspectiveList = modelService.findElements(application, null, MPerspective.class);

		/*
		 * Wechselt zur Perspektive, die in der PerspektiveList den Index 0 hat.
		 */
		switchTo(window.getContext(), perspectiveList.get(0), perspectiveList.get(0).getElementId(), window);

		/*
		 * Holt alle UIElemente und setzt die richtigen Selektionsstatus.
		 */
		List<MHandledToolItem> htoolitems = modelService.findElements(toolbar,
				"aero.minova.rcp.rcp.handledtoolitem." + perspectiveList.get(0).getElementId(), MHandledToolItem.class);

		MHandledToolItem htoolitem = (htoolitems == null || htoolitems.size() == 0) ? null : htoolitems.get(0);

		MPerspective newCurrentPerspective = modelService.getActivePerspective(window);

		if (htoolitem != null) {
			if (newCurrentPerspective.getElementId() == perspectiveList.get(0).getElementId()) {
				htoolitem.setSelected(true);
			} else {
				htoolitem.setSelected(false);
			}
		}

		/*
		 * Entfernt den Close Button, falls keine dynamischen Perspektiven aktive sind.
		 */
		MUIElement closeToolbar = modelService.find("aero.minova.rcp.rcp.toolbar.close", application);
		List<MHandledToolItem> closeToolitems = modelService.findElements(closeToolbar,
				"aero.minova.rcp.rcp.handledtoolitem.closeperspective", MHandledToolItem.class);
		MHandledToolItem closeToolitem = (closeToolitems == null || closeToolitems.size() == 0) ? null
				: closeToolitems.get(0);

		if (closeToolitem != null) {
			if (newCurrentPerspective.getElementId().equals("aero.minova.rcp.rcp.perspective.sis")) {
				closeToolitem.getParent().getChildren().remove(0);
			}
		}

	}

}
