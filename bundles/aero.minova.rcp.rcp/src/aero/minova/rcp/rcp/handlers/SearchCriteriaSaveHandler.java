
package aero.minova.rcp.rcp.handlers;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class SearchCriteriaSaveHandler {

	@AboutToShow
	public void aboutToShow(EModelService service, List<MMenuElement> items) {
		MDirectMenuItem createModelElement = service.createModelElement(MDirectMenuItem.class);
		createModelElement.setLabel("Test");
		// Handler der aufgerufen werden soll, wenn wir auf den Button drücken
		createModelElement.setContributionURI("bundleclass://aero.minova.rcp.rcp/aero.minova.rcp.rcp.handlers.SearchCriteriaSaveHandler");
		createModelElement.getPersistedState().put("persistState", "false");
		items.add(createModelElement);
	}

}