package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.rcp.parts.WFCSearchPart;

public class SelectSearchPart {

	@Inject
	private EModelService model;

	@Inject
	EPartService partService;

	@Execute
	public void execute(MPerspective mPerspective) {
		List<MPart> findElements = model.findElements(mPerspective, Constants.SEARCH_PART, MPart.class);
		MPart part = findElements.get(0);
		partService.activate(part);
		WFCSearchPart searchPart = (WFCSearchPart) part.getObject();
		NatTable natTable = searchPart.getNatTable();
		SelectionLayer selectionLayer = searchPart.getSelectionLayer();
		natTable.setFocus();
		natTable.doCommand(new SelectCellCommand(selectionLayer, 2, 0, false, false));
		natTable.commitAndCloseActiveCellEditor();
	}
}
