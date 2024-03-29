package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.util.DirtyFlagUtil;

public class RevertDetailHandler {

	@Inject
	IEventBroker broker;

	@Inject
	@Optional
	DirtyFlagUtil dirtyFlagUtil;

	@CanExecute
	public boolean canExecute(MPart part) {
		if (part.getObject() instanceof WFCDetailPart) {
			WFCDetailPart detail = (WFCDetailPart) part.getObject();
			return detail.getRequestUtil().getSelectedTable() != null && dirtyFlagUtil.isDirty();
		}
		return false;
	}

	@Execute
	public void execute(@Optional MPerspective perspective) {
		if (perspective == null) {
			return;
		}
		broker.post(Constants.BROKER_REVERTENTRY, perspective);
	}
}
