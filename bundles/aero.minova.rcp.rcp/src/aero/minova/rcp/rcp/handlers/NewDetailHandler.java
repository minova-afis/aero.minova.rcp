package aero.minova.rcp.rcp.handlers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import aero.minova.rcp.rcp.util.Constants;

public class NewDetailHandler {

	@Inject
	EModelService model;
	@Inject
	private IEventBroker broker;

	@Execute
	public void execute(MPart mpart, @Optional MPerspective perspective) {
		if (perspective == null) {
			return;
		}
		Map<MPerspective, String> map = new HashMap<>();
		map.put(perspective, Constants.CLEAR_REQUEST);
		broker.post(Constants.BROKER_CLEARFIELDS, map);
	}
}
