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
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
import aero.minova.rcp.rcp.widgets.LookupControl;

public class WFCDetailCASRequestsUtil {

	@Inject
	protected UISynchronize sync;

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private IDataService dataService;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	private Map<String, Control> controls = null;

	private List<ArrayList> keys = null;

	private Table selectedTable = null;

	private Form form;

	public WFCDetailCASRequestsUtil(Map<String, Control> controls, List<ArrayList> keys, Table selectedTable,
			Form form) {
		this.controls = controls;
		this.keys = keys;
		this.selectedTable = selectedTable;
		this.form = form;

	}

	/**
	 * Bei Auswahl eines Indexes wird anhand der in der Row vorhandenen Daten eine
	 * Anfrage an den CAS versendet, um sämltiche Informationen zu erhalten
	 *
	 * @param rows
	 */

	@Inject
	public void changeSelectedEntry(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) List<Row> rows) {
		if (rows != null) {

			Row row = rows.get(0);
			Table rowIndexTable = dataFormService.getTableFromFormDetail(form, "Read");

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
		Table table = selectedTable;

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

	/**
	 * Erstellen einer Update-Anfrage oder einer Insert-Anfrage an den CAS,abhängig
	 * der gegebenen Keys
	 *
	 * @param obj
	 */
	@Inject
	@Optional
	public void buildSaveTable(@UIEventTopic("SaveEntry") Object obj) {
		Table formTable = null;
		RowBuilder rb = RowBuilder.newRow();

		if (getKeys() != null) {
			formTable = dataFormService.getTableFromFormDetail(form, "Update");
		} else {
			formTable = dataFormService.getTableFromFormDetail(form, "Insert");
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
		checkWorkingTime(((Text) controls.get("BookingDate")).getText(), ((Text) controls.get("StartDate")).getText(),
				((Text) controls.get("EndDate")).getText(), ((Text) controls.get("RenderedQuantity")).getText(),
				((Text) controls.get("ChargedQuantity")).getText(), formTable, r);
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
		ZonedDateTime zdtBooking = localDateTime.atZone(ZoneId.of("Europe/Berlin"));
		r.setValue(new Value(zdtBooking.toInstant()), t.getColumnIndex("BookingDate"));
		LocalTime timeEndDate = LocalTime.parse(endDate);
		LocalTime timeStartDate = LocalTime.parse(startDate);

		LocalDateTime localEndDate = localDate.atTime(timeEndDate);
		ZonedDateTime zdtEnd = localEndDate.atZone(ZoneId.of("Europe/Berlin"));
		r.setValue(new Value(zdtEnd.toInstant()), t.getColumnIndex("EndDate"));
		LocalDateTime localStartDate = localDate.atTime(timeStartDate);
		ZonedDateTime zdtStart = localStartDate.atZone(ZoneId.of("Europe/Berlin"));
		r.setValue(new Value(zdtStart.toInstant()), t.getColumnIndex("StartDate"));
		r.setValue(new Value(Double.valueOf(chargedQuantity)), t.getColumnIndex("ChargedQuantity"));
		r.setValue(new Value(Double.valueOf(renderedQuantity)), t.getColumnIndex("RenderedQuantity"));

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
					checkNewEntryInsert(tr.getReturnCode());
				}));
			} else {
				tableFuture.thenAccept(tr -> sync.asyncExec(() -> {
					checkEntryUpdate(tr.getReturnCode());
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
	private void checkEntryUpdate(int responce) {
		if (responce != 1) {
			openNotificationPopup("Entry could not be updated");
		} else {
			openNotificationPopup("Sucessfully updated the entry");
			clearFields("Update");
		}
	}

	/**
	 * Überprüft, ob der neue Eintrag erstellt wurde
	 *
	 * @param responce
	 */
	private void checkNewEntryInsert(int responce) {
		if (responce != 1) {
			openNotificationPopup("Entry could not be added");
		} else {
			openNotificationPopup("Sucessfully added the entry");
			clearFields("Insert");
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
	public void buildDeleteTable(@UIEventTopic("DeleteEntry") Object obj) {
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
					deleteEntry(ta.getReturnCode());
				}));
			}
		}
	}

	/**
	 * Überprüft, ob die Anfrage erfolgreich war, falls nicht bleiben die Textfelder
	 * befüllt um die Anfrage anzupassen
	 *
	 * @param responce
	 */
	public void deleteEntry(int responce) {
		if (responce != 1) {
			openNotificationPopup("Entry could not be deleted");
		} else {
			openNotificationPopup("Sucessfully deleted the entry");
			clearFields("Delete");
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
	public void clearFields(@UIEventTopic("clearFields") String origin) {
		for (Control c : controls.values()) {
			if (c instanceof Text) {
				Text t = (Text) c;
				if (origin.equals("Delete")) {
					t.setText("");
				} else if (c.getData("field") == controls.get("BookingDate").getData("field")) {
					SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
					Date date = new Date(System.currentTimeMillis());
					t.setText(formatter.format(date));
				} else if (c.getData("field") == controls.get("StartDate").getData("field")
						&& origin.equals("Insert")) {
					Text endDate = (Text) controls.get("EndDate");
					t.setText(endDate.getText());
				} else {
					Field f = (Field) c.getData("field");
					if (f.getNumber() != null) {
						t.setText("0");
					} else {
						t.setText("");
					}
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
