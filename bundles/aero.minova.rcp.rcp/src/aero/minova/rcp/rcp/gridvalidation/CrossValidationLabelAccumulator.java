package aero.minova.rcp.rcp.gridvalidation;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.form.IGridValidator;

/**
 * {@link IConfigLabelAccumulator} that adds labels to the columns that show date values and labels to indicate invalid data.
 */
public class CrossValidationLabelAccumulator extends ColumnLabelAccumulator {

	private List<Integer> columnsToValidate;
	private IGridValidator validator;

	ILayer layer;

	public CrossValidationLabelAccumulator(ILayer layer, IGridValidator validator, List<Integer> columnsToValidate) {
		this.validator = validator;
		this.columnsToValidate = columnsToValidate;
		this.layer = layer;
	}

	@Override
	public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
		int columnIndex = layer.getColumnIndexByPosition(columnPosition);
		int rowIndex = layer.getRowIndexByPosition(rowPosition);
		super.accumulateConfigLabels(configLabels, columnIndex, rowIndex);

		// Angegebenen Spalten überprüfen
		if (columnsToValidate.contains(columnIndex)) {
			configLabels.addLabel(Constants.VALIDATION_CELL_LABEL);

			if (!validator.checkValid(columnIndex, rowIndex)) {
				System.out.println(columnIndex + " " + rowIndex + " Label " + columnPosition + " " + rowPosition + " "
						+ layer.getDataValueByPosition(columnPosition, rowPosition));
				configLabels.addLabel(Constants.INVALID_CELL_LABEL);
			}
		}
	}
}
