package aero.minova.rcp.rcp.accessor;

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
}
