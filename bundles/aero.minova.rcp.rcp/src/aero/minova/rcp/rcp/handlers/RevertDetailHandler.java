package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;

import aero.minova.rcp.rcp.util.Constants;

public class RevertDetailHandler {

	@Inject
	IEventBroker broker;

	@Execute
	public void execute(@Optional MPerspective perspective) {
		if (perspective == null) {
			return;
		}
		broker.post(Constants.BROKER_REVERTENTRY, perspective);

	}
}
