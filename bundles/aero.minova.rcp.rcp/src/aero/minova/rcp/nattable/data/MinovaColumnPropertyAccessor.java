package aero.minova.rcp.nattable.data;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.FilterValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

public class MinovaColumnPropertyAccessor implements IColumnPropertyAccessor<Row> {

	private Table table;
	private Form form;
	private Map<String, String> tableHeadersMap;
	private String[] propertyNames;

	/**
	 * @param propertyNames
	 *            of the members of the row bean
	 */
	public MinovaColumnPropertyAccessor(Table table, Form form) {
		this.table = table;
		this.form = form;
		propertyNames = new String[form.getIndexView().getColumn().size()];
		tableHeadersMap = new HashMap<>();
	}

	@Override
	public Object getDataValue(Row rowObject, int columnIndex) {
		Value value = rowObject.getValue(columnIndex);
		if (value instanceof FilterValue)
			return value;
		return value == null ? "" : value.getValue();
	}

	@Override
	public void setDataValue(Row rowObject, int columnIndex, Object newValue) {
		if (newValue == null) {
			rowObject.setValue(null, columnIndex);
		} else if (newValue instanceof FilterValue) {
			rowObject.setValue((FilterValue) newValue, columnIndex);
		} else {
			rowObject.setValue(new Value(newValue), columnIndex);
		}
	}

	public void initPropertyNames(TranslationService translationService) {
		int i = 0;
		for (Column column : form.getIndexView().getColumn()) {
			String translate = translationService.translate(column.getLabel(), null);
			getTableHeadersMap().put(column.getName(), translate);
			propertyNames[i++] = column.getName();
		}
	}

	public void translate(TranslationService translationService) {
		getTableHeadersMap().clear();
		for (Column column : form.getIndexView().getColumn()) {
			String translate = translationService.translate(column.getLabel(), null);
			getTableHeadersMap().put(column.getName(), translate);
		}
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

	public String[] getPropertyNames() {
		return propertyNames;
	}

	public Map<String, String> getTableHeadersMap() {
		return tableHeadersMap;
	}

}
