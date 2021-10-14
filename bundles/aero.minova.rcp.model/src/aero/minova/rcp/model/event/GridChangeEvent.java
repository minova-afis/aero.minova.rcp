package aero.minova.rcp.model.event;

import java.util.EventObject;

import aero.minova.rcp.constants.GridChangeType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MGrid;

public class GridChangeEvent extends EventObject {

	private static final long serialVersionUID = 202107061438L;

	private final MGrid grid;
	private final int column;
	private final int row;
	private final Value oldValue;
	private final Value newValue;
	private final boolean user;
	private final GridChangeType changeType;

	/**
	 * Für komplette Änderung an der Tabelle
	 * 
	 * @param grid
	 * @param user
	 */
	public GridChangeEvent(MGrid grid, boolean user) {
		super(grid);
		this.grid = grid;
		this.column = -1;
		this.row = -1;
		this.oldValue = null;
		this.newValue = null;
		this.user = user;
		this.changeType = GridChangeType.RESET;
	}

	/**
	 * NUR für INSERT/DELETE von Zeilen
	 * 
	 * @param grid
	 * @param row
	 * @param user
	 * @param changeType
	 */
	public GridChangeEvent(MGrid grid, int row, boolean user, GridChangeType changeType) {

		super(grid);
		this.grid = grid;
		this.column = -1;
		this.row = row;
		this.oldValue = null;
		this.newValue = null;
		this.user = user;
		this.changeType = changeType;
	}

	/**
	 * Für Änderung an einzelnen Werten
	 * 
	 * @param grid
	 * @param column
	 * @param row
	 * @param oldValue
	 * @param newValue
	 * @param user
	 */
	public GridChangeEvent(MGrid grid, int column, int row, Value oldValue, Value newValue, boolean user) {
		super(grid);
		this.grid = grid;
		this.column = column;
		this.row = row;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.user = user;
		this.changeType = GridChangeType.UPDATE;
	}

	public MGrid getGrid() {
		return grid;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public Value getOldValue() {
		return oldValue;
	}

	public Value getNewValue() {
		return newValue;
	}

	public boolean isUser() {
		return user;
	}

	public GridChangeType getChangeType() {
		return changeType;
	}
}
