/*******************************************************************************
 * Copyright (c) 2012 Joseph Carroll and others. All rights reserved. This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html Contributors: Joseph Carroll
 * <jdsalingerjr@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.e4.ui.workbench.perspectiveswitcher.tools;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@Creatable
public class EPerspectiveSwitcher {

	@Inject
	EModelService modelService;

	@Inject
	IEventBroker eventBroker;

	@Inject
	MWindow window;

	IPerspectiveSwitcherControl control;

	private EventHandler selectionHandler = new EventHandler() {

		@Override
		public void handleEvent(Event event) {
			if (window == null) {
				return;
			}

			MUIElement changedElement = (MUIElement) event.getProperty(UIEvents.EventTags.ELEMENT);
			if (!(changedElement instanceof MPerspectiveStack)) {
				return;
			}

			MPerspectiveStack perspectiveStack = (MPerspectiveStack) changedElement;
			if (!perspectiveStack.isToBeRendered()) {
				return;
			}

			MWindow stackWindow = modelService.getContainingContext(perspectiveStack).get(MWindow.class);
			if (window != stackWindow) {
				return;
			}

			MPerspective selectedElement = perspectiveStack.getSelectedElement();
			control.setSelectedElement(selectedElement);
		}
	};

	void init() {
		// Handles the changes in the selected element
		eventBroker.subscribe(UIEvents.ElementContainer.TOPIC_SELECTEDELEMENT, selectionHandler);
	}

	@PreDestroy
	void cleanUp() {
		eventBroker.unsubscribe(selectionHandler);
	}

	/**
	 * Sets the UI control associated with the perspective switcher. The perspective switcher subscribes to topics only once a non-null ui control is passed and
	 * unsubscribes from all topics if a null value is received.
	 *
	 * @param uiControl
	 *            the visual representation of the perspective switcher
	 */
	public void setControlProvider(IPerspectiveSwitcherControl uiControl) {
		if (uiControl == null) {
			control = null;
			cleanUp();
		} else {
			if (control == null) {
				init();
			}
			control = uiControl;
		}
	}

}
