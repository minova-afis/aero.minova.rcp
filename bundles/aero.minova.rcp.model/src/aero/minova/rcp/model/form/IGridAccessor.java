package aero.minova.rcp.model.form;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;

public interface IGridAccessor {

	Table getSelectedRows();

	void deleteCurrentRows();

	void addRows(List<Row> rows);

	void addSelectionListener(ILayerListener listener);

	void removeSelectionListener(ILayerListener listener);

}
