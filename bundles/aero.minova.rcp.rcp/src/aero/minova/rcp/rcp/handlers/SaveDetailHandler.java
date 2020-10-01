package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import aero.minova.rcp.rcp.parts.XMLDetailPart;

public class SaveDetailHandler {

	@CanExecute
	public boolean canExecute(MPart mpart) {
		// TODO
		System.out.println("TODO canExecute");
		return true;
	}

	// Sucht die aktiven Controls aus der XMLDetailPart und baut anhand deren Werte
	// eine Abfrage an den CAS zusammen. Anhand eines gegebenen oder nicht gegebenen
	// KeyLongs wird zwischen update und neuem Eintrag unterschieden
	@Execute
	public void execute(MPart mpart) {
		XMLDetailPart xmlPart = (XMLDetailPart) mpart;
		xmlPart.buildSaveTable();

	}
}