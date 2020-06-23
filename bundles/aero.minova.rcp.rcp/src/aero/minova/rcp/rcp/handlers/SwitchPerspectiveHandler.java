package aero.minova.rcp.rcp.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.rcp.parts.SimplePart;

public class SwitchPerspectiveHandler {
	private static final String COMMAND_PARAMETER = "aero.minova.rcp.rcp.commandparameter.perspectiveId";
	private static final String PARTID_PREFIX = "aero.minova.rcp.rcp.part.";
	private static final String PERSPECTIVE_PREFIX = "aero.minova.rcp.perspective.";
	private static final String SNIPPET_PERSPECTIVE = PERSPECTIVE_PREFIX + "main";

	@Inject
	EModelService modelService;

	@Inject
	EPartService partService;

	@Execute
	public void execute(MWindow window, @Named(COMMAND_PARAMETER) String perspectiveId) {
		MElementContainer<MUIElement> perspectiveStack = (MElementContainer<MUIElement>) modelService.find("aero.minova.rcp.rcp.perspectivestack", window);

		MUIElement element = modelService.find(PERSPECTIVE_PREFIX + perspectiveId, window);
		MPerspective perspective;
		if (element == null) {
			element = modelService.cloneSnippet(window, SNIPPET_PERSPECTIVE, window);

			if (element == null) {
				// immer noch?
				Logger.getGlobal().log(Level.SEVERE, "Can't find nor clone Perspective " + perspectiveId);
			} else {
				element.setElementId(PERSPECTIVE_PREFIX + perspectiveId);
				perspective = (MPerspective) element;
				perspective.setParent(perspectiveStack);

				switchTo(perspective);

				findAndInitPart(perspective, "search", perspectiveId);
				findAndInitPart(perspective, "index", perspectiveId);
				findAndInitPart(perspective, "details", perspectiveId);
			}
		} else {
			switchTo(element);
		}
	}

	private void findAndInitPart(MPerspective perspective, String partId, String perspectiveId) {
		MUIElement elem = modelService.find(PARTID_PREFIX + partId, perspective);
		if (elem instanceof MPart) {
			MPart part = (MPart) elem;
			Object object = part.getObject();
			if (object instanceof SimplePart) {
				((SimplePart) object).setText(perspectiveId);
			}
		} else {
			Logger.getGlobal().log(Level.SEVERE, "Can't find Part " + partId);
		}
	}

	private void switchTo(MUIElement element) {
		if (element instanceof MPerspective) {
			partService.switchPerspective((MPerspective) element);
		} else {
			Logger.getGlobal().log(Level.WARNING, element.getElementId() + " is no Perspective");
		}
	}
}