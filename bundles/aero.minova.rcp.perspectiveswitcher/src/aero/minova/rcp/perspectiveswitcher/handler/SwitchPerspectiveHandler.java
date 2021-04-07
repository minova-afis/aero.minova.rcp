package aero.minova.rcp.perspectiveswitcher.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

public class SwitchPerspectiveHandler {

	@Inject
	MApplication application;

	@Inject
	ECommandService commandService;
	
	@Inject
	EPartService partService;

	@Inject
	EModelService model;

	@Inject
	IEventBroker broker;

	@Execute
	public void execute(IEclipseContext context,
			@Optional @Named(E4WorkbenchParameterConstants.FORM_NAME) String formName,
			@Optional @Named(E4WorkbenchParameterConstants.COMMAND_PERSPECTIVE_NEW_WINDOW) String newWindow,
			@Optional MHandledMenuItem handledMenuItem,
			MWindow window) throws InvocationTargetException, InterruptedException {
		if (Boolean.parseBoolean(newWindow)) {
			openNewWindowPerspective(context, formName);
		} else {
			openPerspective(context, formName, window, formName);
		}
	}

	/**
	 * Opens the perspective with the given identifier.
	 * 
	 * @param perspectiveId The perspective to open; must not be <code>null</code>
	 * @throws ExecutionException If the perspective could not be opened.
	 */
	private final void openPerspective(IEclipseContext context, String perspectiveID, MWindow window, String formName) {
		MApplication application = context.get(MApplication.class);
		EModelService modelService = context.get(EModelService.class);
		if (perspectiveID.contains("."))
			perspectiveID = perspectiveID.substring(0, perspectiveID.indexOf("."));

		MUIElement element = modelService.find(perspectiveID, application);
		if (element == null) {
			/* MPerspective perspective = */ createNewPerspective(context, perspectiveID, formName);
		} else {
			switchTo(element, perspectiveID, window);
		}
	}

	/**
	 * Opens the specified perspective in a new window.
	 * 
	 * @param perspectiveId The perspective to open; must not be <code>null</code>
	 * @throws ExecutionException If the perspective could not be opened.
	 */
	private void openNewWindowPerspective(IEclipseContext context, String perspectiveID) {
		EModelService modelService = context.get(EModelService.class);
		EPartService partService = context.get(EPartService.class);

		List<MPerspective> perspectives = modelService.findElements(application, perspectiveID, MPerspective.class,
				null);
		partService.switchPerspective(perspectives.get(0));

	}

	/**
	 * Erzeugt eine neue Perspektive mit rudimentärem Inhalt. Die Ansicht wechselt
	 * sofort zur neuen Perspektive.
	 * 
	 * @param window
	 * @param perspectiveStack
	 * @param perspectiveID
	 * @return die neue Perspektive
	 */
	private MPerspective createNewPerspective(IEclipseContext context, String perspectiveID, String formName) {
		MWindow window = context.get(MWindow.class);
		EModelService modelService = context.get(EModelService.class);
		List<MHandledMenuItem> items = model.findElements(window.getMainMenu(), perspectiveID, MHandledMenuItem.class);
		MHandledMenuItem item = items.get(0);

		@SuppressWarnings("unchecked")
		MElementContainer<MUIElement> perspectiveStack = (MElementContainer<MUIElement>) modelService
				.find("aero.minova.rcp.rcp.perspectivestack", application);

		MPerspective perspective = null;
		MUIElement element = modelService.cloneSnippet(window, "aero.minova.rcp.rcp.perspective.main", window);

		if (element == null) {
			Logger.getGlobal().log(Level.SEVERE, "Can't find or clone Perspective " + perspectiveID);
		} else {
			element.setElementId(perspectiveID);
			perspective = (MPerspective) element;
			perspective.getPersistedState().put(E4WorkbenchParameterConstants.FORM_NAME, formName);
			perspective.setLabel(item.getLabel());
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
	public void switchTo(MUIElement element, @Named(E4WorkbenchParameterConstants.FORM_NAME) String perspectiveID,
			MWindow window) {

		if (element instanceof MPerspective) {
			partService.switchPerspective(element.getElementId());
		} else {
			Logger.getGlobal().log(Level.SEVERE, "Can't find or clone Perspective " + perspectiveID);
		}

	}

}
