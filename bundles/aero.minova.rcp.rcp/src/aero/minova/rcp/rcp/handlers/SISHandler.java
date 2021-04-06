
package aero.minova.rcp.rcp.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import aero.minova.rcp.perspectiveswitcher.handler.SwitchPerspectiveHandler;

public class SISHandler extends SwitchPerspectiveHandler {

	@Inject
	EModelService modelService;

	@Execute
	public void execute(MWindow window, MApplication app) throws InvocationTargetException, InterruptedException {

		execute(window.getContext(), "aero.minova.rcp.rcp.perspective.sis", "org.eclipse.e4.ui.perspectives.parameters.newWindow", null, window);
	}

}