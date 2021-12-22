
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
	public void execute(IEclipseContext context, Shell shell, @Optional @Named(Constants.CONTROL_GRID_BUTTON_ID) String buttonId,
			@Optional @Named(Constants.CONTROL_GRID_ID) String gridID, MPart part) {

		WFCDetailPart wfcdetailpart = (WFCDetailPart) part.getObject();
		if (wfcdetailpart != null) {
			MGrid mGrid = wfcdetailpart.getDetail().getGrid(gridID);
			GridAccessor gridAccessor = (GridAccessor) mGrid.getGridAccessor();
			SectionGrid sectionGrid = gridAccessor.getSectionGrid();
			if (sectionGrid == null) {
				return;
			}

			if (buttonId.equals(Constants.CONTROL_GRID_BUTTON_INSERT)) {
				sectionGrid.addNewRow();
			} else if (buttonId.equals(Constants.CONTROL_GRID_BUTTON_DELETE)) {
				sectionGrid.deleteCurrentRows();
			} else if (buttonId.equals(Constants.CONTROL_GRID_BUTTON_OPTIMIZEWIDTH)) {
				NatTableUtil.resizeColumns(sectionGrid.getNatTable());
			} else if (buttonId.equals(Constants.CONTROL_GRID_BUTTON_OPTIMIZEHEIGHT)) {
				NatTableUtil.resizeRows(sectionGrid.getNatTable());
				sectionGrid.adjustHeight();
			} else if (buttonId.equals(Constants.CONTROL_GRID_BUTTON_HORIZONTALFILL)) {
				sectionGrid.fillHorizontal();
			}
		}
	}
}