package aero.minova.rcp.rcp.nattable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;

import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.preferencewindow.control.CustomLocale;

public abstract class MinovaColumnConfiguration extends AbstractRegistryConfiguration {

	protected List<Column> columns;
	protected Locale locale = CustomLocale.getLocale();
	protected Form form;
	protected Map<String, aero.minova.rcp.form.model.xsd.Column> formColumns;

	protected MinovaColumnConfiguration(List<Column> columns, Form form) {
		this.columns = columns;
		this.form = form;
		initFormFields();
	}

	public void initFormFields() {
		if (form == null) {
			return;
		}

		formColumns = new HashMap<>();
		List<aero.minova.rcp.form.model.xsd.Column> column = form.getIndexView().getColumn();
		for (aero.minova.rcp.form.model.xsd.Column column2 : column) {
			formColumns.put(column2.getName(), column2);
		}
	}

	public List<Integer> getHiddenColumns() {
		List<Integer> hiddenCols = new ArrayList<>();
		for (Column c : columns) {
			if (!c.isVisible()) {
				hiddenCols.add(columns.indexOf(c));
			}
		}
		return hiddenCols;
	}

}
