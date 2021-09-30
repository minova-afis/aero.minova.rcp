package aero.minova.rcp.nattable.data;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.model.FilterValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

public class MinovaColumnPropertyAccessor implements IColumnPropertyAccessor<Row> {

	private Table table;
	private Form form;
	private Grid grid;
	private Map<String, String> tableHeadersMap;
	private String[] propertyNames;

	public MinovaColumnPropertyAccessor(Table table, Form form) {
		this.table = table;
		this.form = form;
		propertyNames = new String[form.getIndexView().getColumn().size()];
		tableHeadersMap = new HashMap<>();
	}

	public MinovaColumnPropertyAccessor(Table table, Grid grid) {
		this.table = table;
		this.grid = grid;
		propertyNames = new String[grid.getField().size()];
		tableHeadersMap = new HashMap<>();
	}

	@Override
	public Object getDataValue(Row rowObject, int columnIndex) {
		Value value = rowObject.getValue(columnIndex);
		if (value instanceof FilterValue) {
			return value;
		}
		return value == null ? "" : value.getValue();
	}

	@Override
	public void setDataValue(Row rowObject, int columnIndex, Object newValue) {
		if (newValue == null) {
			rowObject.setValue(null, columnIndex);
		} else if (newValue instanceof FilterValue) {
			rowObject.setValue((FilterValue) newValue, columnIndex);
		} else {
			rowObject.setValue(new Value(newValue, table.getColumns().get(columnIndex).getType()), columnIndex);
		}
	}

	public void initPropertyNames(TranslationService translationService) {
		if (form != null) {
			initPropertyNamesForm(translationService);
		} else if (grid != null) {
			initPropertyNamesGrid(translationService);
		}
	}

	private void initPropertyNamesForm(TranslationService translationService) {
		int i = 0;
		for (Column column : form.getIndexView().getColumn()) {
			String translate = column.getName();
			if (column.getLabel() != null) {
				translate = translationService.translate(column.getLabel(), null);
			}
			getTableHeadersMap().put(column.getName(), translate);
			propertyNames[i++] = column.getName();
		}
	}

	private void initPropertyNamesGrid(TranslationService translationService) {
		int i = 0;
		for (Field field : grid.getField()) {
			String translate = field.getName();
			if (field.getLabel() != null) {
				translate = translationService.translate(field.getLabel(), null);
			}
			getTableHeadersMap().put(field.getName(), translate);
			propertyNames[i++] = field.getName();
		}
	}

	public void translate(TranslationService translationService) {
		getTableHeadersMap().clear();
		if (form != null) {
			translateForm(translationService);
		} else if (grid != null) {
			translateGrid(translationService);
		}
	}

	private void translateForm(TranslationService translationService) {
		for (Column column : form.getIndexView().getColumn()) {
			String translate = translationService.translate(column.getLabel(), null);
			getTableHeadersMap().put(column.getName(), translate);
		}
	}

	private void translateGrid(TranslationService translationService) {
		for (Field field : grid.getField()) {
			String translate = translationService.translate(field.getText().toString(), null);
			getTableHeadersMap().put(field.getName(), translate);
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
