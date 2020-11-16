package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;

import aero.minova.rcp.rcp.util.Constants;

public class SaveDetailHandler {

	@Inject
	IEventBroker broker;

	/*
	 * @CanExecute public boolean canExecute(MPart mpart) { // TODO
	 * System.out.println("TODO canExecute"); return true; }
	 */

	// Sucht die aktiven Controls aus der XMLDetailPart und baut anhand deren Werte
	// eine Abfrage an den CAS zusammen. Anhand eines gegebenen oder nicht gegebenen
	// KeyLongs wird zwischen update und neuem Eintrag unterschieden
	@Execute
	public void execute(@Optional MPerspective perspective) {
		if (perspective == null) {
			return;
		}
		broker.post(Constants.BROKER_SAVEENTRY, perspective);

	}
}