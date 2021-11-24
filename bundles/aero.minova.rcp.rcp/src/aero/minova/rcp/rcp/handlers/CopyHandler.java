package aero.minova.rcp.rcp.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.rcp.accessor.GridAccessor;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.widgets.SectionGrid;

public class CopyHandler {

	@Inject
	IEventBroker broker;

	private WFCDetailPart detail;

	/**
	 * Button entsprechend der Maske anzeigen
	 * 
	 * @param part
	 * @return
	 */
	@Evaluate
	public boolean visible(MPart part) {
		detail = (WFCDetailPart) part.getObject();
		return detail.getForm().getDetail().isButtonCopyVisible();
	}

	@CanExecute
	public boolean canExecute(MPart part, @Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object selection) {
		detail = (WFCDetailPart) part.getObject();
		return detail.getDetail().getField("KeyLong").getValue() != null;
	}

	@Execute
	public void execute() {

		detail.getDetail().getField("KeyLong").setValue(null, false);
		detail.getRequestUtil().setKeys(null);

		// Die Zeilen der Grids kopieren/neu hinzufügen. Dabei werden nur die sichtbaren Felder übernommen, um Duplikate bei Keys zu vermeiden
		for (MGrid g : detail.getDetail().getGrids()) {
			SectionGrid sg = ((GridAccessor) g.getGridAccessor()).getSectionGrid();

			// Zeilen neu erstellen
			List<Row> newRows = new ArrayList<>();
			for (Row r : sg.getDataTable().getRows()) {
				Row newRow = new Row();
				for (Column c : sg.getDataTable().getColumns()) {
					if (c.isVisible()) {
						newRow.addValue(sg.getDataTable().getValue(c.getName(), r));
					} else {
						newRow.addValue(null);
					}
				}
				newRows.add(newRow);
			}

			// Neue Tabelle aufbauen
			Table newTable = new Table();
			newTable.setName(sg.getDataTable().getName());
			newTable.addColumns(sg.getDataTable().getColumns());
			newTable.addRows(newRows);

			// Tabelle in Grid setzten
			sg.getDataTable().getRows().clear();
			sg.addRows(newTable);
		}

		broker.post(Constants.BROKER_SHOWNOTIFICATION, "msg.CopySuccessful");
	}
}
