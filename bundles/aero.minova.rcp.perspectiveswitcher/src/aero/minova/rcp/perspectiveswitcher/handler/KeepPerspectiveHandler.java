package aero.minova.rcp.perspectiveswitcher.handler;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
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
	public void execute() {
		
		MUIElement toolbar = modelService.find("aero.minova.rcp.rcp.toolbar.perspectiveswitchertoolbar", application);
		List<MHandledToolItem> keepPerspectives = modelService.findElements(toolbar, "aero.minova.rcp.rcp.handledtoolitem.keepperspective", MHandledToolItem.class);
		MHandledToolItem keepPerspectiveItem = keepPerspectives.get(0);
		MUIElement closeToolbar = modelService.find("aero.minova.rcp.rcp.toolbar.close", application);
		List<MHandledToolItem> closeItems = modelService.findElements(closeToolbar, "aero.minova.rcp.rcp.handledtoolitem.closeperspective", MHandledToolItem.class);
		MHandledToolItem closeItem = closeItems.get(0);
		
		if(keepPerspectiveItem.isSelected()) {
			closeItem.setEnabled(false);
		} else {
			closeItem.setEnabled(true);
		}
		
		
	}

}
