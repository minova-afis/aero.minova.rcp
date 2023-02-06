
package aero.minova.rcp.rcp.handlers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.FilterValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.parts.WFCIndexPart;
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;

public class LoadFromMatchcode {

	@Inject
	private EModelService model;

	@Inject
	private EPartService partService;

	@Inject
	private IDataService dataService;

	@Inject
	private IDataFormService dataFormService;

	@Execute
	public void execute(MPerspective mPerspective) throws InterruptedException, ExecutionException {
		MField field = null;
		String indexViewName;

		// Wir holen uns den DetailPart
		List<MPart> findElements = model.findElements(mPerspective, Constants.DETAIL_PART, MPart.class);
		MPart part = findElements.get(0);
		partService.activate(part);
		WFCDetailPart detailPart = (WFCDetailPart) part.getObject();
		indexViewName = detailPart.getForm().getIndexView().getSource();

		// Wir holen uns den IndexPart
		findElements = model.findElements(mPerspective, Constants.INDEX_PART, MPart.class);
		part = findElements.get(0);
		partService.activate(part);
		WFCIndexPart indexPart = (WFCIndexPart) part.getObject();

		// Matchcode Feld ermitteln
		for (MField field2 : detailPart.getDetail().getFields()) {
			if (field2.getName().equals(Constants.TABLE_KEYTEXT)) {
				field = field2;
			}
		}

		if (field != null && field.getValue() != null) {
			((AbstractValueAccessor) field.getValueAccessor()).getControl().setFocus();
			Table table = dataFormService.getTableFromFormIndex(indexPart.getForm());
			Row row = RowBuilder.newRow().create();
			for (Column column : table.getColumns()) {
				if (column.getName().equals(Constants.TABLE_KEYTEXT)) {
					row.addValue(field.getValue());
				} else {
					row.addValue(null);
				}
			}
			table.addRow(row);

			// IndexView aufrufen
			Table tableFuture = dataService.getTableAsync(table).get();
			table.clearRows();
			Row row2 = RowBuilder.newRow().create();
			for (Column column : table.getColumns()) {
				if (column.isKey()) {
					row2.addValue(tableFuture.getValue(column.getName(), 0));
				} else {
					row2.addValue(null);
				}
			}
			table.addRow(row2);

			// READ-Prozedur ausführen um Detail zu füllen
			detailPart.getRequestUtil().readData(table);
		}
	}

}