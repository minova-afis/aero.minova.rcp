package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.event.GridChangeEvent;
import aero.minova.rcp.model.event.GridChangeListener;

public class MGrid {

	public MGrid(String procedureSuffix) {
		super();
		this.procedureSuffix = procedureSuffix;
	}

	private String title;
	private String procedureSuffix;
	private String procedurePrefix;
	private String helperClass;
	private Image icon;
	private IGridAccessor gridAccessor;
	private boolean delReqAllParams;
	private String fill;
	private Grid grid;
	private List<MField> fields;
	private MSection mSection;
	private Table dataTable;
	private ArrayList<GridChangeListener> listeners;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
		return dataTable;
	}

	public void setDataTable(Table dataTable) {
		this.dataTable = dataTable;
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
		for (Column c : dataTable.getColumns()) {
			if (c.isRequired()) {
				// TODO: Weitere Eigenschaften prüfen? (Textlänge, ...)
				for (Row r : dataTable.getRows()) {
					if (r.getValue(dataTable.getColumns().indexOf(c)).getValue() == null) {
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
	public void dataTableChanged() {
		fire(new GridChangeEvent(this, dataTable));
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

	public void addRows(List<Row> rows) {
		gridAccessor.addRows(rows);
	}
}
