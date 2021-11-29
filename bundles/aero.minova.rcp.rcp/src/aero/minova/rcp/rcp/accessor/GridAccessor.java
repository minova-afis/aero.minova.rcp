package aero.minova.rcp.rcp.accessor;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.IGridValidator;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.rcp.widgets.SectionGrid;

public class GridAccessor implements aero.minova.rcp.model.form.IGridAccessor {

	SectionGrid sg;
	MGrid mgrid;

	public GridAccessor(MGrid mgrid) {
		super();
		this.mgrid = mgrid;
	}

	public void setSectionGrid(SectionGrid sg) {
		this.sg = sg;
		sg.setGridAccessor(this);
	}

	public SectionGrid getSectionGrid() {
		return sg;
	}

	public MGrid getMGrid() {
		return mgrid;
	}

	@Override
	public Table getSelectedRows() {
		return sg.getSelectedRows();
	}

	@Override
	public void deleteCurrentRows() {
		sg.deleteCurrentRows();
	}

	@Override
	public Row addRow() {
		return sg.addNewRow();
	}

	@Override
	public void addRows(Table rows) {
		sg.addRows(rows);
	}

	@Override
	public void addSelectionListener(ILayerListener listener) {
		sg.addSelectionListener(listener);
	}

	@Override
	public void removeSelectionListener(ILayerListener listener) {
		sg.removeSelectionListener(listener);
	}

	@Override
	public Table getDataTable() {
		return sg.getDataTable();
	}

	@Override
	public void setDataTable(Table dataTable) {
		sg.setDataTable(dataTable);
	}

	@Override
	public void closeEditor() {
		sg.closeEditor();
	}

	@Override
	public void resetReadOnlyAndRequiredColumns() {
		sg.resetReadOnlyAndRequiredColumns();
	}

	@Override
	public void setColumnRequired(int columnIndex, boolean required) {
		sg.setColumnRequired(columnIndex, required);
	}

	@Override
	public void setGridRequired(boolean required) {
		sg.setGridRequired(required);
	}

	@Override
	public void setColumnReadOnly(int columnIndex, boolean readOnly) {
		sg.setColumnReadOnly(columnIndex, readOnly);
	}

	@Override
	public void setGridReadOnly(boolean readOnly) {
		sg.setGridReadOnly(readOnly);
	}

	@Override
	public void addValidation(IGridValidator validator, List<Integer> columnsToValidate) {
		sg.addValidation(validator, columnsToValidate);
	}

	@Override
	public void clearGrid() {
		sg.clearGrid();
	}
}
