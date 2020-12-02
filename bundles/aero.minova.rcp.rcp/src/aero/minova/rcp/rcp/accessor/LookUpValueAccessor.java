package aero.minova.rcp.rcp.accessor;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.ILocalDatabaseService;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.LookupCASRequestUtil;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class LookUpValueAccessor extends AbstractValueAccessor {

	@Inject
	IDataService dataService;

	@Inject
	UISynchronize sync;

	@Inject
	ILocalDatabaseService localDatabaseService;

	private MDetail detail;

	public LookUpValueAccessor(MField field, MDetail detail, Control control) {
		super(field, control);
		this.detail = detail;

	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		if (value == null) {
			((LookupControl) control).setText("");
		} else {
			((LookupControl) control).setData(Constants.CONTROL_KEYLONG, value.getIntegerValue());
			((LookupControl) control).setData(Constants.CONTROL_DATATYPE, value.getType());
			getLookUpConsumer(control, value);
		}
	}

	private void getLookUpConsumer(Control control, Value value) {
		// hinterlegen einer Methode in die component, um stehts die Daten des richtigen
		// Indexes in der Detailview aufzulisten. Hierfür wird eine Anfrage an den CAS
		// gestartet, um die Werte des zugehörigen Keys zu erhalten

		CompletableFuture<?> tableFuture;
		tableFuture = LookupCASRequestUtil.getRequestedTable(value.getIntegerValue(), null, field, detail, dataService,
				"Resolve");
		// Diese Methode lauft auserhalb des Hauptthreads. Desshalb brauchen wir nochmal
		// den MainThread, damit die UI-Componenten aktualisiert werden können
		tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
			Table t = null;
			if (ta instanceof SqlProcedureResult) {
				SqlProcedureResult sql = (SqlProcedureResult) ta;
				t = sql.getResultSet();
			} else if (ta instanceof Table) {
				t = (Table) ta;
			}
			localDatabaseService.addResultsForLookupField(field.getName(), t);
			updateSelectedLookupEntry(t, control);
		}));
	}

	/**
	 * Abfangen der Table der in der Consume-Methode versendeten CAS-Abfrage mit
	 * Bindung zur Componente
	 *
	 * @param table
	 * @param control
	 */
	public static void updateSelectedLookupEntry(Table table, Control control) {
		Row r = table.getRows().get(0);
		LookupControl lc = (LookupControl) control;
		int index = table.getColumnIndex(Constants.TABLE_KEYTEXT);
		Value v = r.getValue(index);
		lc.setText((String) ValueBuilder.value(v).create());
		if (lc.getDescription() != null && table.getColumnIndex(Constants.TABLE_DESCRIPTION) > -1) {
			if (r.getValue(table.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
				lc.getDescription().setText((String) ValueBuilder
						.value(r.getValue(table.getColumnIndex(Constants.TABLE_DESCRIPTION))).create());
			} else {
				lc.getDescription().setText("");
			}
		}
	}
}
