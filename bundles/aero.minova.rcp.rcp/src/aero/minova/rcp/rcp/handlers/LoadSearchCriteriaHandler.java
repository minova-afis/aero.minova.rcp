package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.rcp.parts.XMLSearchPart;

public class LoadSearchCriteriaHandler {
	
	@Execute
	public void execute(MPart mpart, Shell shell) {
		System.out.println("Test");
	}

}
