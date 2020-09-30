package aero.minova.rcp.nattable.data;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

public class MinovaColumnPropertyAccessor implements IColumnPropertyAccessor<Row> {

	private Table table;
	// Keylong, Keytext, CustomerKeyText, ...

//		Row r;
//		r = new Row();
//		r.addValue(new Value(1));
//		r.addValue(null);
//		r.addValue(new Value(23.5));
//		r.addValue(new Value("Wilfried Saak"));
//		r.addValue(new Value(Instant.now()));
//		r.addValue(new Value(ZonedDateTime.now()));
//		r.addValue(new Value(true));
//		r.addValue(new Value(false));

	/**
	 * @param propertyNames of the members of the row bean
	 */
	public MinovaColumnPropertyAccessor(Table table) {
		this.table = table;
	}

	@Override
	public Object getDataValue(Row rowObject, int columnIndex) {
		Value value = rowObject.getValue(columnIndex);
		return value == null ? null : value.getValue();
	}

	@Override
	public void setDataValue(Row rowObject, int columnIndex, Object newValue) {
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
