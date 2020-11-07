package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.rcp.parts.XMLIndexPart;

public class ResizeIndexHandler {

	@Inject
	private EModelService model;

	@Execute
	public void execute(MPart mpart, MPerspective mPerspective) {

		List<MPart> findElements = model.findElements(mPerspective, PartsID.INDEX_PART, MPart.class);
		XMLIndexPart indexPart = (XMLIndexPart) findElements.get(0).getObject();
//		NatTable table = indexPart.getNatTable();
//		NatTableUtil.resizeTable(table);

		// TODO update table
	}

}
