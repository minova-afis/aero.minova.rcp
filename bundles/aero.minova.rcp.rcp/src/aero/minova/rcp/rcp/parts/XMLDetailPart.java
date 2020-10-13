package aero.minova.rcp.rcp.parts;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dialogs.NotificationPopUp;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.DetailUtil;
import aero.minova.rcp.rcp.util.LookupCASRequestUtil;
import aero.minova.rcp.rcp.util.TextfieldVerifier;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class XMLDetailPart {

	@Inject
	protected UISynchronize sync;

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private IDataService dataService;

	@Inject
	private IEventBroker broker;

	@Inject
	private TranslationService translationService;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	Shell shell;

	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Composite parent;

	private Map<String, Control> controls = new HashMap<>();
	private int entryKey = 0;
	private List<ArrayList> keys = null;
	private Table selectedTable;
	private Form form;

	@PostConstruct
	public void createComposite(Composite parent) {
		// Top-Level_Element
		parent.setLayout(new GridLayout(1, true));
		this.parent = parent;

		form = dataFormService.getForm();
		DetailUtil detailUtil = new DetailUtil(translationService, broker);

		for (Object o : form.getDetail().getHeadAndPage()) {
			if (o instanceof Head) {
				Head head = (Head) o;
				Composite detailFieldComposite = detailUtil.createSection(formToolkit, parent, head);
				for (Object fieldOrGrid : head.getFieldOrGrid()) {
					if (fieldOrGrid instanceof Field) {
						detailUtil.createField((Field) fieldOrGrid, detailFieldComposite, controls);
					}
				}
			} else if (o instanceof Page) {
				Page page = (Page) o;
				Composite detailFieldComposite = detailUtil.createSection(formToolkit, parent, page);
				for (Object fieldOrGrid : page.getFieldOrGrid()) {
					if (fieldOrGrid instanceof Field) {
						detailUtil.createField((Field) fieldOrGrid, detailFieldComposite, controls);

					}
				}
			}
		}
		// Erster Ansatz um selbstgeschriebene Eingaben in Lookupfields zu überprüfen,
		// garantiert das gültige Eingaben an den CAS versendet werden können
		for (Control c : controls.values()) {
			if (c instanceof LookupControl) {
				// requestOptionsFromCAS(c);
			}
			// Automatische anpassung der Quantitys, sobald sich die Zeiteinträge verändern
			if ((c.getData(Constants.CONTROL_FIELD) == controls.get("StartDate").getData(Constants.CONTROL_FIELD)) || (c
					.getData(Constants.CONTROL_FIELD) == controls.get("EndDate").getData(Constants.CONTROL_FIELD))) {
				c.addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent e) {
					}

					@Override
					public void keyReleased(KeyEvent e) {
						updateQuantitys();
					}

				});
			}
			// Hinzufügen der Listener, um die Verification der Werte zu gewährleisten
			if (c instanceof Text) {
				TextfieldVerifier tfv = new TextfieldVerifier();
				Text text = (Text) c;
				Field field = (Field) c.getData(Constants.CONTROL_FIELD);
				if (field.getNumber() != null) {
					text.addVerifyListener(e -> {
						if (e.character != '\b') {
							final String oldString = ((Text) e.getSource()).getText();
							String newString = oldString.substring(0, e.start) + e.text + oldString.substring(e.end);
							e.doit = tfv.verifyDouble(newString);
						}
					});
				}
				if (field.getShortDate() != null || field.getLongDate() != null || field.getDateTime() != null
						|| field.getShortTime() != null) {
					text.addFocusListener(tfv);
				}
				if (field.getText() != null) {
					text.addVerifyListener(e -> {
						if (e.character != '\b') {
							final String oldString = ((Text) e.getSource()).getText();
							String newString = oldString.substring(0, e.start) + e.text + oldString.substring(e.end);
							e.doit = tfv.verifyText(newString, field.getText().getLength());
						}
					});
				}
			}
		}
	}

	public void updateQuantitys() {
		Text endDate = (Text) controls.get("EndDate");
		Text startDate = (Text) controls.get("StartDate");
		if (endDate.getText().matches("..:..") && startDate.getText().matches("..:..")) {
			LocalTime timeEndDate = LocalTime.parse(endDate.getText());
			LocalTime timeStartDate = LocalTime.parse(startDate.getText());
			float timeDifference = ((timeEndDate.getHour() * 60) + timeEndDate.getMinute())
					- ((timeStartDate.getHour() * 60) + timeStartDate.getMinute());
			timeDifference = timeDifference / 60;
			Text renderedField = (Text) controls.get("RenderedQuantity");
			Text chargedField = (Text) controls.get("ChargedQuantity");
			String renderedValue;
			String chargedValue;
			if (timeDifference >= 0) {
				renderedValue = String.valueOf(Math.round(timeDifference * 4) / 4f);
				chargedValue = String.valueOf(Math.round(timeDifference * 2) / 2f);
			} else {
				renderedValue = "0";
				chargedValue = "0";
			}
			chargedField.setText(chargedValue);
			renderedField.setText(renderedValue);
		}
	}

	public void requestOptionsFromCAS(Control c) {
		Field field = (Field) c.getData(Constants.CONTROL_FIELD);
		CompletableFuture<?> tableFuture;
		tableFuture = LookupCASRequestUtil.getRequestedTable(0, ((LookupControl) c).getText(), field, controls,
				dataService, sync, "List");
		tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
			if (ta instanceof SqlProcedureResult) {
				SqlProcedureResult sql = (SqlProcedureResult) ta;
				changeOptionsForLookupField(sql.getResultSet(), c);
			} else if (ta instanceof Table) {
				Table t = (Table) ta;
				changeOptionsForLookupField(t, c);
			}

		}));
	}

	/**
	 * Tauscht die Optionen aus, welche dem LookupField zur Verfügung stehen
	 *
	 * @param ta
	 * @param c
	 */
	public void changeOptionsForLookupField(Table ta, Control c) {
		c.setData(Constants.CONTROL_OPTIONS, ta);
		changeSelectionBoxList(c);
	}

	/**
	 * Diese Mtethode setzt die den Ausgewählten Wert direkt in das Control oder
	 * lässt eine Liste aus möglichen Werten zur Auswahl erscheinen.
	 *
	 * @param c
	 */
	public void changeSelectionBoxList(Control c) {
		if (c.getData(Constants.CONTROL_OPTIONS) != null) {
			Table t = (Table) c.getData(Constants.CONTROL_OPTIONS);
			// TODO prüfen ob der Wert in der Row auch em angefragten Wert entspricht
			Field field = (Field) c.getData(Constants.CONTROL_FIELD);
			if (t.getRows().size() == 1) {
				if (field != null && field.getText() != null) {
					Value value = t.getRows().get(0).getValue(t.getColumnIndex(Constants.TABLE_KEYTEXT));
					if (value.getStringValue().equalsIgnoreCase(field.getText().toString())) {
						sync.asyncExec(() -> DetailUtil.updateSelectedLookupEntry(t, c));
					} else {
						// TODO "gu" != "MIN" es folgt:
						// TODO Hier muss aktiv eine neue Liste mit Werten angefragt werden
					}
				} else {
					sync.asyncExec(() -> DetailUtil.updateSelectedLookupEntry(t, c));

				}
			} else {
				// TODO
				// Auswahl der Liste von Treffern anzeigen (Aufpoppen)
				if (c instanceof LookupControl) {
					LookupControl lookupControl = (LookupControl) c;
					lookupControl.setProposals(t);
				}
				// c.setData("keyLong", null);
			}
		}
	}

	/**
	 * Auslesen aller bereits einhgetragenen key die mit diesem Controll in
	 * Zusammenhang stehen Es wird eine Liste von Ergebnissen Erstellt, diese wird
	 * dem benutzer zur verfügung gestellt.
	 *
	 * @param luc
	 */
	@Inject
	@Optional
	public void requestLookUpEntriesAll(@UIEventTopic("LoadAllLookUpValues") String name) {
		Control control = controls.get(name);

		if (control instanceof LookupControl) {
			Field field = (Field) control.getData(Constants.CONTROL_FIELD);
			CompletableFuture<?> tableFuture;
			tableFuture = LookupCASRequestUtil.getRequestedTable(0, null, field, controls, dataService, sync, "List");

			tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
				if (ta instanceof SqlProcedureResult) {
					SqlProcedureResult sql = (SqlProcedureResult) ta;
					changeOptionsForLookupField(sql.getResultSet(), control);
				} else if (ta instanceof Table) {
					Table t1 = (Table) ta;
					changeOptionsForLookupField(t1, control);
				}

			}));
		}

	}

	// Bei Auswahl eines Indexes wird anhand der in der Row vorhandenen Daten eine
	// Anfrage an den CAS versendet, um sämltiche Informationen zu erhalten
	@Inject
	public void changeSelectedEntry(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) List<Row> rows) {
		if (rows != null) {

			Row row = rows.get(0);
			Table rowIndexTable = dataFormService.getTableFromFormDetail(form, "Read");

			RowBuilder builder = RowBuilder.newRow();
			List<Field> allFields = dataFormService.getFieldsFromForm(form, false);

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

	// Verarbeitung der empfangenen Tabelle des CAS mit Bindung der Detailfelder mit
	// den daraus erhaltenen Daten, dies erfolgt durch die Consume-Methode
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

	// Erstellen einer Update-Anfrage oder einer Insert-Anfrage an den CAS,abhängig
	// der gegebenen Keys
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

		// TODO: spelling/collumns ohne zugehöriges feld mit default-wert versorgen
		// anhand der Maske wird der Defaultwert und der DataType des Fehlenden
		// Row-Wertes ermittelt und der Row angefügt
		Row r = rb.create();
		List<Field> formFields = dataFormService.getFieldsFromForm(form, false);
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

	// Eine Methode, welche eine Anfrage an den CAS versendet um zu überprüfen, ob
	// eine Überschneidung in den Arbeitszeiten vorliegt
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
			if (!(t.getColumnName(0).equals("KeyLong"))) {
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

	// Überprüft. ob das Update erfolgreich war
	private void checkEntryUpdate(int responce) {
		if (responce != 1) {
			NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(),
					"Entry could not be updated", shell);
			notificationPopUp.open();
			return;
		} else {
			NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(),
					"Sucessfully updated the entry", shell);
			notificationPopUp.open();
		}
	}

	// Überprüft, ob der neue Eintrag erstellt wurde
	private void checkNewEntryInsert(int responce) {
		if (responce != 1) {
			NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(), "Entry could not be added",
					shell);
			notificationPopUp.open();
		} else {
			NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(),
					"Sucessfully added the entry", shell);
			notificationPopUp.open();
		}
	}

	// Sucht die aktiven Controls aus der XMLDetailPart und baut anhand deren Werte
	// eine Abfrage an den CAS zusammen
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

	// Überprüft, ob die Anfrage erfolgreich war, falls nicht bleiben die Textfelder
	// befüllt um die Anfrage anzupassen
	public void deleteEntry(int responce) {
		if (responce != 1) {
			NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(),
					"Entry could not be deleted", shell);
			notificationPopUp.open();
		} else {
			NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(),
					"Sucessfully deleted the entry", shell);
			notificationPopUp.open();

			for (Control c : controls.values()) {
				if (c instanceof Text) {
					((Text) c).setText("");
				}
				if (c instanceof LookupControl) {
					((LookupControl) c).setText("");
				}
			}
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
