package aero.minova.rcp.rcp.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dialogs.NotificationPopUp;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.helper.ActionCode;

public class WFCDetailCASRequestsUtil {

	@Inject
	protected UISynchronize sync;

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private IDataService dataService;

	@Inject
	private TranslationService translationService;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	@Inject
	@Preference(nodePath = "aero.minova.rcp.preferencewindow", value = "user")
	String employee;

	@Inject
	@Preference(nodePath = "aero.minova.rcp.preferencewindow", value = "timezone")
	String timezone;

	private MDetail detail;

	private MPerspective perspective = null;

	private Map<String, Integer> lookups = new HashMap<>();

	private List<ArrayList> keys = null;

	private Table selectedTable = null;

	@Inject
	private Form form;

	/**
	 * Bei Auswahl eines Indexes wird anhand der in der Row vorhandenen Daten eine
	 * Anfrage an den CAS versendet, um sämltiche Informationen zu erhalten
	 *
	 * @param rows
	 */

	public void setDetail(MDetail detail, MPerspective perspective) {
		this.detail = detail;
		this.perspective = perspective;
	}

	@Inject
	public void changeSelectedEntry(@Optional @Named(Constants.BROKER_ACTIVEROWS) List<Row> rows) {
		if (rows == null || rows.isEmpty()) {
			return;
		}

		Row row = rows.get(0);
		if (row.getValue(0).getValue() != null) {
			Table rowIndexTable = dataFormService.getTableFromFormDetail(form, Constants.READ_REQUEST);

			RowBuilder builder = RowBuilder.newRow();
			List<Field> allFields = dataFormService.getFieldsFromForm(form);

			// Hauptmaske

			List<Column> indexColumns = form.getIndexView().getColumn();
			ArrayList<ArrayList> newKeys = new ArrayList<>();
			for (Field f : allFields) {
				boolean found = false;
				for (int i = 0; i < form.getIndexView().getColumn().size(); i++) {
					if (indexColumns.get(i).getName().equals(f.getName())) {
						found = true;
						if ("primary".equals(f.getKeyType())) {
							builder.withValue(row.getValue(i).getValue());
							ArrayList<Object> al = new ArrayList<>();
							al.add(indexColumns.get(i).getName());
							al.add(row.getValue(i).getValue());
							al.add(ValueBuilder.value(row.getValue(i)).getDataType());
							newKeys.add(al);
						} else {
							builder.withValue(null);
						}
					}
				}
				if (!found) {
					builder.withValue(null);
				}

			}

			if (newKeys.equals(keys)) {
				return;
			} else {
				setKeys(newKeys);
			}

			Row r = builder.create();
			rowIndexTable.addRow(r);

			CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(rowIndexTable.getName(),
					rowIndexTable);
			tableFuture.thenAccept(t -> sync.asyncExec(() -> {
				selectedTable = t.getOutputParameters();
				updateSelectedEntry();
			}));
		}
	}

	/**
	 * Verarbeitung der empfangenen Tabelle des CAS mit Bindung der Detailfelder mit
	 * den daraus erhaltenen Daten, dies erfolgt durch die Consume-Methode
	 */
	public void updateSelectedEntry() {
		if (selectedTable != null) {
			for (MField field : detail.getFields()) {
				field.indicateWaiting();
			}
			for (int i = 0; i < selectedTable.getColumnCount(); i++) {
				String name = selectedTable.getColumnName(i);
				MField c = detail.getField(name);
				if (c != null && c.getConsumer() != null) {
					try {
						c.getConsumer().accept(selectedTable);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	/**
	 * Erstellen einer Update-Anfrage oder einer Insert-Anfrage an den CAS,abhängig
	 * der gegebenen Keys
	 *
	 * @param obj
	 */
	@Inject
	@Optional
	public void buildSaveTable(@UIEventTopic(Constants.BROKER_SAVEENTRY) MPerspective perspective) {
		if (perspective == this.perspective) {
			Table formTable = null;
			RowBuilder rb = RowBuilder.newRow();

			if (getKeys() != null) {
				formTable = dataFormService.getTableFromFormDetail(form, Constants.UPDATE_REQUEST);
			} else {
				formTable = dataFormService.getTableFromFormDetail(form, Constants.INSERT_REQUEST);
			}
			int valuePosition = 0;
			if (getKeys() != null) {
				for (ArrayList key : getKeys()) {
					rb.withValue(key.get(1));
					valuePosition++;
				}
			} else {
				List<Field> keyList = dataFormService.getAllPrimaryFieldsFromForm(form);
				for (Field f : keyList) {
					rb.withValue(null);
					valuePosition++;
				}

			}
			while (valuePosition < formTable.getColumnCount()) {
				MField field = detail.getField(formTable.getColumnName(valuePosition));
//				if (field instanceof MLookupField) {
//					MLookupField mlookup = (MLookupField) field;
//					Value lookupValue = new Value(mlookup.getKeyLong());
//					rb.withValue(lookupValue);
//				}else {
				if (field != null) {
					rb.withValue(field.getValue() != null ? field.getValue().getValue() : null);
				}
//				}
				valuePosition++;
			}

			// anhand der Maske wird der Defaultwert und der DataType des Fehlenden
			// Row-Wertes ermittelt und der Row angefügt
			Row r = rb.create();
			formTable.addRow(r);
			sendSaveRequest(formTable);
		}
	}

	private void sendSaveRequest(Table t) {
		if (t.getRows() != null) {
			CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(t.getName(), t);
			if (Objects.isNull(getKeys())) {
				tableFuture.thenAccept(tr -> sync.asyncExec(() -> {
					checkNewEntryInsert(tr);
				}));
			} else {
				tableFuture.thenAccept(tr -> sync.asyncExec(() -> {
					checkEntryUpdate(tr);
				}));
			}
		} else {
			NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(),
					"Entry not possible, check for wronginputs in your messured Time", shell);
			notificationPopUp.open();
		}
	}

	/**
	 * Überprüft. ob das Update erfolgreich war
	 *
	 * @param responce
	 */
	private void checkEntryUpdate(SqlProcedureResult responce) {
		// Wenn es Hier negativ ist dann haben wir einen Fehler
		if (responce.getReturnCode() == -1) {
			// openNotificationPopup("Entry could not be updated:" +
			// responce.getResultSet());
			Row r = responce.getResultSet().getRows().get(0);
			MessageDialog.openError(shell, "Error while updating Entry",
					r.getValue(responce.getResultSet().getColumnIndex("Message")).getStringValue());
		} else {
			openNotificationPopup("Sucessfully updated the entry");
			Map<MPerspective, String> map = new HashMap<>();
			map.put(perspective, Constants.UPDATE_REQUEST);
			clearFields(map);
			if (detail.getHelper() != null) {
				detail.getHelper().handleDetailAction(ActionCode.SAVE);
			}
		}
	}

	/**
	 * Überprüft, ob der neue Eintrag erstellt wurde
	 *
	 * @param responce
	 */
	private void checkNewEntryInsert(SqlProcedureResult responce) {
		if (responce.getReturnCode() == -1) {
			// openNotificationPopup("Entry could not be added:" + responce.getResultSet());
			Row r = responce.getResultSet().getRows().get(0);
			MessageDialog.openError(shell, "Error while adding Entry",
					r.getValue(responce.getResultSet().getColumnIndex("Message")).getStringValue());
		} else {
			openNotificationPopup("Sucessfully added the entry");
			Map<MPerspective, String> map = new HashMap<>();
			map.put(perspective, Constants.INSERT_REQUEST);
			clearFields(map);
			if (detail.getHelper() != null) {
				detail.getHelper().handleDetailAction(ActionCode.SAVE);
			}
		}
	}

	@Inject
	@Optional
	public void showErrorMessage(@UIEventTopic(Constants.BROKER_SHOWERROR) Table errorTable) {
		Value vMessageProperty = errorTable.getRows().get(0).getValue(0);
		String messageproperty = "@" + vMessageProperty.getStringValue();
		String value = translationService.translate(messageproperty, null);
		// Ticket number {0} is not numeric
		if (errorTable.getColumnCount() > 1) {
			List<String> params = new ArrayList<>();
			for (int i = 1; i < errorTable.getColumnCount(); i++) {
				Value v = errorTable.getRows().get(0).getValue(i);
				params.add(v.getStringValue());
			}
			value = MessageFormat.format(value, params.toArray(new String[0]));
		}
		MessageDialog.openError(shell, "Error", value);
	}

	/**
	 * Sucht die aktiven Controls aus der XMLDetailPart und baut anhand deren Werte
	 * eine Abfrage an den CAS zusammen
	 *
	 * @param obj
	 */
	@Inject
	@Optional
	public void buildDeleteTable(@UIEventTopic(Constants.BROKER_DELETEENTRY) MPerspective perspective) {
		if (perspective == this.perspective) {
			if (getKeys() != null) {
				String tablename = form.getIndexView() != null ? "sp" : "op";
				if ((!"sp".equals(form.getDetail().getProcedurePrefix())
						&& !"op".equals(form.getDetail().getProcedurePrefix()))) {
					tablename = form.getDetail().getProcedurePrefix();
				}
				tablename += "Delete";
				tablename += form.getDetail().getProcedureSuffix();
				TableBuilder tb = TableBuilder.newTable(tablename);
				RowBuilder rb = RowBuilder.newRow();
				for (ArrayList key : getKeys()) {
					tb.withColumn((String) key.get(0), (DataType) key.get(2));
					rb.withValue(key.get(1));
				}
				Table t = tb.create();
				Row r = rb.create();
				t.addRow(r);
				if (t.getRows() != null) {
					CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(t.getName(), t);
					tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
						deleteEntry(ta);
					}));
				}
			}
		}
	}

	/**
	 * Überprüft, ob die Anfrage erfolgreich war, falls nicht bleiben die Textfelder
	 * befüllt um die Anfrage anzupassen
	 *
	 * @param responce
	 */
	public void deleteEntry(SqlProcedureResult responce) {
		if (responce.getReturnCode() == -1) {
			openNotificationPopup("Entry could not be deleted:" + responce.getResultSet());
			Row r = responce.getResultSet().getRows().get(0);
			MessageDialog.openError(shell, "Error while deleting Entry",
					r.getValue(responce.getResultSet().getColumnIndex("Message")).getStringValue());
		} else {
			openNotificationPopup("Sucessfully deleted the entry");
			Map<MPerspective, String> map = new HashMap<>();
			map.put(perspective, Constants.DELETE_REQUEST);
			clearFields(map);
			// Helper-Klasse triggern, damit die Standard-Werte gesetzt werden können.
			if (detail.getHelper() != null) {
				detail.getHelper().handleDetailAction(ActionCode.DEL);
			}
		}
	}

	/**
	 * Öffet ein Popup, welches dem Nutzer über den Erfolg oder das Scheitern seiner
	 * Anfrage informiert
	 *
	 * @param message
	 */
	public void openNotificationPopup(String message) {
		NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(), message, shell);
		notificationPopUp.open();
	}

	/**
	 * Diese Methode reagiert wird ausgeführt, wenn der Anwender einen neuen
	 * Datensatz eintragen möchte.
	 *
	 * @param origin
	 */
	@Optional
	@Inject
	public void newFields(@UIEventTopic(Constants.BROKER_NEWENTRY) Map<MPerspective, String> map) {
		clearFields(map);
		// Helper-Klasse triggern, damit die Standard-Werte gesetzt werden können.
		if (detail.getHelper() != null) {
			detail.getHelper().handleDetailAction(ActionCode.NEW);
		}
	}

	/**
	 * Diese Methode bereiningt die Felder nach einer Erfolgreichen CAS-Anfrage
	 *
	 * @param origin
	 */
	@Optional
	@Inject
	public void clearFields(@UIEventTopic(Constants.BROKER_CLEARFIELDS) Map<MPerspective, String> map) {
		for (MField f : detail.getFields()) {
			setKeys(null);
			f.setValue(null, false);
			if (f instanceof MLookupField) {
				((MLookupField) f).setOptions(null);
			}
		}
	}

//	/**
//	 * Antworten des CAS für Ticketnummern werden hier ausgelesen, so das sie wie bei einem Aufruf in der Index-Tabelle ausgewertet werden können
//	 *
//	 * @param recievedTable
//	 */
//	@Optional
//	@Inject
//	public void getTicket(@UIEventTopic(Constants.RECEIVED_TICKET) Table recievedTable) {
//
//		for (Control c : controls.values()) {
//			if (c instanceof LookupControl) {
//				LookupControl lc = (LookupControl) c;
//				if (lc != controls.get(Constants.EMPLOYEEKEY)) {
//					lc.setText("");
//					lc.setData(Constants.CONTROL_KEYLONG, null);
//					lc.getDescription().setText("");
//				}
//			}
//		}
//		Row recievedRow = recievedTable.getRows().get(0);
//		if (selectedTable == null) {
//			selectedTable = dataFormService.getTableFromFormDetail(form, Constants.READ_REQUEST);
//			selectedTable.addRow();
//		} else if (selectedTable.getRows() == null) {
//			selectedTable.addRow();
//		}
//		Row r = selectedTable.getRows().get(0);
//		for (int i = 0; i < r.size(); i++) {
//			if ((recievedTable.getColumnIndex(selectedTable.getColumnName(i))) >= 0) {
//				r.setValue(recievedRow.getValue(recievedTable.getColumnIndex(selectedTable.getColumnName(i))), i);
//			} else {
//				Control c = controls.get(selectedTable.getColumnName(i));
//				if (c instanceof LookupControl) {
//					LookupControl lc = (LookupControl) c;
//					r.setValue(new Value(lc.getData(Constants.CONTROL_KEYLONG), DataType.INTEGER), i);
//				} else if (c instanceof Text) {
//					Text t = (Text) c;
//					if (t.getText() != null) {
//						r.setValue(new Value(t.getText(), DataType.STRING), i);
//					} else {
//						r.setValue(new Value("", DataType.STRING), i);
//					}
//				}
//			}
//		}
//
//		updateSelectedEntry();
//	}

	/**
	 * Setzt die Detail-Felder wieder auf den Usprungszustand des Ausgewählten
	 * Eintrags zurück
	 *
	 * @param obj
	 */
	@Inject
	@Optional
	public void revertEntry(@UIEventTopic(Constants.BROKER_REVERTENTRY) MPerspective perspective) {
		if (perspective == this.perspective) {
			updateSelectedEntry();
		}
	}

	public List<ArrayList> getKeys() {
		return keys;
	}

	public void setKeys(ArrayList<ArrayList> arrayList) {
		this.keys = arrayList;
	}
}
