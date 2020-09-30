package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.model.Table;

public class ClearSearchHandler {

	@Inject
	EModelService model;

	@Execute
	public void execute(MPerspective mPerspective) {
		List<MPart> findElements = model.findElements(mPerspective, PartsID.SEARCH_PART, MPart.class);
		Table table = (Table) findElements.get(0).getContext().get("NatTableDataSearchArea");
		table.getRows().clear();
		table.addRow();
	}

}
