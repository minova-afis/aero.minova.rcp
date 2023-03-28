package aero.minova.rcp.perspectiveswitcher.handler;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.constants.Constants;

public class SwitchPerspectiveHandler {

	@Inject
	protected MApplication application;

	@Inject
	EPartService partService;

	@Inject
	EModelService modelService;

	@Execute
	public void execute(IEclipseContext context, //
			@Optional @Named(Constants.FORM_NAME) String formName, //
			@Optional @Named(Constants.FORM_ID) String perspectiveId, //
			@Optional @Named(Constants.FORM_LABEL) String perspectiveName, //
			@Optional @Named(Constants.FORM_ICON) String perspectiveIcon, MWindow window) {

		Objects.requireNonNull(formName);
		Objects.requireNonNull(perspectiveId);
		Objects.requireNonNull(perspectiveName);

		openPerspective(context, perspectiveId, window, formName, perspectiveName, perspectiveIcon);
	}

	/**
	 * Opens the perspective with the given identifier.
	 *
	 * @param perspectiveIcon
	 * @param perspectiveId
	 *            The perspective to open; must not be <code>null</code>
	 * @throws ExecutionException
	 *             If the perspective could not be opened.
	 */
	private final void openPerspective(IEclipseContext context, String perspectiveID, MWindow window, String formName, String perspectiveName,
			String perspectiveIcon) {
		MUIElement element = modelService.find(perspectiveID, application);
		if (element == null) {
			/* MPerspective perspective = */ createNewPerspective(context, perspectiveID, formName, perspectiveName, perspectiveIcon);
		} else {
			switchTo(element, perspectiveID, window);
		}
	}

	/**
	 * Erzeugt eine neue Perspektive mit rudimentärem Inhalt. Die Ansicht wechselt sofort zur neuen Perspektive.
	 *
	 * @param window
	 * @param perspectiveStack
	 * @param perspectiveID
	 * @param perspectiveIcon
	 * @return die neue Perspektive
	 */
	private MPerspective createNewPerspective(IEclipseContext context, String perspectiveID, String formName, String perspectiveName, String perspectiveIcon) {
		MWindow window = context.get(MWindow.class);

		@SuppressWarnings("unchecked")
		MElementContainer<MUIElement> perspectiveStack = (MElementContainer<MUIElement>) modelService.find("aero.minova.rcp.rcp.perspectivestack", application);

		MPerspective perspective = null;
		MUIElement element;
		if (perspectiveID.equalsIgnoreCase("statistic")) {
			element = modelService.cloneSnippet(window, "aero.minova.rcp.rcp.perspective.statistic", window);
		} else {
			element = modelService.cloneSnippet(window, "aero.minova.rcp.rcp.perspective.main", window);
		}

		if (element == null) {
			Logger.getGlobal().log(Level.SEVERE, "Can't find or clone Perspective " + perspectiveID);
		} else {

			element.setElementId(perspectiveID);
			perspective = (MPerspective) element;
			perspective.getPersistedState().put(Constants.FORM_NAME, formName);
			perspective.setLabel(perspectiveName);
			perspective.setIconURI(perspectiveIcon);
			perspectiveStack.getChildren().add(perspective);
			switchTo(perspective, perspectiveID, window);
		}
		return perspective;
	}

	/**
	 * wechselt zur angegebenen Perspektive, falls das Element eine Perspektive ist
	 *
	 * @param element
	 */
	public void switchTo(MUIElement element, @Named(Constants.FORM_NAME) String perspectiveID, MWindow window) {
		if (element instanceof MPerspective) {
			partService.switchPerspective(perspectiveID);
		} else {
			Logger.getGlobal().log(Level.SEVERE, "Can't find or clone Perspective " + perspectiveID);
		}

	}

}
