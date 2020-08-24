package aero.minova.rcp.perspectiveswitcher.handler;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class KeepPerspectiveHandler {

	@Inject
	MApplication application;

	@Inject
	EModelService modelService;

	@Inject
	EPartService partService;

	@Execute
	public void execute(MWindow window) {

		/*
		 * Setzt fest, dass das Toolitem der Perspektive, bei der der Knopf gesetzt
		 * wurde, in der Toolbar behalten wird, auch wenn die eigentliche Perspektive
		 * geschlossen ist. Vergleich das Dock bei MAC.
		 */
		MPerspective currentPerspective = modelService.getActivePerspective(window);
		String perspectiveID = currentPerspective.getElementId();

		MUIElement toolbar = modelService.find("aero.minova.rcp.rcp.toolbar.perspectiveswitchertoolbar", application);
		List<MHandledToolItem> toolitems = modelService.findElements(toolbar,
				"aero.minova.rcp.rcp.handledtoolitem." + perspectiveID, MHandledToolItem.class);
		MHandledToolItem toolitem = (toolitems == null || toolitems.size() == 0) ? null : toolitems.get(0);

		if (toolitem != null)
			toolitem.getTags().add("keepIt");

	}

}
