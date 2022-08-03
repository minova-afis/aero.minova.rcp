
package aero.minova.rcp.rcp.handlers;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class SearchCriteriaSaveHandler extends SeachCriteriaDynamicHandler {

	@AboutToShow
	public void aboutToShow(EModelService service, List<MMenuElement> items, MPart mpart) {
		super.aboutToShow(service, items, mpart, "SAVE_NAME");
	}
}