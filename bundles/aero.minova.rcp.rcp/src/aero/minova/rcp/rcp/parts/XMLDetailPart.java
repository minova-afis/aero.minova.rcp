package aero.minova.rcp.rcp.parts;

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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.SWT;
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
import aero.minova.rcp.rcp.util.LookupFieldFocusListener;
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

	@Inject
	@Preference(nodePath = "aero.minova.rcp.preferencewindow", value = "timezone")
	String timezone;

	@Inject
	@Preference(nodePath = "aero.minova.rcp.preferencewindow", value = "user")
	String employee;

	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Composite parent;

	private Map<String, Control> controls = new HashMap<>();
	private int entryKey = 0;
	private List<ArrayList> keys = null;
	private Table selectedTable;
	private Form form;
	private String lastEndDate = "";
	private MPerspective perspective = null;

	@PostConstruct
	public void createComposite(Composite parent, MPerspective perspective) {

		this.perspective = perspective;
		// Top-Level_Elemen
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

		for (Control c : controls.values()) {
			// Automatische anpassung der Quantitys, sobald sich die Zeiteinträge verändern
			if ((c.getData(Constants.CONTROL_FIELD) == controls.get(Constants.FORM_STARTDATE)
					.getData(Constants.CONTROL_FIELD))
					|| (c.getData(Constants.CONTROL_FIELD) == controls.get(Constants.FORM_ENDDATE)
							.getData(Constants.CONTROL_FIELD))) {
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
					text.addFocusListener(tfv);
				}
				if (field.getShortDate() != null || field.getLongDate() != null || field.getDateTime() != null
						|| field.getShortTime() != null) {
					text.setData(Constants.FOCUSED_ORIGIN, this);
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
			if (c instanceof LookupControl) {
				LookupFieldFocusListener lfl = new LookupFieldFocusListener(broker, dataService);
				LookupControl lc = (LookupControl) c;
				lc.addFocusListener(lfl);
				// Timer timer = new Timer();
				// Hinzufügen von Keylistenern, sodass die Felder bei Eingaben
				// ihre Optionen auflisten können und ihren Wert bei einem Treffer übernehmen
				lc.addKeyListener(new KeyListener() {
					boolean controlPressed = false;

					@Override
					public void keyPressed(KeyEvent e) {
						// TODO Auto-generated method stub
						if (e.keyCode == SWT.CONTROL) {
							controlPressed = true;
						}
					}

					@Override
					public void keyReleased(KeyEvent e) {
						if (e.keyCode == SWT.CONTROL) {
							controlPressed = false;
						} else
						// PFeiltastenangaben, Enter und TAB sollen nicht den Suchprozess auslösen
						if (e.keyCode != SWT.ARROW_DOWN && e.keyCode != SWT.ARROW_LEFT && e.keyCode != SWT.ARROW_RIGHT
								&& e.keyCode != SWT.ARROW_UP && e.keyCode != SWT.TAB && e.keyCode != SWT.CR) {
							if (lc.getData(Constants.CONTROL_OPTIONS) == null || lc.getText().equals("")) {
								requestOptionsFromCAS(lc);
							} else {
								changeSelectionBoxList(lc, false);
							}
							// Wird die untere Pfeiltaste eingeben, so sollen sämtliche Optionen,
							// wie auch bei einem Klick auf das Twiste, angezeigt werden
							// PROBLEM: durch die Optionen wechseln via pfeiltasten so nicht möglich
						} else if (e.keyCode == SWT.SPACE && controlPressed == true) {
							Field field = (Field) lc.getData(Constants.CONTROL_FIELD);
							broker.post("LoadAllLookUpValues", field.getName());
						} else if (e.keyCode == SWT.ARROW_DOWN && lc.isProposalPopupOpen() == false) {
							if (lc.getData(Constants.CONTROL_OPTIONS) != null) {
								changeSelectionBoxList(c, false);
							} else {
								Field field = (Field) lc.getData(Constants.CONTROL_FIELD);
								broker.post("WFCLoadAllLookUpValues", field.getName());
							}
						}
					}

				});
			}
		}
	}

	/**
	 * Aktuellisiert die Quantityvalues, sobald sich einer der beiden Zeiteinträge
	 * verändert
	 */
	public void updateQuantitys() {
		Text endDate = (Text) controls.get(Constants.FORM_ENDDATE);
		Text startDate = (Text) controls.get(Constants.FORM_STARTDATE);
		if (endDate.getText().matches("..:..") && startDate.getText().matches("..:..")) {
			LocalTime timeEndDate = LocalTime.parse(endDate.getText());
			LocalTime timeStartDate = LocalTime.parse(startDate.getText());
			float timeDifference = ((timeEndDate.getHour() * 60) + timeEndDate.getMinute())
					- ((timeStartDate.getHour() * 60) + timeStartDate.getMinute());
			timeDifference = timeDifference / 60;
			Text renderedField = (Text) controls.get(Constants.FORM_RENDEREDQUANTITY);
			Text chargedField = (Text) controls.get(Constants.FORM_CHARGEDQUANTITY);
			String renderedValue;
			String chargedValue;
			if (timeDifference >= 0) {
				Double quarter = (double) Math.round(timeDifference * 4) / 4f;
				Double half = (double) Math.round(timeDifference * 2) / 2f;
				String chargedFormat = "%1."
						+ ((Field) chargedField.getData(Constants.CONTROL_FIELD)).getNumber().getDecimals() + "f";
				String renderedFormat = "%1."
						+ ((Field) renderedField.getData(Constants.CONTROL_FIELD)).getNumber().getDecimals() + "f";
				chargedValue = String.format(chargedFormat, half);
				chargedValue = chargedValue.replace(',', '.');
				renderedValue = String.format(renderedFormat, quarter);
				renderedValue = renderedValue.replace(',', '.');
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
				changeOptionsForLookupField(sql.getResultSet(), c, false);
			} else if (ta instanceof Table) {
				Table t = (Table) ta;
				changeOptionsForLookupField(t, c, false);
			}

		}));
	}

	/**
	 * Tauscht die Optionen aus, welche dem LookupField zur Verfügung stehen
	 *
	 * @param ta
	 * @param c
	 */
	public void changeOptionsForLookupField(Table ta, Control c, boolean twisty) {
		c.setData(Constants.CONTROL_OPTIONS, ta);
		changeSelectionBoxList(c, twisty);
	}

	/**
	 * Diese Mtethode setzt die den Ausgewählten Wert direkt in das Control oder
	 * lässt eine Liste aus möglichen Werten zur Auswahl erscheinen.
	 *
	 * @param c
	 */
	public void changeSelectionBoxList(Control c, boolean twisty) {
		if (c.getData(Constants.CONTROL_OPTIONS) != null) {
			Table t = (Table) c.getData(Constants.CONTROL_OPTIONS);
			LookupControl lc = (LookupControl) c;
			Field field = (Field) c.getData(Constants.CONTROL_FIELD);
			// Existiert nur ein Wert für das gegebene Feld, so wird überprüft ob die
			// Eingabe gleich dem gesuchten Wert ist.
			// Ist dies der Fall, so wird dieser Wert ausgewählt. Ansonsten wird der Wert
			// aus dem CAS als Option/Proposal aufgelistet
			if (t.getRows().size() == 1) {
				if (lc != null && lc.getText() != null && twisty == false) {
					Value value = t.getRows().get(0).getValue(t.getColumnIndex(Constants.TABLE_KEYTEXT));
					if (value.getStringValue().equalsIgnoreCase(lc.getText().toString())) {
						sync.asyncExec(() -> DetailUtil.updateSelectedLookupEntry(t, c));
						lc.setData(Constants.CONTROL_KEYLONG,
								t.getRows().get(0).getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)));
					}
					// Setzen der Proposals/Optionen
					changeProposals((LookupControl) c, t);

				} else {
					sync.asyncExec(() -> DetailUtil.updateSelectedLookupEntry(t, c));
					System.out.println(t.getRows().get(0).getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)));
					c.setData(Constants.CONTROL_KEYLONG,
							t.getRows().get(0).getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)).getValue());
					// Setzen der Proposals/Optionen
					changeProposals((LookupControl) c, t);
				}
			} else {
				if (lc != null && lc.getText() != null && twisty == false) {
					// Aufbau einer gefilterten Tabelle, welche nur die Werte aus dem CAS enthält,
					// die den Text im Field am Anfang stehen haben
					Table filteredTable = new Table();
					// Übernahme sämtlicher Columns
					for (aero.minova.rcp.model.Column column : t.getColumns()) {
						filteredTable.addColumn(column);
					}
					// Trifft der Text nicht überein, so wird auserdem die Description überprüft
					for (Row r : t.getRows()) {
						if ((r.getValue(t.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue().toLowerCase()
								.startsWith(lc.getText().toLowerCase()))) {
							filteredTable.addRow(r);
						} else if (r.getValue(t.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
							if ((r.getValue(t.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue().toLowerCase()
									.startsWith(r.getValue(t.getColumnIndex(Constants.TABLE_DESCRIPTION))
											.getStringValue().toLowerCase()))) {
								filteredTable.addRow(r);
							}
						}

					}
					// Existiert genau 1 Treffer, so wird geschaut ob dieser bereits 100%
					// übereinstimmt. Tut er dies, so wird statt dem setzen des Proposals direkt der
					// Wert gesetzt
					if (filteredTable.getRows().size() == 1
							&& (filteredTable.getRows().get(0)
									.getValue(filteredTable.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue()
									.toLowerCase().equals(lc.getText().toLowerCase()))
							|| (filteredTable.getRows().get(0)
									.getValue(filteredTable.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null
									&& filteredTable.getRows().get(0)
											.getValue(filteredTable.getColumnIndex(Constants.TABLE_DESCRIPTION))
											.getStringValue().toLowerCase().equals(lc.getText().toLowerCase()))) {
						c.setData(Constants.CONTROL_KEYLONG, filteredTable.getRows().get(0)
								.getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)).getValue());
						sync.asyncExec(() -> DetailUtil.updateSelectedLookupEntry(filteredTable, c));
						changeProposals(lc, t);
						// Setzen der Proposals/Optionen
					} else {
						changeProposals((LookupControl) lc, filteredTable);
						lc.setData(Constants.CONTROL_KEYLONG, null);
					}
					// Setzen der Proposals/Optionen
				} else {
					changeProposals((LookupControl) lc, t);
					lc.setData(Constants.CONTROL_KEYLONG, null);
				}

			}
		}
	}

	/**
	 * Austauschen der gegebenen Optionen für das LookupField
	 *
	 * @param c
	 * @param t
	 */
	public void changeProposals(LookupControl lc, Table t) {
		lc.setProposals(t);
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
					changeOptionsForLookupField(sql.getResultSet(), control, true);
				} else if (ta instanceof Table) {
					Table t1 = (Table) ta;
					changeOptionsForLookupField(t1, control, true);
				}

			}));
		}

	}

	/**
	 * Bei Auswahl eines Indexes wird anhand der in der Row vorhandenen Daten eine
	 * Anfrage an den CAS versendet, um sämltiche Informationen zu erhalten
	 *
	 * @param rows
	 */

	@Inject
	public void changeSelectedEntry(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) List<Row> rows,
			ESelectionService selectionService) {

		if (rows != null) {

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
	public void buildSaveTable(@UIEventTopic("SaveEntry") MPerspective perspective) {
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
			checkWorkingTime(((Text) controls.get(Constants.FORM_BOOKINGDATE)).getText(),
					((Text) controls.get(Constants.FORM_STARTDATE)).getText(),
					((Text) controls.get(Constants.FORM_ENDDATE)).getText(),
					((Text) controls.get(Constants.FORM_RENDEREDQUANTITY)).getText(),
					((Text) controls.get(Constants.FORM_CHARGEDQUANTITY)).getText(), formTable, r);
		}
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
	private void checkNewEntryInsert(int responce) {
		if (responce != 1) {
			openNotificationPopup("Entry could not be added");
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
	public void buildDeleteTable(@UIEventTopic("DeleteEntry") MPerspective perspective) {
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
						deleteEntry(ta.getReturnCode());
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
	public void deleteEntry(int responce) {
		if (responce != 1) {
			openNotificationPopup("Entry could not be deleted");
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
	public void clearFields(@UIEventTopic("clearFields") Map<MPerspective, String> map) {
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

	public Map<String, Control> getControls() {
		return controls;
	}

	public List<ArrayList> getKeys() {
		return keys;
	}

	public void setKeys(List<ArrayList> keys) {
		this.keys = keys;
	}

	public String getTimeZone() {
		return timezone;
	}

}
