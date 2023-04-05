
package aero.minova.rcp.rcp.addons;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.event.Event;

import aero.minova.rcp.dataservice.ImageUtil;

public class IconRendering {

	@Inject
	@Optional
	EModelService modelService;

	@Inject
	@Optional
	void subscribeTopicChildrenChanged(@UIEventTopic(UIEvents.ElementContainer.TOPIC_CHILDREN) Event event) {
		Object objElement = event.getProperty(UIEvents.EventTags.ELEMENT);

		// only care of added elements
		if (!UIEvents.isADD(event)) {
			return;
		}
		// Ensure that this event is for a MMenuItem or MToolBar
		if (!(objElement instanceof MMenuElement && !(objElement instanceof MPopupMenu)) && !(objElement instanceof MToolBar)) {
			return;
		}

		// Ensure that it's a View part's menu or toolbar
		MUIElement uiElement = (MUIElement) objElement;
		MUIElement parent = modelService.getContainer(uiElement);
		if (!(parent instanceof MPart)) {
			return;
		}

		MPart part = (MPart) parent;
		ImageUtil.updateIconsForPart(part);
		if (uiElement instanceof MToolBar toolbar) {
			ImageUtil.updateIconsForToolbarItems(toolbar);
		}

	}
}
