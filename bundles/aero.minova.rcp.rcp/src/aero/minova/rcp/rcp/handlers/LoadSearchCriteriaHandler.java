package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import aero.minova.rcp.rcp.util.LoadTableSelection;

public class LoadSearchCriteriaHandler {

	@Execute
	public void execute(@Optional MPerspective perspective, @Optional MPart part) {
		System.out.println("Load Search Criteria NatTable Search Part");
		if (perspective == null) {
			return;
		}
		IEclipseContext context = part.getContext();
		context.set("ConfigName", "DEFAULT");
		ContextInjectionFactory.invoke(part.getObject(), LoadTableSelection.class, context);
	}

}
