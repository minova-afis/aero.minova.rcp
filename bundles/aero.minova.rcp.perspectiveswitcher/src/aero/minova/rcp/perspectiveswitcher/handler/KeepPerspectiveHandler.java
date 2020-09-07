package aero.minova.rcp.perspectiveswitcher.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

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

	@SuppressWarnings("unchecked")
	@Execute
	public void execute(MWindow window, @Optional @Named(E4WorkbenchParameterConstants.COMMAND_PERSPECTIVE_ID) String perspectiveId) {

		List<String> keepItToolitems;

		keepItToolitems = (List<String>) application.getContext().get("perspectivetoolbar");
		
		if (keepItToolitems == null) {
			keepItToolitems = new ArrayList<String>();
			application.getContext().set("perspectivetoolbar", keepItToolitems);
		}

		if (keepItToolitems.contains(perspectiveId)) {
			keepItToolitems.remove(perspectiveId);
		} else {
			keepItToolitems.add(perspectiveId);
		}

	}

}
