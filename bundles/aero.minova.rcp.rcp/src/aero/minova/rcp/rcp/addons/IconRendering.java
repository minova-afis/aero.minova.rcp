
package aero.minova.rcp.rcp.addons;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.event.Event;

public class Iconrendering {

	@Inject
	@Optional
	public void applicationStarted(
			@EventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) Event event, MApplication mApp, EModelService eModelService) {
		// TODO Modify the UIEvents.UILifeCycle.APP_STARTUP_COMPLETE EventTopic to a certain event you want to listen to.
		List<MPart> findElements = eModelService.findElements(mApp, null, MPart.class);
		for (MPart mPart : findElements) {
			String iconURI = mPart.getIconURI();
			if (iconURI.contains("32x32")) {
				iconURI = iconURI.replace("32x32", "24x24");
				mPart.setIconURI(iconURI);
			}
		}
	}
}
