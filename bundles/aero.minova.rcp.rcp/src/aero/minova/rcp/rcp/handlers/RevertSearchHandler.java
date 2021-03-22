package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;

public class RevertSearchHandler {

	@Inject
	IEventBroker broker;

	@Execute
	public void execute(Shell shell) {
		System.out.println("Revert NatTable Search Part");
		broker.post(Constants.BROKER_REVERTSEARCHTABLE, "Revert Search Table");
	}

}
