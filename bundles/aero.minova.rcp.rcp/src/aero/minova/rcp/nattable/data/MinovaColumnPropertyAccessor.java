package aero.minova.rcp.nattable.data;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

public class MinovaColumnPropertyAccessor implements IColumnPropertyAccessor<Row> {

	private Table table;

	/**
	 * @param propertyNames of the members of the row bean
	 */
	public MinovaColumnPropertyAccessor(Table table) {
		this.table = table;
	}

	@Override
	public Object getDataValue(Row rowObject, int columnIndex) {
		Value value = rowObject.getValue(columnIndex);
		return value == null ? "" : value.getValue();
	}

	@Override
	public void setDataValue(Row rowObject, int columnIndex, Object newValue) {
		if (newValue == null) {
			newValue = "";
		}
		rowObject.setValue(new Value(newValue), columnIndex);

	}

	@Override
	public int getColumnCount() {
		return table.getColumnCount();
	}

	@Override
	public String getColumnProperty(int columnIndex) {
		return table.getColumnName(columnIndex);
	}

	@Override
	public int getColumnIndex(String propertyName) {
		return table.getColumnIndex(propertyName);
	}

}
