package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import aero.minova.rcp.rcp.parts.WFCDetailPart;

public class AnnounceHandler {

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
				&& detail.getForm(false).getDetail().isButtonAnnounceVisible();
	}

	@CanExecute
	public boolean canExecute(MPart part) {
		WFCDetailPart detail = (WFCDetailPart) part.getObject();
		return !detail.getDetail().getField("IsBooked").getValue().getBooleanValue();
	}

	@Execute
	public void execute() {
		// TODO
	}

}
