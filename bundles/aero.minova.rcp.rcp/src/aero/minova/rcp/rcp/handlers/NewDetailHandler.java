package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.rcp.parts.WFCDetailPart;

public class NewDetailHandler {

	@Inject
	EModelService model;
	@Inject
	private IEventBroker broker;
	
	/**
	 * Button entsprechend der Maske anzeigen
	 * 
	 * @param part
	 * @return
	 */
	@Evaluate
	public boolean visible(MPart part) {
		WFCDetailPart detail = (WFCDetailPart) part.getObject();
		return detail != null && detail.getForm(false) != null && detail.getForm(false).getDetail() != null
				&& detail.getForm(false).getDetail().isButtonDeleteVisible();
	}

	@Execute
	public void execute(MPart mpart, @Optional MPerspective perspective) {
		if (perspective == null) {
			return;
		}

		broker.post(Constants.BROKER_NEWENTRY, Constants.CLEAR_REQUEST);
	}
}
