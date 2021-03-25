
package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.rcp.util.PersistTableSelection;
import aero.minova.rcp.rcp.util.SaveSearchCriteriaDialog;

public class SaveAsHandler {

	@Inject
	TranslationService translationService;

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Inject
	IEventBroker broker;

	@Execute
	public void execute(Shell shell, MPart part) {
		final SaveSearchCriteriaDialog sscd = new SaveSearchCriteriaDialog(shell, translationService, prefs);
		String name = sscd.open();
		boolean saveColumnWidth = sscd.getSaveWidths();


		IEclipseContext context = part.getContext();
		context.set("SaveRowConfig", saveColumnWidth);// setzen der Konfiguration, verfügbar auch später.
		context.set("Name", name);// setzen der Konfiguration, verfügbar auch später.
		// TODO DIalog zum Speichern der Suchkriterien
		ContextInjectionFactory.invoke(part.getObject(), PersistTableSelection.class, context);

		// SaveCriteria sc = new SaveCriteria(name, saveColumnWidth);
		// broker.post(Constants.BROKER_SAVEASSEARCHCRITERIA, sc);

	}

}