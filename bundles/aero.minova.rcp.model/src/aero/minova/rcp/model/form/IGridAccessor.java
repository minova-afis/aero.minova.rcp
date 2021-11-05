package aero.minova.rcp.model.form;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;

public interface IGridAccessor {

	Table getSelectedRows();

	void deleteCurrentRows();

	Row addRow();

	void addRows(Table rows);

	void addSelectionListener(ILayerListener listener);

	void removeSelectionListener(ILayerListener listener);

	Table getDataTable();

	void setDataTable(Table dataTable);

	void closeEditor();

	void setGridRequired(boolean required);

	void resetReadOnlyAndRequiredColumns();

	void setColumnRequired(int columnIndex, boolean required);

	void setColumnReadOnly(int columnIndex, boolean readOnly);

	void setGridReadOnly(boolean readOnly);

	void addValidation(IGridValidator validator, List<Integer> columnsToValidate);
}
