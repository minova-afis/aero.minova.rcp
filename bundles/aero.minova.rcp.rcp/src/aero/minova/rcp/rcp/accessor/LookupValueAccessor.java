package aero.minova.rcp.rcp.accessor;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.ILocalDatabaseService;
import aero.minova.rcp.model.LookupEntry;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.LookupCASRequestUtil;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class LookupValueAccessor extends AbstractValueAccessor {

	@Inject
	IDataService dataService;

	@Inject
	UISynchronize sync;

	@Inject
	ILocalDatabaseService localDatabaseService;

	private MDetail detail;

	private Label description;

	public LookupValueAccessor(MField field, MDetail detail, LookupControl control, Label description) {
		super(field, control);
		this.description = description;
		this.detail = detail;

	}

	@Override
	/**
	 * Die Methode verändert den MessageWert und setzt den Textinhalt auf "". Sie überprüft die locale Datenbase, ob der value bereits bekannt ist. Andernfalls
	 * wird eine Abfrage an den CAS versendet
	 */
	protected void updateControlFromValue(Control control, Value keyLong) {
		if (keyLong != null) {
			sync.asyncExec(() -> {
				((LookupControl) control).setMessage("...");
				replaceKeyValues(keyLong);
			});
		} else {
			description.setText("");
			((LookupControl) control).setText("");
		}

	}

	/**
	 * versuchen wir mal herauszufinden, was hier passieren soll
	 * 
	 * @param value
	 */
	private void replaceKeyValues(Value value) {
		Table options = ((MLookupField) field).getOptions();
		if (options != null) {
			for (Row r : options.getRows()) {
				if (r.getValue(options.getColumnIndex(Constants.TABLE_KEYLONG)).equals(value)) {
					((LookupControl) control).setMessage("");
					((LookupControl) control).setText(r.getValue(options.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue());
					if (r.getValue(options.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
						description.setText(r.getValue(options.getColumnIndex(Constants.TABLE_DESCRIPTION)).getStringValue());
					}
				}
			}
		}
		if (((LookupControl) control).getMessage().equals("...")) {

			String lookupName = (field.getLookupTable() != null) ? field.getLookupTable() : field.getLookupProcedurePrefix();
			LookupEntry lookupEntry = localDatabaseService.resolveValue(lookupName, value);

			if (lookupEntry != null) {
				((LookupControl) control).setText(lookupEntry.keyText);
				description.setText((lookupEntry.description != null) ? lookupEntry.description : "");
			} else {
				getLookupConsumer(control, value);
			}


//			changeOptions(); // das benötigen wir hier nicht, weil wir nur aufgerufen werden, wenn ein Wert durch das System gesetzt wird.
//			// Ändern der Optionen der drunterliegenden Felder
//			if (field.getLookupTable() == null) {
//				for (MField f : detail.getFields()) {
//					if (f instanceof MLookupField && f.getSqlIndex() > field.getSqlIndex()) {
//						((LookupValueAccessor) f.getValueAccessor()).changeOptions();
//					}
//				}
//			}
		}
	}

	/**
	 * @param control
	 * @param value
	 */
	private void getLookupConsumer(Control control, Value value) {
		// hinterlegen einer Methode in die component, um stehts die Daten des richtigen
		// Indexes in der Detailview aufzulisten. Hierfür wird eine Anfrage an den CAS
		// gestartet, um die Werte des zugehörigen Keys zu erhalten

		CompletableFuture<?> tableFuture;
		tableFuture = LookupCASRequestUtil.getRequestedTable(value.getIntegerValue(), null, field, detail, dataService, "Resolve");
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

			if (t.getRows().size() > 0) {
				Row row = t.getRows().get(0);
				String lookupName = (field.getLookupTable() != null) ? field.getLookupTable() : field.getLookupProcedurePrefix();
				Value keyLong = row.getValue(t.getColumnIndex(Constants.TABLE_KEYLONG));
				Value keyText = row.getValue(t.getColumnIndex(Constants.TABLE_KEYTEXT));
				Value description = row.getValue(t.getColumnIndex(Constants.TABLE_DESCRIPTION));
				localDatabaseService.updateResolveValue(lookupName, keyLong, keyText, description);
			}
		}));
	}

	/**
	 * Abfangen der Table der in der Consume-Methode versendeten CAS-Abfrage mit Bindung zur Componente
	 *
	 * @param table
	 * @param control
	 */
	public void updateSelectedLookupEntry(Table table, Control control) {
		Row r = table.getRows().get(0);
		int index = table.getColumnIndex(Constants.TABLE_KEYTEXT);
		Value v = r.getValue(index);
		((LookupControl) control).setText((String) ValueBuilder.value(v).create());
		((LookupControl) control).setMessage("");
		if (description != null && table.getColumnIndex(Constants.TABLE_DESCRIPTION) > -1) {
			Value v1 = r.getValue(table.getColumnIndex(Constants.TABLE_DESCRIPTION));
			if (v1 == null) description.setText("");
			else description.setText((String) ValueBuilder.value(v1).create());
		}
	}

	@Override
	/**
	 * Wenn das Feld den Focus verliert wird der Textinhalt überprüft. Ist der Inhalt in keiner Option vorhanden oder ist der Inhalt leer wird das Feld und die
	 * Description bereinigt Ist der Wert vorhanden, so wird geschaut ob er bereits gesetzt wurde oder ob dies getan Werden muss
	 */
	public void setFocussed(boolean focussed) {
		if (focussed) return; // wenn wir den Focus erhalten, machen wir nichts

		// Zunächst wird geprüft, ob der FocusListener aktiviert wurde, während keine Optionen vorlagen oder der DisplayValue neu gesetzt wird
		if (((MLookupField) field).getOptions() != null && field.getValue() == getDisplayValue()) {
			((LookupControl) control).setMessage("");
			String displayText = ((LookupControl) control).getText();
			if (displayText != null && !displayText.equals("")) {
				Table optionTable = ((MLookupField) field).getOptions();
				int indexKeyText = optionTable.getColumnIndex(Constants.TABLE_KEYTEXT);
				int indexKeyLong = optionTable.getColumnIndex(Constants.TABLE_KEYLONG);

				for (Row r : optionTable.getRows()) {
					if (r.getValue(indexKeyText).getStringValue().toLowerCase().startsWith(displayText.toLowerCase())) {
						Value rowValue = r.getValue(indexKeyLong);
						// Der Wert wurde bereits gesetzt und wurde möglicherweise in der Zeile gekürzt
						if (field.getValue() != null && field.getValue().getValue().equals(rowValue.getValue())) {
							((LookupControl) control).setText(r.getValue(indexKeyText).getStringValue());
							return;
						}
						// Ist der Wert noch nicht gesetzt, so wird dies nun getan
						else {
							field.setValue(rowValue, false);
							return;
						}
					}
				}
			}
			field.setValue(null, false);
		}
	}

	public void changeOptions() {

		System.out.println("LookupValueAccessor[" + field.getName() + "].changeOptions()");

		CompletableFuture<?> tableFuture;
		tableFuture = LookupCASRequestUtil.getRequestedTable(0, ((LookupControl) control).getText(), field, detail, dataService, "List");
		tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
			if (ta instanceof SqlProcedureResult) {
				SqlProcedureResult sql = (SqlProcedureResult) ta;
				localDatabaseService.replaceResultsForLookupField(field.getLookupProcedurePrefix(), sql.getResultSet());
				((MLookupField) field).setOptions(sql.getResultSet());
			} else if (ta instanceof Table) {
				Table t = (Table) ta;
				localDatabaseService.replaceResultsForLookupField(field.getLookupTable(), t);
				((MLookupField) field).setOptions(t);
			}
		}));
	}
}
