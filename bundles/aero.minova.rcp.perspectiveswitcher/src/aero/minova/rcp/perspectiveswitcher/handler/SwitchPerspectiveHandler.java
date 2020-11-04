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
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

public class SwitchPerspectiveHandler {

	@Inject
	MApplication application;

	@Inject
	ECommandService commandService;

	@Inject
	EModelService model;

	@Inject
	IEventBroker broker;

	@Execute
	public void execute(IEclipseContext context,
			@Optional @Named(E4WorkbenchParameterConstants.FORM_NAME) String perspectiveID,
			@Optional @Named(E4WorkbenchParameterConstants.COMMAND_PERSPECTIVE_NEW_WINDOW) String newWindow,
			MWindow window) throws InvocationTargetException, InterruptedException {
		if (Boolean.parseBoolean(newWindow)) {
			openNewWindowPerspective(context, perspectiveID);
		} else {
			openPerspective(context, perspectiveID, window);
		}
	}

	/**
	 * Opens the perspective with the given identifier.
	 * 
	 * @param perspectiveId The perspective to open; must not be <code>null</code>
	 * @throws ExecutionException If the perspective could not be opened.
	 */
	private final void openPerspective(IEclipseContext context, String perspectiveID, MWindow window) {
		MApplication application = context.get(MApplication.class);
		EModelService modelService = context.get(EModelService.class);

		MUIElement element = modelService.find(perspectiveID, application);
		if (element == null) {
			/* MPerspective perspective = */ createNewPerspective(context, perspectiveID);
		} else {
			switchTo(context, element, perspectiveID, window);
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
	private MPerspective createNewPerspective(IEclipseContext context, String perspectiveID) {
		MWindow window = context.get(MWindow.class);
		EModelService modelService = context.get(EModelService.class);
		String[] ids = perspectiveID.split(".xml");
		String id = ids[0];
		List<MHandledMenuItem> items = model.findElements(window.getMainMenu(), id, MHandledMenuItem.class);
		MHandledMenuItem item = items.get(0);

		@SuppressWarnings("unchecked")
		MElementContainer<MUIElement> perspectiveStack = (MElementContainer<MUIElement>) modelService
				.find("aero.minova.rcp.rcp.perspectivestack", application);

		MPerspective perspective = null;
		IEclipseContext ctx = window.getContext().createChild();
		window.getContext().set(E4WorkbenchParameterConstants.FORM_NAME, perspectiveID);
//		MUIElement element = modelService.cloneSnippet(window, E4WorkbenchCommandConstants.SNIPPET_PERSPECTIVE, window);
		MUIElement element = modelService.cloneSnippet(window, "aero.minova.rcp.rcp.perspective.main", window);

		if (element == null) {
			Logger.getGlobal().log(Level.SEVERE, "Can't find or clone Perspective " + perspectiveID);
		} else {
			element.setElementId(perspectiveID);
			perspective = (MPerspective) element;
			perspective.setContext(context);
			perspective.setLabel(item.getLabel());
			perspectiveStack.getChildren().add(0, perspective);
			switchTo(context, perspective, perspectiveID, window);

		}
		return perspective;
	}

	/**
	 * wechselt zur angegebenen Perspektive, falls das Element eine Perspektive ist
	 * 
	 * @param element
	 */
	public void switchTo(IEclipseContext context, MUIElement element,
			@Named(E4WorkbenchParameterConstants.FORM_NAME) String perspectiveID, MWindow window) {
		EPartService partService = context.get(EPartService.class);

		if (element instanceof MPerspective) {
			partService.switchPerspective(element.getElementId());
		} else {
			Logger.getGlobal().log(Level.SEVERE, "Can't find or clone Perspective " + perspectiveID);
		}

	}

}
