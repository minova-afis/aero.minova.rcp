package aero.minova.rcp.rcp.accessor;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;

import aero.minova.rcp.model.Row;
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
	public void addRows(List<Row> rows) {
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
}
