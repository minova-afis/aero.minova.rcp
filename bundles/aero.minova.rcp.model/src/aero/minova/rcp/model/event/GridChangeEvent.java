package aero.minova.rcp.model.event;

import java.util.EventObject;

import aero.minova.rcp.constants.GridChangeType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MGrid;

public class GridChangeEvent extends EventObject {

	private static final long serialVersionUID = 202107061438L;

	private final Row row;
	private final int columnIndex;
	private final int rowIndex;
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
		this.row = null;
		this.columnIndex = -1;
		this.rowIndex = -1;
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
	 * @param rowIndex
	 * @param user
	 * @param changeType
	 */
	public GridChangeEvent(MGrid grid, Row row, int rowIndex, boolean user, GridChangeType changeType) {
		super(grid);
		this.row = row;
		this.columnIndex = -1;
		this.rowIndex = rowIndex;
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
	public GridChangeEvent(MGrid grid, Row row, int columnIndex, int rowIndex, Value oldValue, Value newValue, boolean user) {
		super(grid);
		this.row = row;
		this.columnIndex = columnIndex;
		this.rowIndex = rowIndex;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.user = user;
		this.changeType = GridChangeType.UPDATE;
	}

	public MGrid getGrid() {
		return (MGrid) getSource();
	}

	public int getColumn() {
		return columnIndex;
	}

	public int getRow() {
		return rowIndex;
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

	public Row getChangedRow() {
		return row;
	}
}
