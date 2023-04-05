package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.rcp.parts.WFCSearchPart;

public class ClearSearchHandler {

	@Inject
	IEventBroker broker;

	@Inject
	EModelService model;

	@Execute
	public void execute(MPerspective mPerspective) {
		broker.post(Constants.BROKER_DELETEROWSEARCHTABLE, Constants.SEARCH_PART);
	}

	@CanExecute
	public boolean canExecute(MPart part) {
		return part.getObject() instanceof WFCSearchPart;
	}
}
