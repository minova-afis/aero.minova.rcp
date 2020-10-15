package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

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
	public void execute() {
		broker.post("SaveEntry", null);

	}
}