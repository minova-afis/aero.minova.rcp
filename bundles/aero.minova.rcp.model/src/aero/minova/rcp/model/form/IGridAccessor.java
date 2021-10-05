package aero.minova.rcp.model.form;

import java.util.List;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;

public interface IGridAccessor {

	Table getSelectedRows();

	void deleteCurrentRows();

	void addRows(List<Row> rows);

}
