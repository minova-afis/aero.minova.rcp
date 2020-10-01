package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import aero.minova.rcp.rcp.parts.XMLDetailPart;

public class DeleteDetailHandler {

	@Execute
	public void execute(MPart mpart) {
		XMLDetailPart xmlPart = (XMLDetailPart) mpart;
		xmlPart.buildDeleteTable();

	}
}
