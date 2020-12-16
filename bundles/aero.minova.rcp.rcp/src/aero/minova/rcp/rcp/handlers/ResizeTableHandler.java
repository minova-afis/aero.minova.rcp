package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import aero.minova.rcp.rcp.util.Constants;

public class ResizeTableHandler {

	@Inject
	IEventBroker broker;

	@Execute
	public void execute(MPart mpart, MPerspective mPerspective) {
		if (mPerspective == null) {
			// TODO Info an den Benutzer, Ist die Perspektive jemals Null?
			return;
		}
		broker.post(Constants.BROKER_RESIZETABLE, mpart);
	}

}
