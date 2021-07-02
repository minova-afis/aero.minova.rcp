
package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;

public class GridButtonHandler {

	@Inject
	protected TranslationService translationService;

	@Execute
	public void execute(IEclipseContext context, Shell shell, @Optional @Named(Constants.CONTROL_BUTTON) String className, MPart part) {
		System.out.println("ich bin da " + className);
	}
}