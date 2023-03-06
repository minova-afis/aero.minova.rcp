package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.rcp.parts.WFCDetailPart;

public class SelectDetailPart {

	@Inject
	private EModelService model;

	@Inject
	private EPartService partService;

	@Execute
	public void execute(MPerspective mPerspective) {
		List<MPart> findElements = model.findElements(mPerspective, Constants.DETAIL_PART, MPart.class);
		MPart part = findElements.get(0);
		partService.activate(part);
		((WFCDetailPart) part.getObject()).getRequestUtil().focusFirstEmptyField();
	}
}
