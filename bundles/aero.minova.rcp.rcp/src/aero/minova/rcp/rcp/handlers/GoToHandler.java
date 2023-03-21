package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.parts.WFCIndexPart;

public class GoToHandler {

	private static final String COMMAND_PARAMETER = "aero.minova.rcp.rcp.commandparameter.gotofield";

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private EModelService eModelService;

	/**
	 * Buttons nur anzeigen, wenn es sich um Booking-Toolbar handelt
	 * 
	 * @param part
	 * @return
	 */
	@Evaluate
	public boolean visible(MPart part) {
		if (part.getObject() != null) {
			return ((WFCDetailPart) part.getObject()).getDetail().isBooking();
		}
		return false;
	}

	@CanExecute
	public boolean canExecute(MPart part, @Named(COMMAND_PARAMETER) String fieldName) {
		return ((WFCDetailPart) part.getObject()).getDetail().getField(fieldName).getValue() != null;
	}

	@Execute
	public void execute(MPerspective mPerspective, MPart part, @Named(COMMAND_PARAMETER) String fieldName) {
		WFCDetailPart detailPart = (WFCDetailPart) part.getObject();
		WFCIndexPart indexPart = (WFCIndexPart) eModelService.findElements(mPerspective, Constants.INDEX_PART, MPart.class).get(0).getObject();

		Table table = dataFormService.getTableFromFormIndex(indexPart.getForm());
		Row row = RowBuilder.newRow().create();
		for (Column column : table.getColumns()) {
			if (column.getName().equals(Constants.TABLE_KEYLONG)) {
				row.addValue(detailPart.getDetail().getField(fieldName).getValue());
			} else {
				row.addValue(null);
			}
		}
		table.addRow(row);

		// READ-Prozedur ausführen um Detail zu füllen
		detailPart.getRequestUtil().readData(table);
	}
}
