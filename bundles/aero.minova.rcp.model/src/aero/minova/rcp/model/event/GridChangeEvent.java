package aero.minova.rcp.model.event;

import java.util.EventObject;

import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MGrid;

public class GridChangeEvent extends EventObject {

	private static final long serialVersionUID = 202107061438L;
	private MGrid grid;
	private Table newTable;

	public GridChangeEvent(MGrid grid, Table newTable) {
		super(grid);
		this.grid = grid;
		this.newTable = newTable;
	}

	public MGrid getGrid() {
		return grid;
	}

	public Table getNewTable() {
		return newTable;
	}
}
