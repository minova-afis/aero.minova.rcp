package aero.minova.rcp.perspectiveswitcher.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchCommandConstants;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

public class SwitchPerspectiveHandler {

	@Inject
	MApplication application;

	@Inject
	ECommandService commandService;

	@Inject
	EModelService model;

	@Execute
	public void execute(IEclipseContext context,
			@Optional @Named(E4WorkbenchParameterConstants.COMMAND_PERSPECTIVE_ID) String perspectiveID,
			@Optional @Named(E4WorkbenchParameterConstants.COMMAND_PERSPECTIVE_NEW_WINDOW) String newWindow)
			throws InvocationTargetException, InterruptedException {

		if (Boolean.parseBoolean(newWindow)) {
			openNewWindowPerspective(context, perspectiveID);
		} else {
			openPerspective(context, perspectiveID);
			createNewToolItem(perspectiveID);
			createCloseItem();

		}

	}

	/*
	 * Creating new HandledToolItem for each Perspective that is open
	 * 
	 */
	public void createNewToolItem(
			@Optional @Named(E4WorkbenchParameterConstants.COMMAND_PERSPECTIVE_ID) String perspectiveID) {


		MUIElement toolbar = model.find("aero.minova.rcp.rcp.toolbar.perspectiveswitchertoolbar", application);
		MUIElement toolitem = model.find("aero.minova.rcp.rcp.handledtoolitem." + perspectiveID, toolbar);

		if (toolitem == null) {
			final MHandledToolItem newToolitem = model.createModelElement(MHandledToolItem.class);
			MCommand command = null;

			String toolitemLabel = perspectiveID.substring(perspectiveID.lastIndexOf(".") + 1);
			String toolLabel = toolitemLabel.substring(0, 1).toUpperCase() + toolitemLabel.substring(1);

			((MToolBar) toolbar).getChildren().add(newToolitem);
			command = model.createModelElement(MCommand.class);
			command.setElementId("aero.minova.rcp.rcp.command.openform");
			application.getCommands().add(command);

			MParameter parameter = model.createModelElement(MParameter.class);
			parameter.setName("org.eclipse.e4.ui.perspectives.parameters.perspectiveId");
			parameter.setElementId("org.eclipse.e4.ui.perspectives.parameters.perspectiveId33");
			parameter.setValue(perspectiveID);

			newToolitem.setElementId("aero.minova.rcp.rcp.handledtoolitem." + perspectiveID);
			newToolitem.setCommand(command);
			newToolitem.getParameters().add(parameter);
			newToolitem.setToBeRendered(true);
			newToolitem.setLabel(toolLabel);
//			newToolitem.setIconURI("platform:/plugin/aero.minova.rcp.rcp/icons/open_in_app" + toolitemLabel +  ".png");

		}
	}

	/*
	 * Create a HandledToolitem to Close Perspectives, if a perspective was opened.
	 */

	public void createCloseItem() {

		MUIElement closeToolbar = model.find("aero.minova.rcp.rcp.toolbar.close", application);
		MUIElement closeToolitem = model.find("aero.minova.rcp.rcp.handledtoolitem.closeperspective", closeToolbar);

		if (closeToolitem == null) {
			final MHandledToolItem closeNewToolitem = model.createModelElement(MHandledToolItem.class);
			MCommand closeCommand = null;
			((MToolBar) closeToolbar).getChildren().add(closeNewToolitem);
			closeCommand = model.createModelElement(MCommand.class);
			closeCommand.setElementId("aero.minova.rcp.rcp.command.closeperspective");
			application.getCommands().add(closeCommand);

			closeNewToolitem.setElementId("aero.minova.rcp.rcp.handledtoolitem.closeperspective");
			closeNewToolitem.setCommand(closeCommand);
			closeNewToolitem.setToBeRendered(true);
			closeNewToolitem.setLabel("Close");
			closeNewToolitem.setEnabled(true);
		}

	}

	/**
	 * Opens the perspective with the given identifier.
	 * 
	 * @param perspectiveId The perspective to open; must not be <code>null</code>
	 * @throws ExecutionException If the perspective could not be opened.
	 */
	private final void openPerspective(IEclipseContext context, String perspectiveID) {
		MApplication application = context.get(MApplication.class);
		EModelService modelService = context.get(EModelService.class);

		MUIElement element = modelService.find(perspectiveID, application);
		if (element == null) {
			/* MPerspective perspective = */ createNewPerspective(context, perspectiveID);
		} else {
			switchTo(context, element);
		}
	}

	/**
	 * Opens the specified perspective in a new window.
	 * 
	 * @param perspectiveId The perspective to open; must not be <code>null</code>
	 * @throws ExecutionException If the perspective could not be opened.
	 */
	private void openNewWindowPerspective(IEclipseContext context, String perspectiveID) {
		MApplication application = context.get(MApplication.class);
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
		MApplication application = context.get(MApplication.class);
		MWindow window = context.get(MWindow.class);
		EModelService modelService = context.get(EModelService.class);

		@SuppressWarnings("unchecked")
		MElementContainer<MUIElement> perspectiveStack = (MElementContainer<MUIElement>) modelService
				.find("aero.minova.rcp.rcp.perspectivestack", application);

		MPerspective perspective = null;
		MUIElement element = modelService.cloneSnippet(window, E4WorkbenchCommandConstants.SNIPPET_PERSPECTIVE, window);

		if (element == null) {
//			Logger.getGlobal().log(Level.SEVERE, "Can't find nor clone Perspective " + perspectiveID);
		} else {
			element.setElementId(perspectiveID);
			perspective = (MPerspective) element;
			perspective.setContext(context);
			perspective.setLabel(perspectiveID.substring(perspectiveID.lastIndexOf(".") + 1));
			perspective.setParent(perspectiveStack);

			switchTo(context, perspective);

		}

		return perspective;
	}

	/**
	 * wechselt zur angegebenen Perspektive, falls das Element eine Perspektive ist
	 * 
	 * @param element
	 */
	private void switchTo(IEclipseContext context, MUIElement element) {
		EPartService partService = context.get(EPartService.class);

		if (element instanceof MPerspective) {
			partService.switchPerspective((MPerspective) element);
		} else {
			// error
		}
	}

}
