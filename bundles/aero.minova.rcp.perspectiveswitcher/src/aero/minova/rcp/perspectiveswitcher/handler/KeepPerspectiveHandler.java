package aero.minova.rcp.perspectiveswitcher.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

/**
 * Dieser Handler wird im PopupMenu aufgerufen. Toolitems, die mit diesem
 * Handler makiert wurden, werden nach dem schließen der Perspektive nicht aus
 * der Toolbar entfernt. So kann man die Perspektive erneutr aus der Toolbar aus
 * öffnen.
 * 
 * @author bauer
 *
 */

public class KeepPerspectiveHandler {
	
	

	@Inject
	MApplication application;

	@Inject
	EModelService modelService;

	@Inject
	EPartService partService;
	
	Preferences prefs = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_KEPTPERSPECTIVES);

	@SuppressWarnings("unchecked")
	@Execute
	public void execute(MWindow window, @Optional @Named(E4WorkbenchParameterConstants.FORM_NAME) String perspectiveId) {


		String keptPerspective = prefs.get(perspectiveId, "");

		if (keptPerspective.isBlank()) {
			prefs.put(perspectiveId, perspectiveId);
		} else {
			prefs.remove(perspectiveId);
		}
		
		try {
			prefs.flush();
		} catch (BackingStoreException e1) {
			e1.printStackTrace();
		}
	}

}
