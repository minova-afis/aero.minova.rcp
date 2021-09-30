
package aero.minova.rcp.rcp.addons;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.event.Event;

import aero.minova.rcp.dataservice.ImageUtil;

public class IconRendering {

	@Inject
	@Optional
	public void applicationStarted(@EventTopic(UIEvents.UILifeCycle.ACTIVATE) Event event, MApplication mApp, EModelService eModelService) {
		ImageUtil.updateModelIcons(mApp, eModelService);
	}
}
