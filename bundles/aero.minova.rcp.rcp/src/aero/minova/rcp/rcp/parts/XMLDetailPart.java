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
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
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

		for (Object o : form.getDetail().getHeadAndPage()) {
			if (o instanceof Head) {
				Head head = (Head) o;
				Composite detailFieldComposite = DetailUtil.createSection(formToolkit, parent, head);
				for (Object fieldOrGrid : head.getFieldOrGrid()) {
					if (fieldOrGrid instanceof Field) {
						DetailUtil.createField((Field) fieldOrGrid, detailFieldComposite, controls);
					}
				}
			} else if (o instanceof Page) {
				Page page = (Page) o;
				Composite detailFieldComposite = DetailUtil.createSection(formToolkit, parent, page);
				for (Object fieldOrGrid : page.getFieldOrGrid()) {
					if (fieldOrGrid instanceof Field) {
						DetailUtil.createField((Field) fieldOrGrid, detailFieldComposite, controls);

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
			if ((c.getData("field") == controls.get("StartDate").getData("field"))
					|| (c.getData("field") == controls.get("EndDate").getData("field"))) {
				c.addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent e) {
					}

					@Override
					public void keyReleased(KeyEvent e) {
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

				});
			}
			// Hinzufügen der Listener, um die Verification der Werte zu gewährleisten
			if (c instanceof Text) {
				TextfieldVerifier tfv = new TextfieldVerifier();
				Text text = (Text) c;
				Field field = (Field) c.getData("field");
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
		// Einfügen eines Listeners, welche auf Eingaben im LookupField reagiert
		Display.getCurrent().addFilter(SWT.KeyUp, event -> {
			Widget w = event.widget;
			for (Control c : controls.values()) {
				if (c instanceof LookupControl) {
					if (w == ((Composite) c).getChildren()[1]) {
						if (((LookupControl) c).getText().length() == 1 && event.character != '\b') {
							requestOptionsFromCAS(c);
						} else {
							changeShownOptions(c);
						}
					}
				}

			}
		});
	}

	// Eigentliche CAS abfrage anhand des gegebenen KeyTextes
	public void requestOptionsFromCAS(Control c) {
		Field field = (Field) c.getData("field");
		CompletableFuture<?> tableFuture;
		tableFuture = LookupCASRequestUtil.getRequestedTable(0, ((LookupControl) c).getText(), field, controls,
				dataService, sync, "List");
		tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
			if (ta instanceof SqlProcedureResult) {
				SqlProcedureResult sql = (SqlProcedureResult) ta;
				changeOptionsForLookupField(sql.getOutputParameters(), c);
			} else if (ta instanceof Table) {
				Table t = (Table) ta;
				changeOptionsForLookupField(t, c);
			}

		}));
	}

	// Tauscht die Optionen aus, welche dem LookupField zur Verfügung stehen
	public void changeOptionsForLookupField(Table ta, Control c) {
		Map m = new HashMap<Integer, String>();
		for (Row r : ta.getRows()) {
			m.put(ValueBuilder.newValue(r.getValue(0)).create(), ValueBuilder.newValue(r.getValue(1)).create());
		}
		c.setData("options", m);
		changeShownOptions(c);
	}

	// Schränkt die angezeigten Optionen an, welche in der Map hinterlegt sind,
	// anhand des eingegebenen Strings
	// TODO: Die Optionen sind als Hashmap vorhanden, allerdings existiert noch kein
	// Object welches mit dieser Arbeitet
	public void changeShownOptions(Control c) {
		if (c.getData("options") != null) {
			Map<Integer, String> optionsMap = (Map<Integer, String>) c.getData("options");
			Map<Integer, String> shownOptionsMap = new HashMap<Integer, String>();
			int i = 0;
			for (String s : optionsMap.values()) {
				Integer keyLong = (Integer) optionsMap.keySet().toArray()[i];
				if (s.contains(((LookupControl) c).getText())) {
					shownOptionsMap.put(keyLong, s);
				}
				i++;
			}
			c.setData("shownOptions", shownOptionsMap);

			if (shownOptionsMap.size() == 1) {
				c.setData("keyLong", shownOptionsMap.keySet().toArray()[0]);
			} else {
				c.setData("keyLong", null);
			}
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
							al.add(ValueBuilder.newValue(row.getValue(i)).dataType());
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
				Consumer<Table> consumer = (Consumer<Table>) c.getData("consumer");
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

				Consumer<Map> lookupConsumer = (Consumer<Map>) c.getData("lookupConsumer");
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
						if (c.getData("keyLong") != null) {
							rb.withValue(c.getData("keyLong"));
						} else {
							rb.withValue(null);
						}
					}
				}
				i++;
			}
			valuePosition++;
		}

		// TODO: spelling/felder collumns ohne zugehöriges feld mit wert null versorgen

		Row r = rb.create();
		if (controls.size() < formTable.getColumnCount()) {
			while (r.size() < formTable.getColumnCount()) {
				r.addValue(null);
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
		if ((renderedQuantityFloat < chargedQuantityFloat)) {
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
					System.out.println("test");
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
					"Entry could not be updated",
					shell);
			notificationPopUp.open();
			return;
		} else {
			NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(),
					"Sucessfully updated the entry",
					shell);
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
					"Sucessfully added the entry",
					shell);
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
			if (!("sp".equals(form.getDetail().getProcedurePrefix())
					|| "op".equals(form.getDetail().getProcedurePrefix()))) {
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
					"Entry could not be deleted",
					shell);
			notificationPopUp.open();
		} else {
			NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(),
					"Sucessfully deleted the entry",
					shell);
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
