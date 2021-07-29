package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import aero.minova.rcp.constants.Constants;

public class ExpandOrCollapseIndexHandler {

	@Inject
	IEventBroker broker;

	@Execute
	public void execute(MPart mpart, @Optional @Named("aero.minova.rcp.rcp.commandparameter.expandorcollapseindex") String name) {
		if (name.equals("EXPAND")) {
			broker.post(Constants.BROKER_EXPANDINDEX, "Expand Index");
		} else if (name.equals("COLLAPSE")) {
			broker.post(Constants.BROKER_COLLAPSEINDEX, "Collapse Index");
		}
	}
}
