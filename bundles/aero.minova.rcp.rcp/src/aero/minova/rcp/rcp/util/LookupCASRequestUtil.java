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
			// Hier müssen wir explizit unsere Felder abfragen:
			// Keylong, KeyText, Descript, LastAction
		} else {
			tableName = field.getLookup().getProcedurePrefix() + purpose;
		}
		TableBuilder tb = TableBuilder.newTable(tableName);
		RowBuilder rb = RowBuilder.newRow();
		if (isTable) {
			tb = tb.withColumn("KeyLong", DataType.INTEGER)//
					.withColumn("KeyText", DataType.STRING)//
					.withColumn("Description", DataType.STRING)//
					.withColumn("LastAction", DataType.INTEGER);//
			rb = rb.withValue(null);
			rb = rb.withValue(null);
			rb = rb.withValue(null);
			rb = rb.withValue(">0");
		}
		if (!purpose.equals("List") && !isTable) {
			tb = tb//
					.withColumn("KeyLong", DataType.INTEGER)//
					.withColumn("KeyText", DataType.STRING);
			if (keyLong == 0) {
				rb = rb.withValue(null).withValue(keyText);
			} else {
				rb = rb.withValue(keyLong).withValue(null);
			}
		} else if (purpose.equals("List") && !isTable) {
			tb = tb.withColumn("count", DataType.INTEGER);
			rb = rb.withValue(null);
		}
		if (field.getLookup().getTable() == null && !isTable) {
			tb = tb.withColumn("FilterLastAction", DataType.BOOLEAN);
			rb = rb.withValue(false);
		}

		// TODO: Einschränken der angegebenen Optionen anhand bereits ausgewählter
		// Optionen (Kontrakt nur für Kunde x,...)
		// Für nicht-lookups(bookingdate)->Text übernehmen wenn nicht null
		// bei leeren feldern ein nullfeld anhängen, alle parameter müssen für die
		// anfrage gesetzt sein
		if (purpose.equals("List") && !isTable) {
			List<TypeParam> parameters = field.getLookup().getParam();
			for (TypeParam param : parameters) {
				Control parameterControl = controls.get(param.getFieldName());
				if (parameterControl instanceof LookupControl) {
					tb.withColumn(param.getFieldName(), DataType.INTEGER);
					// Auslesen des KeyLong-Wertes und setzen in der Table!
					if (parameterControl.getData("KeyLong") != null) {
						rb.withValue(parameterControl.getData("KeyLong"));
					} else {
						rb.withValue(null);
					}
				} else if (parameterControl instanceof Text) {
					tb.withColumn(param.getFieldName(), (DataType) parameterControl.getData("dataType"));
					rb.withValue(null);
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
