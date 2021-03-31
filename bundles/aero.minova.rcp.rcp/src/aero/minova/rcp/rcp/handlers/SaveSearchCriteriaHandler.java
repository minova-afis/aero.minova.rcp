package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.rcp.util.PersistTableSelection;

public class SaveSearchCriteriaHandler {

	@Inject
	IEventBroker broker;

	@Execute
	public void execute(MPart part) {
		System.out.println("Save Search Criteria NatTable Search Part");
		IEclipseContext context = part.getContext();
		context.set("SaveRowConfig", true);// setzen der Konfiguration, verfügbar auch später.
		context.set("ConfigName", "DEFAULT");
		// TODO DIalog zum Speichern der Suchkriterien
		ContextInjectionFactory.invoke(part.getObject(), PersistTableSelection.class, context);
		broker.send(Constants.BROKER_SAVESEARCHCRITERIA, "DEFAULT");
	}

}
