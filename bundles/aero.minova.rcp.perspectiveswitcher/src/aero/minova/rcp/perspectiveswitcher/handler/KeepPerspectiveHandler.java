package aero.minova.rcp.perspectiveswitcher.handler;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

/**
 * Dieser Handler wird im PopupMenu aufgerufen. Toolitems, die mit diesem Handler makiert wurden, werden nach dem schließen der Perspektive nicht aus der
 * Toolbar entfernt. So kann man die Perspektive erneutr aus der Toolbar aus öffnen.
 * 
 * @author bauer
 */

public class KeepPerspectiveHandler {

	@Inject
	MApplication application;

	@Inject
	EModelService modelService;

	@Inject
	EPartService partService;

	ILog logger = Platform.getLog(this.getClass());

	Preferences prefs = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_KEPTPERSPECTIVES);

	@Execute
	public void execute(MWindow window, @Optional @Named(E4WorkbenchParameterConstants.FORM_NAME) String perspectiveId) {

		String keptPerspective = prefs.get(perspectiveId + Constants.KEPT_PERSPECTIVE_FORMNAME, "");

		if (keptPerspective.isBlank()) {
			List<MPerspective> perspectives = modelService.findElements(window, perspectiveId, MPerspective.class);
			MPerspective perspective = perspectives.get(0);

			prefs.put(perspectiveId + Constants.KEPT_PERSPECTIVE_FORMID, perspective.getElementId() == null ? "" : perspective.getElementId());
			prefs.put(perspectiveId + Constants.KEPT_PERSPECTIVE_FORMNAME,
					perspective.getPersistedState().get(Constants.FORM_NAME) == null ? "" : perspective.getPersistedState().get(Constants.FORM_NAME));
			prefs.put(perspectiveId + Constants.KEPT_PERSPECTIVE_FORMLABEL, perspective.getLabel() == null ? "" : perspective.getLabel());
			prefs.put(perspectiveId + Constants.KEPT_PERSPECTIVE_ICONURI, perspective.getIconURI() == null ? "" : perspective.getIconURI());
			prefs.put(perspectiveId + Constants.KEPT_PERSPECTIVE_LOCALIZEDLABEL,
					perspective.getLocalizedLabel() == null ? "" : perspective.getLocalizedLabel());
			prefs.put(perspectiveId + Constants.KEPT_PERSPECTIVE_LOCALIZEDTOOLTIP,
					perspective.getLocalizedTooltip() == null ? "" : perspective.getLocalizedTooltip());
		} else {
			prefs.remove(perspectiveId + Constants.KEPT_PERSPECTIVE_FORMID);
			prefs.remove(perspectiveId + Constants.KEPT_PERSPECTIVE_FORMNAME);
			prefs.remove(perspectiveId + Constants.KEPT_PERSPECTIVE_FORMLABEL);
			prefs.remove(perspectiveId + Constants.KEPT_PERSPECTIVE_ICONURI);
			prefs.remove(perspectiveId + Constants.KEPT_PERSPECTIVE_LOCALIZEDLABEL);
			prefs.remove(perspectiveId + Constants.KEPT_PERSPECTIVE_LOCALIZEDTOOLTIP);
		}

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
