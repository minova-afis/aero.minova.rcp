package aero.minova.rcp.rcp.gridvalidation;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.IGridValidator;

/**
 * {@link IConfigLabelAccumulator} that adds labels to the columns that show date values and labels to indicate invalid data.
 */
public class CrossValidationLabelAccumulator extends ColumnLabelAccumulator {

	private List<Integer> columnsToValidate;
	private IGridValidator validator;

	ILayer layer;
	private List<Row> sortedList;
	private Table dataTable;

	public CrossValidationLabelAccumulator(ILayer layer, IGridValidator validator, List<Integer> columnsToValidate, List<Row> sortedList, Table dataTable) {
		this.validator = validator;
		this.columnsToValidate = columnsToValidate;
		this.layer = layer;
		this.sortedList = sortedList;
		this.dataTable = dataTable;
	}

	@Override
	public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
		super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);

		// Da die Tabelle nicht sortiert wird müssen wir den entsprechenden Index herausfinden
		int rowIndexInDataTable = dataTable.getRows().indexOf(sortedList.get(rowPosition));

		// Angegebenen Spalten überprüfen
		if (columnsToValidate.contains(columnPosition)) {
			configLabels.addLabel(Constants.VALIDATION_CELL_LABEL);
			if (!validator.checkValid(columnPosition, rowIndexInDataTable)) {
				configLabels.addLabel(Constants.INVALID_CELL_LABEL);
			}
		}
	}
}
