package aero.minova.rcp.model.form;

import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;

import aero.minova.rcp.model.Table;

public interface IGridAccessor {

	Table getSelectedRows();

	void deleteCurrentRows();

	void addRows(Table rows);

	void addSelectionListener(ILayerListener listener);

	void removeSelectionListener(ILayerListener listener);

	Table getDataTable();

	void setDataTable(Table dataTable);

	void closeEditor();

	void setGridRequired(boolean required);

}
