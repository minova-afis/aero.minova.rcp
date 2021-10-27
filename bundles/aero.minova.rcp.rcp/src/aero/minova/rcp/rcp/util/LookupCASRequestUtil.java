package aero.minova.rcp.rcp.util;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;

public class LookupCASRequestUtil {
	private LookupCASRequestUtil() {}

	public static CompletableFuture<?> getRequestedTablexx(int keyLong, String keyText, MField field, MDetail detail, IDataService dataService,
			String purpose) {
		String tableName;
		boolean isTable = false;
		if (field.getLookupTable() != null) {
			tableName = field.getLookupTable();
			isTable = true;
		} else {
			tableName = field.getLookupProcedurePrefix() + purpose;
		}
		TableBuilder tableBuilder = TableBuilder.newTable(tableName);
		RowBuilder rowBuilder = RowBuilder.newRow();
		if (isTable) {
			tableBuilder = tableBuilder.withColumn(Constants.TABLE_KEYLONG, DataType.INTEGER)//
					.withColumn(Constants.TABLE_KEYTEXT, DataType.STRING)//
					.withColumn(Constants.TABLE_DESCRIPTION, DataType.STRING)//
					.withColumn(Constants.TABLE_LASTACTION, DataType.INTEGER);//
			if (keyLong == 0) {
				rowBuilder = rowBuilder.withValue(null);
			} else {
				rowBuilder = rowBuilder.withValue(keyLong);
			}
			rowBuilder = rowBuilder.withValue(keyText);
			rowBuilder = rowBuilder.withValue(null);
			rowBuilder = rowBuilder.withValue(">0");
		} else {
			// Reihenfolge der Werte einhalten!
			if (!purpose.equals("List")) {
				tableBuilder = tableBuilder//
						.withColumn(Constants.TABLE_KEYLONG, DataType.INTEGER)//
						.withColumn(Constants.TABLE_KEYTEXT, DataType.STRING);
				if (keyLong == 0) {
					rowBuilder = rowBuilder.withValue(null).withValue(keyText);
				} else {
					rowBuilder = rowBuilder.withValue(keyLong).withValue(null);
				}
			} else if (purpose.equals("List")) {
				tableBuilder = tableBuilder.withColumn("count", DataType.INTEGER);
				rowBuilder = rowBuilder.withValue(null);
			}
			tableBuilder = tableBuilder.withColumn("FilterLastAction", DataType.BOOLEAN);
			rowBuilder = rowBuilder.withValue(false);

			// Einschränken der angegebenen Optionen anhand bereits ausgewählter
			// Optionen (Kontrakt nur für Kunde x,...)
			// Für nicht-lookups(bookingdate)->Text übernehmen wenn nicht null
			// bei leeren feldern ein nullfeld anhängen, alle parameter müssen für die
			// anfrage gesetzt sein
			if (purpose.equals("List")) {
				List<String> parameters = field.getLookupParameters();
				for (String param : parameters) {
					MField parameterControl = detail.getField(param);
					tableBuilder.withColumn(param, parameterControl.getDataType());
					if (parameterControl.getValue() != null) {
						rowBuilder.withValue(parameterControl.getValue().getValue());
					} else {
						rowBuilder.withValue(null);
					}
				}
			}
		}
		Table t = tableBuilder.create();
		Row row = rowBuilder.create();
		t.addRow(row);
		CompletableFuture<?> tableFuture;
		if (field.getLookupTable() != null) {
			tableFuture = dataService.getTableAsync(t);
		} else {
			tableFuture = dataService.callProcedureAsync(t);
		}

		return tableFuture;
	}
}
