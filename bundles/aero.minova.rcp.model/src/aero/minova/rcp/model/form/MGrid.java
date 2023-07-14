package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.swt.graphics.Image;

import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.event.GridChangeEvent;
import aero.minova.rcp.model.event.GridChangeListener;

public class MGrid {

	public MGrid(String id) {
		this.id = id;
	}

	private String title;
	private String id;
	private String procedureSuffix;
	private String procedurePrefix;
	private String helperClass;
	private Image icon;
	private IGridAccessor gridAccessor;
	private boolean delReqAllParams;
	private boolean executeAlways;
	private String fill;
	private Grid grid;
	private List<MField> fields;
	private MSection mSection;
	private ArrayList<GridChangeListener> listeners;

	private IGridValidator validator;
	private List<Integer> columnsToValidate;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProcedureSuffix() {
		return procedureSuffix;
	}

	public void setProcedureSuffix(String procedureSuffix) {
		this.procedureSuffix = procedureSuffix;
	}

	public String getProcedurePrefix() {
		return procedurePrefix;
	}

	public void setProcedurePrefix(String procedurePrefix) {
		this.procedurePrefix = procedurePrefix;
	}

	public String getHelperClass() {
		return helperClass;
	}

	public void setHelperClass(String helperClass) {
		this.helperClass = helperClass;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public IGridAccessor getGridAccessor() {
		return gridAccessor;
	}

	public void setGridAccessor(IGridAccessor gridAccessor) {
		this.gridAccessor = gridAccessor;
	}

	public boolean isDelReqAllParams() {
		return delReqAllParams;
	}

	public void setDelReqAllParams(boolean delReqAllParams) {
		this.delReqAllParams = delReqAllParams;
	}

	public String getFill() {
		return fill;
	}

	public void setFill(String fill) {
		this.fill = fill;
	}

	public Table getDataTable() {
		return gridAccessor.getDataTable();
	}

	public void setDataTable(Table dataTable) {
		gridAccessor.setDataTable(dataTable);
	}

	public MSection getmSection() {
		return mSection;
	}

	public void setmSection(MSection mSection) {
		this.mSection = mSection;
	}

	public List<MField> getFields() {
		return fields;
	}

	public void setFields(List<MField> fields) {
		this.fields = fields;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public Grid getGrid() {
		return this.grid;
	}

	public boolean isValid() {
		Table dataTable = gridAccessor.getDataTable();
		for (Column c : dataTable.getColumns()) {

			if (validator != null && columnsToValidate.contains(dataTable.getColumns().indexOf(c))) {
				for (Row r : dataTable.getRows()) {
					if (!validator.checkValid(dataTable.getColumns().indexOf(c), dataTable.getRows().indexOf(r))) {
						return false;
					}
				}
			}

			if (c.isRequired()) {
				// TODO: Weitere Eigenschaften prüfen? (Textlänge, ...)
				for (Row r : dataTable.getRows()) {
					if (r.getValue(dataTable.getColumns().indexOf(c)) == null || r.getValue(dataTable.getColumns().indexOf(c)).getValue() == null) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/*
	 * Diese Methode muss aufgerufen werden, wenn sich an der unterliegenden Tabelle etwas geändert hat, damit die GridChangedEvents verschickt werden
	 */
	public void dataTableChanged(GridChangeEvent event) {
		fire(event);
	}

	/**
	 * Mit dieser Methode kann man einen Listener für Wertänderungen anhängen.
	 *
	 * @param listener
	 */
	public void addGridChangeListener(GridChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (listeners == null) {
			listeners = new ArrayList<>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Mit dieser Methode kann man einen Listener für Wertänderungen entfernen.
	 *
	 * @param listener
	 */
	public void removeGridChangeListener(GridChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (listeners == null) {
			return;
		}
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	protected void fire(GridChangeEvent event) {
		if (listeners == null) {
			return;
		}
		for (GridChangeListener listener : listeners) {
			listener.gridChange(event);
		}
	}

	public Table getSelectedRows() {
		return gridAccessor.getSelectedRows();
	}

	public void deleteCurrentRows() {
		gridAccessor.deleteCurrentRows();
	}

	public Row addRow() {
		return gridAccessor.addRow();
	}

	public void addRows(Table rows) {
		gridAccessor.addRows(rows);
	}

	public void clearGrid() {
		gridAccessor.clearGrid();
	}

	public void addSelectionListener(ILayerListener listener) {
		gridAccessor.addSelectionListener(listener);
	}

	public void removeSelectionListener(ILayerListener listener) {
		gridAccessor.removeSelectionListener(listener);
	}

	public void closeEditor() {
		gridAccessor.closeEditor();
	}

	/**
	 * Setzt alle Spalten auf ihren ursprünglichen read-only und required Zustand zurück
	 */
	public void resetReadOnlyAndRequiredColumns() {
		gridAccessor.resetReadOnlyAndRequiredColumns();
	}

	public void setColumnRequired(int columnIndex, boolean required) {
		gridAccessor.setColumnRequired(columnIndex, required);
	}

	public void setGridRequired(boolean required) {
		gridAccessor.setGridRequired(required);
	}

	public void setColumnReadOnly(int columnIndex, boolean readOnly) {
		gridAccessor.setColumnReadOnly(columnIndex, readOnly);
	}

	public void setGridReadOnly(boolean readOnly) {
		gridAccessor.setGridReadOnly(readOnly);
	}

	/**
	 * Fügt Validierung zum Grid hinzu, über die Methoden des IGridValidator. Nur die Spalten in columnsToValidate werden überprüft
	 * 
	 * @param validator
	 * @param columnsToValidate
	 */
	public void addValidation(IGridValidator validator, List<Integer> columnsToValidate) {
		this.validator = validator;
		this.columnsToValidate = columnsToValidate;
		gridAccessor.addValidation(validator, columnsToValidate);
	}

	public void setValue(int columnIndex, int rowIndex, Value newValue) {
		GridChangeEvent gridChangeEvent = new GridChangeEvent(this, getDataTable().getRows().get(rowIndex), columnIndex, rowIndex,
				getDataTable().getValue(columnIndex, rowIndex), newValue, false);
		getDataTable().setValue(columnIndex, rowIndex, newValue);
		fire(gridChangeEvent);
	}

	public void setValue(String columnName, int rowIndex, Value newValue) {
		setValue(getDataTable().getColumnIndex(columnName), rowIndex, newValue);
	}

	public void setValue(String columnName, Row r, Value newValue) {
		setValue(getDataTable().getColumnIndex(columnName), getDataTable().getRows().indexOf(r), newValue);
	}

	public Value getValue(String columnName, Row r) {
		return getDataTable().getValue(columnName, r);
	}

	public Value getValue(String columnName, int rowIndex) {
		return getDataTable().getValue(columnName, rowIndex);
	}

	public Value getValue(int col, int row) {
		return getDataTable().getValue(col, row);
	}

	/**
	 * Liefert eine Liste mit allen Zeilen, die neu eingefügt wurden
	 * 
	 * @return
	 */
	public List<Row> getRowsToInsert() {
		return gridAccessor.getRowsToInsert();
	}

	/**
	 * Liefert eine Liste mit allen Zeilen, die verändert wurden
	 * 
	 * @return
	 */
	public List<Row> getRowsToUpdate() {
		return gridAccessor.getRowsToUpdate();
	}

	/**
	 * Liefert eine Liste mit allen Zeilen, die gelöscht wurden
	 * 
	 * @return
	 */
	public List<Row> getRowsToDelete() {
		return gridAccessor.getRowsToDelete();
	}

	public boolean isExecuteAlways() {
		return executeAlways;
	}

	public void setExecuteAlways(boolean executeAlways) {
		this.executeAlways = executeAlways;
	}
}
