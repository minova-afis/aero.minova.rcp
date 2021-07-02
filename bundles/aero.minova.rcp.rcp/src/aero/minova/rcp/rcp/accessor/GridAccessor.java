package aero.minova.rcp.rcp.accessor;

import aero.minova.rcp.rcp.widgets.SectionGrid;

public class GridAccessor implements aero.minova.rcp.model.form.GridAccessor {

	public GridAccessor() {
		super();
	}

	SectionGrid sg;

	public void setSectionGrid(SectionGrid sg) {
		this.sg = sg;
	}

	public SectionGrid getSectionGrid() {
		return sg;
	}

}
