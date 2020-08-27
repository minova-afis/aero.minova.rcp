package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class NewDetailHandler {
	@Execute
	public void execute(MPart mpart) {
		System.out.print("Create a new Detail");
	}
}
