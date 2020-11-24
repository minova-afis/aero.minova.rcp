package aero.minova.rcp.rcp.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.ILocalDatabaseService;
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
import aero.minova.rcp.rcp.widgets.LookupControl;

public class WFCDetailCASRequestsUtil {

	@Inject
	protected UISynchronize sync;

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private IDataService dataService;

	private ILocalDatabaseService localDatabaseService;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	@Inject
	@Preference(nodePath = "aero.minova.rcp.preferencewindow", value = "user")
	String employee;

	@Inject
	@Preference(nodePath = "aero.minova.rcp.preferencewindow", value = "timezone")
	String timezone;

	private MPerspective perspective = null;

	private Map<String, Control> controls = null;
	private Map<String, Integer> lookups = new HashMap();

	private List<ArrayList> keys = null;

	private Table selectedTable = null;

	@Inject
	private Form form;

	private String lastEndDate = "";

	/**
	 * Bei Auswahl eines Indexes wird anhand der in der Row vorhandenen Daten eine
	 * Anfrage an den CAS versendet, um sämltiche Informationen zu erhalten
	 *
	 * @param rows
	 */

	public void setControls(Map<String, Control> controls, MPerspective perspective,
			ILocalDatabaseService localDatabaseService) {
		this.controls = controls;
		this.perspective = perspective;
		this.localDatabaseService = localDatabaseService;
	}

	@Inject
	public void changeSelectedEntry(@Optional @Named(Constants.BROKER_ACTIVEROWS) List<Row> rows) {
		if (rows != null) {
			if (rows.size() != 0) {
				Row row = rows.get(0);
				if (row.getValue(0).getValue() != null) {
					Table rowIndexTable = dataFormService.getTableFromFormDetail(form, Constants.READ_REQUEST);

					RowBuilder builder = RowBuilder.newRow();
					List<Field> allFields = dataFormService.getFieldsFromForm(form);

					// Hauptmaske

					List<Column> indexColumns = form.getIndexView().getColumn();
					setKeys(new ArrayList<ArrayList>());
					for (Field f : allFields) {
						boolean found = false;
						for (int i = 0; i < form.getIndexView().getColumn().size(); i++) {
							if (indexColumns.get(i).getName().equals(f.getName())) {
								found = true;
								if ("primary".equals(f.getKeyType())) {
									builder.withValue(row.getValue(i).getValue());
									ArrayList al = new ArrayList();
									al.add(indexColumns.get(i).getName());
									al.add(row.getValue(i).getValue());
									al.add(ValueBuilder.value(row.getValue(i)).getDataType());
									keys.add(al);
								} else {
									builder.withValue(null);
								}
							}
						}
						if (!found) {
							builder.withValue(null);
						}

					}
					Row r = builder.create();
					rowIndexTable.addRow(r);

					CompletableFuture<SqlProcedureResult> tableFuture = dataService
							.getDetailDataAsync(rowIndexTable.getName(), rowIndexTable);
					tableFuture.thenAccept(t -> sync.asyncExec(() -> {
						selectedTable = t.getOutputParameters();
						updateSelectedEntry();
					}));
				}
			}
		}
	}

	/**
	 * Verarbeitung der empfangenen Tabelle des CAS mit Bindung der Detailfelder mit
	 * den daraus erhaltenen Daten, dies erfolgt durch die Consume-Methode
	 */
	public void updateSelectedEntry() {
		Table table = selectedTable;
		if (selectedTable != null) {
			for (Control c : controls.values()) {
				if (c instanceof Text) {
					Text t = (Text) c;
					t.setText("");

				} else if (c instanceof LookupControl) {
					LookupControl lc = (LookupControl) c;
					lc.setText("");
					lc.getDescription().setText("");
					lc.getTextControl().setMessage("...");
				}
			}

			for (int i = 0; i < table.getColumnCount(); i++) {
				String name = table.getColumnName(i);
				Control c = controls.get(name);
				if (c != null) {
					Consumer<Table> consumer = (Consumer<Table>) c.getData(Constants.CONTROL_CONSUMER);
					if (consumer != null) {
						try {
							consumer.accept(table);
						} catch (Exception e) {
						}
					}
					if (c instanceof LookupControl) {
						LookupControl lc = (LookupControl) c;
						Field field = (Field) lc.getData(Constants.CONTROL_FIELD);
						Map databaseMap = localDatabaseService.getResultsForKeyLong(field.getName(),
								table.getRows().get(0).getValue(i).getIntegerValue());
						if (databaseMap != null) {
							lc.setData(Constants.CONTROL_KEYLONG, databaseMap.get(Constants.TABLE_KEYLONG));
							lc.setText((String) databaseMap.get(Constants.TABLE_KEYTEXT));
							lc.getTextControl().setMessage("");
							if (databaseMap.get(Constants.TABLE_DESCRIPTION) != null) {
								lc.getDescription().setText((String) databaseMap.get(Constants.TABLE_DESCRIPTION));
							}
						} else {
							Map hash = new HashMap<>();
							hash.put("value", table.getRows().get(0).getValue(i));
							hash.put("sync", sync);
							hash.put("dataService", dataService);
							hash.put("control", c);

							Consumer<Map> lookupConsumer = (Consumer<Map>) c.getData(Constants.CONTROL_LOOKUPCONSUMER);
							if (lookupConsumer != null) {
								try {
									lookupConsumer.accept(hash);

								} catch (Exception e) {
								}
							}
						}
					}

				}
			}
			// Dieser Ansatz kommt in Frage, falls wir stehts die Optionen austauschen
			// möchten und die Latenz gesamt kleinhalten möchten
			for (Control co : controls.values()) {
				if (co instanceof LookupControl) {
					LookupControl lc = (LookupControl) co;
					Field field = (Field) lc.getData(Constants.CONTROL_FIELD);
					if (lookups.get(field.getName()) == null || lc.getData(Constants.CONTROL_KEYLONG) == null
							|| (int) lc.getData(Constants.CONTROL_KEYLONG) != lookups.get(field.getName())) {
						lookups.remove(field.getName());

						CompletableFuture<?> tableFuture;
						tableFuture = LookupCASRequestUtil.getRequestedTable(0, (String) lc.getText(), field, controls,
								dataService, sync, "List");
						tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
							if (ta instanceof SqlProcedureResult) {
								SqlProcedureResult sql = (SqlProcedureResult) ta;
								localDatabaseService.replaceResultsForLookupField(field.getName(), sql.getResultSet());
								lc.setData(Constants.CONTROL_OPTIONS, sql.getResultSet());
								for (Row row : sql.getResultSet().getRows()) {
									if (row.getValue(sql.getResultSet().getColumnIndex(Constants.TABLE_KEYTEXT))
											.getStringValue().equals(lc.getText())) {
										lookups.put(field.getName(),
												row.getValue(sql.getResultSet().getColumnIndex(Constants.TABLE_KEYLONG))
														.getIntegerValue());
									}
								}
							} else if (ta instanceof Table) {
								Table t = (Table) ta;
								localDatabaseService.replaceResultsForLookupField(field.getName(), t);
								lc.setData(Constants.CONTROL_OPTIONS, ta);
								for (Row row : t.getRows()) {
									if (row.getValue(t.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue()
											.equals(lc.getText())) {
										lookups.put(field.getName(), row
												.getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)).getIntegerValue());
									}
								}
							}

						}));
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
				int i = 0;
				for (Control c : controls.values()) {
					String s = (String) controls.keySet().toArray()[i];
					if (s.equals(formTable.getColumnName(valuePosition))) {
						if (c instanceof Text) {
							if (!(((Text) c).getText().isBlank())) {
								rb.withValue(((Text) c).getText());
							} else {
								rb.withValue(null);

							}
						}
						if (c instanceof TextAssist) {
							if (!(((TextAssist) c).getText().isBlank())) {
								rb.withValue(((TextAssist) c).getText());
							} else {
								rb.withValue(null);

							}
						}
						if (c instanceof LookupControl) {
							if (c.getData(Constants.CONTROL_KEYLONG) != null) {
								rb.withValue(c.getData(Constants.CONTROL_KEYLONG));
							} else {
								rb.withValue(null);
							}
						}
					}
					i++;
				}
				valuePosition++;
			}

			// anhand der Maske wird der Defaultwert und der DataType des Fehlenden
			// Row-Wertes ermittelt und der Row angefügt
			Row r = rb.create();
			List<Field> formFields = dataFormService.getFieldsFromForm(form);
			if (controls.size() < formTable.getColumnCount()) {
				for (int i = r.size(); i < formTable.getColumnCount(); i++) {
					for (Field f : formFields) {
						if (f.getName().equals(formTable.getColumnName(i))) {
							if (ValueBuilder.value(f).getDataType() == DataType.BOOLEAN) {
								r.addValue(new Value(Boolean.valueOf(f.getDefault()), DataType.BOOLEAN));
							} else if (ValueBuilder.value(f).getDataType() == DataType.DOUBLE) {
								r.addValue(new Value(Double.valueOf(f.getDefault()), DataType.DOUBLE));
							} else if (ValueBuilder.value(f).getDataType() == DataType.INTEGER) {
								r.addValue(new Value(Integer.valueOf(f.getDefault()), DataType.INTEGER));
							} else {
								r.addValue(new Value(f.getDefault(), ValueBuilder.value(f).getDataType()));
							}
						}
					}
				}
			}

			formTable.addRow(r);

			checkWorkingTime(getTextFromControl(Constants.FORM_BOOKINGDATE),
					getTextFromControl(Constants.FORM_STARTDATE), //
					getTextFromControl(Constants.FORM_ENDDATE), //
					getTextFromControl(Constants.FORM_RENDEREDQUANTITY), //
					getTextFromControl(Constants.FORM_CHARGEDQUANTITY), //
					formTable, //
					r);
		}
	}

	/**
	 * Diese Methode ließt die Daten aus den Feldern / Controls und gibt einen
	 * String zurück. Der String kann auch null sein.
	 *
	 * @param constant
	 * @return
	 */
	String getTextFromControl(String constant) {
		Control control = controls.get(constant);
		String text = null;
		if (control instanceof Text) {
			text = ((Text) controls.get(constant)).getText();
		} else if (control instanceof TextAssist) {
			text = ((TextAssist) controls.get(constant)).getText();
		}
		return text;
	}

	/**
	 * Eine Methode, welche eine Anfrage an den CAS versendet um zu überprüfen, ob
	 * eine Überschneidung in den Arbeitszeiten vorliegt
	 *
	 * @param bookingDate
	 * @param startDate
	 * @param endDate
	 * @param renderedQuantity
	 * @param chargedQuantity
	 * @param t
	 * @param r
	 */
	private void checkWorkingTime(String bookingDate, String startDate, String endDate, String renderedQuantity,
			String chargedQuantity, Table t, Row r) {
		boolean contradiction = false;

		// Prüfen, ob die bemessene Arbeitszeit der differenz der Stunden entspricht
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		LocalDate localDate = LocalDate.parse(bookingDate, df);
		LocalDateTime localDateTime = localDate.atTime(0, 0);
		ZonedDateTime zdtBooking = localDateTime.atZone(ZoneId.of(timezone));
		r.setValue(new Value(zdtBooking.toInstant()), t.getColumnIndex(Constants.FORM_BOOKINGDATE));
		LocalTime timeEndDate = LocalTime.parse(endDate);
		LocalTime timeStartDate = LocalTime.parse(startDate);

		LocalDateTime localEndDate = localDate.atTime(timeEndDate);
		ZonedDateTime zdtEnd = localEndDate.atZone(ZoneId.of(timezone));
		r.setValue(new Value(zdtEnd.toInstant()), t.getColumnIndex(Constants.FORM_ENDDATE));
		LocalDateTime localStartDate = localDate.atTime(timeStartDate);
		ZonedDateTime zdtStart = localStartDate.atZone(ZoneId.of(timezone));
		r.setValue(new Value(zdtStart.toInstant()), t.getColumnIndex(Constants.FORM_STARTDATE));
		r.setValue(new Value(Double.valueOf(chargedQuantity)), t.getColumnIndex(Constants.FORM_CHARGEDQUANTITY));
		r.setValue(new Value(Double.valueOf(renderedQuantity)), t.getColumnIndex(Constants.FORM_RENDEREDQUANTITY));

		float timeDifference = ((timeEndDate.getHour() * 60) + timeEndDate.getMinute())
				- ((timeStartDate.getHour() * 60) + timeStartDate.getMinute());
		timeDifference = timeDifference / 60;

		float renderedQuantityFloat = Float.parseFloat(renderedQuantity);
		float chargedQuantityFloat = Float.parseFloat(chargedQuantity);
		if (timeDifference != renderedQuantityFloat) {
			contradiction = true;
		}
		if ((renderedQuantityFloat + 0.25 < chargedQuantityFloat)) {
			contradiction = true;
		}
		// Anfrage an den CAS um zu überprüfen, ob für den Mitarbeiter im angegebenen
		// Zeitrahmen bereits einträge existieren
		sendSaveRequest(t, contradiction);
	}

	private void sendSaveRequest(Table t, boolean contradiction) {
		if (t.getRows() != null && contradiction != true) {
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
		}
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
			// openNotificationPopup("Entry could not be deleted:" +
			// responce.getResultSet());
			Row r = responce.getResultSet().getRows().get(0);
			MessageDialog.openError(shell, "Error while deleting Entry",
					r.getValue(responce.getResultSet().getColumnIndex("Message")).getStringValue());
		} else {
			openNotificationPopup("Sucessfully deleted the entry");
			Map<MPerspective, String> map = new HashMap<>();
			map.put(perspective, Constants.DELETE_REQUEST);
			clearFields(map);
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
	 * Diese Methode bereiningt die Felder nach einer Erfolgreichen CAS-Anfrage
	 *
	 * @param origin
	 */
	@Optional
	@Inject
	public void clearFields(@UIEventTopic(Constants.BROKER_CLEARFIELDS) Map<MPerspective, String> map) {
		if (map.get(perspective) != null) {
			String origin = map.get(perspective);
			for (Control c : controls.values()) {
				if (c instanceof Text) {
					Text t = (Text) c;
					if (origin.equals(Constants.DELETE_REQUEST)) {
						t.setText("");
					} else if (c.getData(Constants.CONTROL_FIELD) == controls.get(Constants.FORM_BOOKINGDATE)
							.getData(Constants.CONTROL_FIELD)) {
						SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
						Date date = new Date(System.currentTimeMillis());
						t.setText(formatter.format(date));
					} else if (c.getData(Constants.CONTROL_FIELD) == controls.get(Constants.FORM_STARTDATE)
							.getData(Constants.CONTROL_FIELD)) {
						Text endDate = (Text) controls.get(Constants.FORM_ENDDATE);
						if (endDate.getText() != "" && !origin.equals(Constants.CLEAR_REQUEST)) {
							lastEndDate = endDate.getText();
						}
						t.setText(lastEndDate);
					} else {
						Field f = (Field) c.getData(Constants.CONTROL_FIELD);
						t.setText("");

					}
				}

				if (c instanceof LookupControl) {
					LookupControl lc = (LookupControl) c;
					lc.setText("");
					lc.setData(Constants.CONTROL_KEYLONG, null);
					lc.getDescription().setText("");
				}
				setKeys(null);
			}
			/*
			 * Nachdem alle felder bereinigt wurden wird der benutzer auf dem wert aus den
			 * preferences gesetzt. Hierfür wird eine frische Anfrage an den CAS versendet
			 * um zu gewährleisten, das wir diesen Eintrag auch derzeit in der Anwendung
			 * haben
			 */
			if (!origin.equals(Constants.DELETE_REQUEST)) {
				LookupControl lc = (LookupControl) controls.get(Constants.EMPLOYEEKEY);
				lc.setText(employee);
				CompletableFuture<?> tableFuture;
				tableFuture = LookupCASRequestUtil.getRequestedTable(0, null,
						(Field) lc.getData(Constants.CONTROL_FIELD), controls, dataService, sync, "List");

				tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
					if (ta instanceof Table) {
						Table t1 = (Table) ta;
						for (Row r : t1.getRows()) {
							if (r.getValue(t1.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue().toLowerCase()
									.equals(employee.toLowerCase())) {
								lc.setText(r.getValue(t1.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue());
								if (r.getValue(t1.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
									lc.getDescription().setText(r
											.getValue(t1.getColumnIndex(Constants.TABLE_DESCRIPTION)).getStringValue());
								}
								lc.setData(Constants.CONTROL_KEYLONG,
										r.getValue(t1.getColumnIndex(Constants.TABLE_KEYLONG)));
							}
						}
						// changeOptionsForLookupField(t1, lc, true);
					}

				}));
			}
		}
	}

	/**
	 * Antworten des CAS für Ticketnummern werden hier ausgelesen, so das sie wie
	 * bei einem Aufruf in der Index-Tabelle ausgewertet werden können
	 *
	 * @param recievedTable
	 */
	@Optional
	@Inject
	public void getTicket(@UIEventTopic(Constants.RECEIVED_TICKET) Table recievedTable) {

		for (Control c : controls.values()) {
			if (c instanceof LookupControl) {
				LookupControl lc = (LookupControl) c;
				if (lc != controls.get(Constants.EMPLOYEEKEY)) {
					lc.setText("");
					lc.setData(Constants.CONTROL_KEYLONG, null);
					lc.getDescription().setText("");
				}
			}
		}
		Row recievedRow = recievedTable.getRows().get(0);
		if (selectedTable == null) {
			selectedTable = dataFormService.getTableFromFormDetail(form, Constants.READ_REQUEST);
			selectedTable.addRow();
		} else if (selectedTable.getRows() == null) {
			selectedTable.addRow();
		}
		Row r = selectedTable.getRows().get(0);
		for (int i = 0; i < r.size(); i++) {
			if ((recievedTable.getColumnIndex(selectedTable.getColumnName(i))) >= 0) {
				r.setValue(recievedRow.getValue(recievedTable.getColumnIndex(selectedTable.getColumnName(i))), i);
			} else {
				Control c = controls.get(selectedTable.getColumnName(i));
				if (c instanceof LookupControl) {
					LookupControl lc = (LookupControl) c;
					r.setValue(new Value(lc.getData(Constants.CONTROL_KEYLONG), DataType.INTEGER), i);
				} else if (c instanceof Text) {
					Text t = (Text) c;
					if (t.getText() != null) {
						r.setValue(new Value(t.getText(), DataType.STRING), i);
					} else {
						r.setValue(new Value("", DataType.STRING), i);
					}
				}
			}
		}

		updateSelectedEntry();
	}

	public Map<String, Control> getControls() {
		return controls;
	}

	public List<ArrayList> getKeys() {
		return keys;
	}

	public void setKeys(List<ArrayList> keys) {
		this.keys = keys;
	}
}
