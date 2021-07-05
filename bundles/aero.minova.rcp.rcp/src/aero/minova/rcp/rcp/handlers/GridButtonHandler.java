
package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.rcp.accessor.GridAccessor;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.rcp.widgets.SectionGrid;

public class GridButtonHandler {

	@Inject
	protected TranslationService translationService;

	@Execute
	public void execute(IEclipseContext context, Shell shell, @Optional @Named(Constants.CONTROL_BUTTON) String buttonId,
			@Optional @Named(Constants.CONTROL_GRID_PROCEDURE_SUFFIX) String gridPS, MPart part) {
		SectionGrid sectionGrid = null;

		WFCDetailPart wfcdetailpart = (WFCDetailPart) part.getObject();
		if (wfcdetailpart != null) {
			MGrid mGrid = wfcdetailpart.getDetail().getGrid(gridPS);
			GridAccessor valueAccessor = (GridAccessor) mGrid.getValueAccessor();
			sectionGrid = valueAccessor.getSectionGrid();
		}
		if (sectionGrid == null) {
			return;
		}
		if (buttonId.equals("OptimizeWidth")) {
			NatTableUtil.resizeColumns(sectionGrid.getNatTable());
		}
		if (buttonId.equals("OptimizeHigh")) {
			NatTableUtil.resizeRows(sectionGrid.getNatTable());
			sectionGrid.AjustCorrectHigh();
		}
		if (buttonId.equals("Insert")) {
			sectionGrid.addNewRow();
		}
	}
}