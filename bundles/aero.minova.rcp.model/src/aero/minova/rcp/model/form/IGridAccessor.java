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

	MGrid getMGrid();

	void setGridRequired(boolean required);

	void resetReadOnlyAndRequiredColumns();

	void setColumnRequired(int columnIndex, boolean required);

	void setColumnReadOnly(int columnIndex, boolean readOnly);

	void setGridReadOnly(boolean readOnly);

	/**
	 * Es gibt keine getSectionGrid Methode im IGridAccessor, da es zu Cycle Fehlern führen würde. Das Project .rcp.model ist bereits als Dependencie in
	 * .rcp.rcp eingebunden. Für das SectionGrid müssten wir .rcp.rcp als Dependencie im .model.rcp einbinden, dies würde zu den erwähnten Cycle Fehlern führen.
	 */

}
