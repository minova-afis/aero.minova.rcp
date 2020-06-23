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
		@SuppressWarnings("unchecked")
		MElementContainer<MUIElement> perspectiveStack = (MElementContainer<MUIElement>) modelService.find("aero.minova.rcp.rcp.perspectivestack", window);

		Logger.getGlobal().log(Level.INFO, "Trying to Switch Perspective " + perspectiveId);

		MUIElement element = modelService.find(PERSPECTIVE_PREFIX + perspectiveId, window);
		if (element == null) {
			/* MPerspective perspective = */ createNewPerspective(window, perspectiveStack, perspectiveId);
		} else {
			switchTo(element);
		}
	}

	/**
	 * Erzeugt eine neue Perspektive mit rudimentärem Inhalt. Die Ansicht wechselt sofort zur neuen Perspektive.
	 * 
	 * @param window
	 * @param perspectiveStack
	 * @param perspectiveId
	 * @return die neue Perspektive
	 */
	private MPerspective createNewPerspective(MWindow window, MElementContainer<MUIElement> perspectiveStack, String perspectiveId) {
		MPerspective perspective = null;
		MUIElement element = modelService.cloneSnippet(window, SNIPPET_PERSPECTIVE, window);

		if (element == null) {
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

		return perspective;
	}

	/**
	 * Initialisiert einen {@link SimplePart}. Dazu muss die Perspektive schon ausgewählt sein, weil sonst der Part noch nicht erzeugt wurde.
	 * 
	 * @param perspective
	 * @param partId
	 * @param text
	 */
	private void findAndInitPart(MPerspective perspective, String partId, String text) {
		MUIElement elem = modelService.find(PARTID_PREFIX + partId, perspective);
		if (elem instanceof MPart) {
			MPart part = (MPart) elem;
			Object object = part.getObject();
			if (object instanceof SimplePart) {
				((SimplePart) object).setText(text);
			}
		} else {
			Logger.getGlobal().log(Level.SEVERE, "Can't find Part " + partId);
		}
	}

	/**
	 * wechselt zur angegebenen Perspektive, falls das Element eine Perspektive ist
	 * 
	 * @param element
	 */
	private void switchTo(MUIElement element) {
		if (element instanceof MPerspective) {
			partService.switchPerspective((MPerspective) element);
		} else {
			Logger.getGlobal().log(Level.WARNING, element.getElementId() + " is no Perspective");
		}
	}
}