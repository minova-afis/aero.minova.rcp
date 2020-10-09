package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

public class DeleteDetailHandler {

	@Inject
	IEventBroker broker;

	@Execute
	public void execute() {
		// XMLDetailPart xmlPart = (XMLDetailPart) mpart;
		broker.post("DeleteEntry", null);
		// xmlPart.buildDeleteTable();

	}
}
