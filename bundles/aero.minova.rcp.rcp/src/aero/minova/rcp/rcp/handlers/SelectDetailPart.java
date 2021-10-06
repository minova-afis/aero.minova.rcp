package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
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
		WFCDetailPart detailPart = (WFCDetailPart) part.getObject();
		MField field = detailPart.getDetail().getMSectionList().get(0).getTabList().get(0);
		((AbstractValueAccessor) field.getValueAccessor()).getControl().setFocus();
	}
}
