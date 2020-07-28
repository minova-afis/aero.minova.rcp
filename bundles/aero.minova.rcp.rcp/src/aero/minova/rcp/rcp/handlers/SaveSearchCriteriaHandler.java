package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import aero.minova.rcp.rcp.util.PersistTableSelection;

public class SaveSearchCriteriaHandler {

	@Execute
	public void execute(MPart part) {
		System.out.println("Save Search Criteria NatTable Search Part");
		IEclipseContext context = part.getContext();
		context.set("SpaltenKonfiguration", true);// setzen der Konfiguration, verfügbar auch später.
		//TODO DIalog zum Speichern der Suchkriterien
		ContextInjectionFactory.invoke(part.getObject(), PersistTableSelection.class, context);
	}

}
