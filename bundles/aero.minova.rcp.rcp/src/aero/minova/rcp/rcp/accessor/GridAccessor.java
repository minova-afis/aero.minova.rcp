package aero.minova.rcp.rcp.accessor;

import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;

import aero.minova.rcp.model.Table;
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
	public void setGridRequired(boolean required) {
		sg.setGridRequired(required);
	}
}
