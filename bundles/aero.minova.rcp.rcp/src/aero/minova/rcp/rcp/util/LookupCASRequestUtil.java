package aero.minova.rcp.rcp.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.TypeParam;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class LookupCASRequestUtil {
	public static CompletableFuture<?> getRequestedTable(int keyLong, String keyText, Field field,
			Map<String, Control> controls, IDataService dataService, UISynchronize sync, String purpose) {
		String tableName;
		Boolean isTable = false;
		if (field.getLookup().getTable() != null) {
			tableName = field.getLookup().getTable();
			isTable = true;
		} else {
			tableName = field.getLookup().getProcedurePrefix() + purpose;
		}
		TableBuilder tb = TableBuilder.newTable(tableName);
		RowBuilder rb = RowBuilder.newRow();
		if (isTable) {
			tb = tb.withColumn(Constants.TABLE_KEYLONG, DataType.INTEGER)//
					.withColumn(Constants.TABLE_KEYTEXT, DataType.STRING)//
					.withColumn(Constants.TABLE_DESCRIPTION, DataType.STRING)//
					.withColumn(Constants.TABLE_LASTACTION, DataType.INTEGER);//
			if (keyLong == 0) {
				rb = rb.withValue(null);
			} else {
				rb = rb.withValue(keyLong);
			}
			rb = rb.withValue(keyText);
			rb = rb.withValue(null);
			rb = rb.withValue(">0");
		} else {
			// Reihenfolge der Werte einhalten!
			if (!purpose.equals("List")) {
				tb = tb//
						.withColumn(Constants.TABLE_KEYLONG, DataType.INTEGER)//
						.withColumn(Constants.TABLE_KEYTEXT, DataType.STRING);
				if (keyLong == 0) {
					rb = rb.withValue(null).withValue(keyText);
				} else {
					rb = rb.withValue(keyLong).withValue(null);
				}
			} else if (purpose.equals("List")) {
				tb = tb.withColumn("count", DataType.INTEGER);
				rb = rb.withValue(null);
			}
			tb = tb.withColumn("FilterLastAction", DataType.BOOLEAN);
			rb = rb.withValue(false);

			// Einschränken der angegebenen Optionen anhand bereits ausgewählter
			// Optionen (Kontrakt nur für Kunde x,...)
			// Für nicht-lookups(bookingdate)->Text übernehmen wenn nicht null
			// bei leeren feldern ein nullfeld anhängen, alle parameter müssen für die
			// anfrage gesetzt sein
			if (purpose.equals("List")) {
				List<TypeParam> parameters = field.getLookup().getParam();
				for (TypeParam param : parameters) {
					Control parameterControl = controls.get(param.getFieldName());
					if (parameterControl instanceof LookupControl) {
						tb.withColumn(param.getFieldName(), DataType.INTEGER);
						// Auslesen des KeyLong-Wertes und setzen in der Table!
						if (parameterControl.getData(Constants.TABLE_KEYLONG) != null) {
							rb.withValue(parameterControl.getData(Constants.TABLE_KEYLONG));
						} else {
							rb.withValue(null);
						}
					} else if (parameterControl instanceof Text) {
						tb.withColumn(param.getFieldName(),
								(DataType) parameterControl.getData(Constants.CONTROL_DATATYPE));
						rb.withValue(null);
					}
				}
			}
		}
		Table t = tb.create();
		Row row = rb.create();
		t.addRow(row);
		CompletableFuture<?> tableFuture;
		if (field.getLookup().getTable() != null) {
			tableFuture = dataService.getIndexDataAsync(t.getName(), t);
		} else {
			tableFuture = dataService.getDetailDataAsync(t.getName(), t);
		}

		return tableFuture;
	}
}
