package aero.minova.rcp.rcp.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.TypeParam;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;

public class LookupCASRequestUtil {
	public static CompletableFuture<Table> getRequestedTable(int keyLong, String keyText, Field field,
			Map<String, Control> controls, IDataService dataService, UISynchronize sync) {
		String tableName;
		if (field.getLookup().getTable() != null) {
			tableName = field.getLookup().getTable();
		} else {
			tableName = field.getLookup().getProcedurePrefix();
		}
		TableBuilder tb = TableBuilder.newTable(tableName)//
				.withColumn("KeyLong", DataType.INTEGER)//
				.withColumn("KeyText", DataType.STRING);
		RowBuilder rb;
		if (keyLong == 0) {
			rb = RowBuilder.newRow().withValue(null).withValue(keyText);
		} else {
			rb = RowBuilder.newRow().withValue(keyLong).withValue(null);
		}
		if (field.getLookup().getTable() == null) {
			tb = tb.withColumn("FilterLastAction", DataType.BOOLEAN);
			rb = rb.withValue(0);
		}

		// TODO: Einschränken der angegebenen Optionen anhand bereits ausgewählter
		// Optionen (Kontrakt nur für Kunde x,...)
		List<TypeParam> parameters = field.getLookup().getParam();
		for (TypeParam param : parameters) {
			Control parameterControl = controls.get(param.getFieldName());
			if (parameterControl.getData("keyLong") != null) {
				tb.withColumn(param.getFieldName(), DataType.INTEGER);
				rb.withValue(parameterControl.getData("keyLong"));
			}
		}
		Table t = tb.create();
		Row row = rb.create();
		t.addRow(row);
		;
		CompletableFuture<Table> tableFuture;
		if (field.getLookup().getTable() != null) {
			tableFuture = dataService.getIndexDataAsync(t.getName(), t);
		} else {
			tableFuture = dataService.getDetailDataAsync(t.getName(), t);
		}

		return tableFuture;
	}
}
