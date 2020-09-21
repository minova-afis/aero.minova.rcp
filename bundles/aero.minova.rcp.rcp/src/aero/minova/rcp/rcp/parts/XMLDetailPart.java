package aero.minova.rcp.rcp.parts;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.plugin1.model.Row;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.builder.RowBuilder;
import aero.minova.rcp.plugin1.model.builder.TableBuilder;
import aero.minova.rcp.plugin1.model.builder.ValueBuilder;
import aero.minova.rcp.plugin1.textfieldVerifier.TextfieldVerifier;
import aero.minova.rcp.rcp.util.DetailUtil;
import aero.minova.rcp.rcp.util.LookupCASRequestUtil;
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

	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Composite parent;

	private Map<String, Control> controls = new HashMap<>();
	private int entryKey = 0;
	private Table selectedTable;

	@PostConstruct
	public void createComposite(Composite parent) {
		// Top-Level_Element
		parent.setLayout(new GridLayout(1, true));
		this.parent = parent;

		Form form = dataFormService.getForm();

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
				requestOptionsFromCAS(c);
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
		CompletableFuture<Table> tableFuture;
		tableFuture = LookupCASRequestUtil.getRequestedTable(0, ((LookupControl) c).getText(), field, controls,
				dataService, sync);
		tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
			changeOptionsForLookupField(ta, c);
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
		if (rows == null || rows.isEmpty()) {
			return;
		}
		int keylong = 0;
		Row row = rows.get(0);
		if (row.getValue(0).getIntegerValue() == null) {
			return;
		} else {
			keylong = row.getValue(0).getIntegerValue();
		}
		entryKey = keylong;
		Table rowIndexTable = TableBuilder.newTable("spReadWorkingTime").withColumn("KeyLong", DataType.INTEGER)//
				.withColumn("EmployeeKey", DataType.STRING)//
				.withColumn("OrderReceiverKey", DataType.STRING)//
				.withColumn("ServiceContractKey", DataType.STRING)//
				.withColumn("ServiceObjectKey", DataType.STRING)//
				.withColumn("ServiceKey", DataType.STRING)//
				.withColumn("BookingDate", DataType.ZONED)//
				.withColumn("StartDate", DataType.ZONED)//
				.withColumn("EndDate", DataType.ZONED)//
				.withColumn("RenderedQuantity", DataType.DOUBLE)//
				.withColumn("ChargedQuantity", DataType.DOUBLE)//
				.withColumn("Description", DataType.STRING)//
				.withColumn("Spelling", DataType.BOOLEAN).withKey(keylong).create();
		CompletableFuture<Table> tableFuture = dataService.getDetailDataAsync(rowIndexTable.getName(), rowIndexTable);
		tableFuture.thenAccept(t -> sync.asyncExec(() -> {
			selectedTable = t;
			updateSelectedEntry();
		}));
	}
	// verarbeitung empfangenen Tabelle des CAS mit Bindung der Detailfelder mit den
	// daraus erhaltenen Daten, dies erfolgt durch die Consume-Methode
	public void updateSelectedEntry() {
		Table table = selectedTable;
		table = getTestTable();

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
	// Testdaten, welche nach erfolgreicher CAS-Abfrage gelöscht werden
	public Table getTestTable() {
		Table rowIndexTable = TableBuilder.newTable("spReadWorkingTime").withColumn("KeyLong", DataType.INTEGER)
				.withColumn("EmployeeKey", DataType.STRING).withColumn("OrderReceiverKey", DataType.STRING)
				.withColumn("ServiceContractKey", DataType.STRING).withColumn("ServiceObjectKey", DataType.STRING)
				.withColumn("ServiceKey", DataType.STRING).withColumn("BookingDate", DataType.ZONED)
				.withColumn("StartDate", DataType.ZONED).withColumn("EndDate", DataType.ZONED)
				.withColumn("RenderedQuantity", DataType.DOUBLE).withColumn("ChargedQuantity", DataType.DOUBLE)
				.withColumn("Description", DataType.STRING).withColumn("Spelling", DataType.BOOLEAN).create();

		Row r = RowBuilder.newRow()//
				.withValue(3)//
				.withValue(3)//
				.withValue(44)//
				.withValue(55)//
				.withValue(66)//
				.withValue(77)//
				.withValue(ZonedDateTime.of(1968, 12, 18, 00, 00, 0, 0, ZoneId.of("Europe/Berlin")))//
				.withValue(ZonedDateTime.of(1968, 12, 18, 18, 15, 0, 0, ZoneId.of("Europe/Berlin")))//
				.withValue(ZonedDateTime.of(1968, 12, 18, 18, 30, 0, 0, ZoneId.of("Europe/Berlin")))//
				.withValue(44.2)//
				.withValue(33.2)//
				.withValue("test")//
				.withValue(true)//
				.create();
		rowIndexTable.addRow(r);
		return rowIndexTable;
	}

	public Map<String, Control> getControls() {
		return controls;
	}

	public void setEntryKey(int entryKey) {
		this.entryKey = entryKey;

	}

	public int getEntryKey() {
		return entryKey;

	}
}
