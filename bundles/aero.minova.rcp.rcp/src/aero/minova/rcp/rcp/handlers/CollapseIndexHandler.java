package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import aero.minova.rcp.constants.Constants;

public class CollapseIndexHandler {

	@Inject
	IEventBroker broker;

	@Execute
	public void execute(MPart mpart) {
		broker.post(Constants.BROKER_COLLAPSEINDEX, "Collapse Index");
	}

}
