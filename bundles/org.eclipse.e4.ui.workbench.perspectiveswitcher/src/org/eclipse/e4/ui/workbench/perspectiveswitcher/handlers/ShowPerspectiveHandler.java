/*******************************************************************************
 * Copyright (c) 2012 Joseph Carroll and others. All rights reserved. This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html Contributors: Joseph Carroll
 * <jdsalingerjr@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.e4.ui.workbench.perspectiveswitcher.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
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
import org.eclipse.e4.ui.workbench.perspectiveswitcher.commands.E4WorkbenchCommandConstants;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.commands.E4WorkbenchParameterConstants;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.internal.dialogs.SelectPerspectiveDialog;

public final class ShowPerspectiveHandler {
	
	
		@Execute
	public void execute(IEclipseContext context,
			@Optional @Named(E4WorkbenchParameterConstants.COMMAND_PERSPECTIVE_ID) String perspectiveID,
			@Optional @Named(E4WorkbenchParameterConstants.COMMAND_PERSPECTIVE_NEW_WINDOW) String newWindow,
			MApplication application, EModelService model, ECommandService commandService)
			throws InvocationTargetException, InterruptedException {

		MUIElement toolbar = model.find("aero.minova.rcp.rcp.toolbar.perspectiveswitchertoolbar", application);
		MUIElement toolitem = model.find("aero.minova.rcp.rcp.handledtoolitem." + perspectiveID, toolbar);

		if (perspectiveID == null || perspectiveID.equals("")) {
			openSelectionDialog(context);
		} else if (Boolean.parseBoolean(newWindow)) {
			openNewWindowPerspective(context, perspectiveID);
		} else {
			openPerspective(context, perspectiveID);
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
				newToolitem.setLabel(toolLabel);
//				newToolitem.setIconURI("platform:/plugin/aero.minova.rcp.rcp/icons/" + toolitemLabel + ".png");
				newToolitem.setEnabled(true);
			}
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
	 * Opens a view selection dialog, allowing the user to chose a view.
	 * 
	 * @throws ExecutionException If the perspective could not be opened.
	 */
	private final void openSelectionDialog(IEclipseContext context) {
		SelectPerspectiveDialog dialog = ContextInjectionFactory.make(SelectPerspectiveDialog.class, context);
		dialog.open();
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
//			String label = (String) context.get("NEW_PERSPECTIVE");
//			label = label.substring(0, 1).toUpperCase() + label.substring(1);
			perspective.setLabel(perspectiveID.substring(perspectiveID.lastIndexOf(".") + 1));
//			perspective.setLabel(label);
//			perspective.setTooltip(label);
			perspective.setParent(perspectiveStack);

			switchTo(context, perspective);

//			findAndInitPart(perspective, "search", perspectiveID);
//			findAndInitPart(perspective, "index", perspectiveID);
//			findAndInitPart(perspective, "details", perspectiveID);
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