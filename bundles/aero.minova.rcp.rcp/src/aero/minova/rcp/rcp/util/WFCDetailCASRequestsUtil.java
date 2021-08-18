package aero.minova.rcp.rcp.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dialogs.NotificationPopUp;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.EventParam;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.form.model.xsd.Procedure;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.KeyType;
import aero.minova.rcp.model.OutputType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.model.helper.ActionCode;
import aero.minova.rcp.model.util.ErrorObject;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.rcp.accessor.GridAccessor;
import aero.minova.rcp.rcp.accessor.LookupValueAccessor;
import aero.minova.rcp.rcp.accessor.TextValueAccessor;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.widgets.SectionGrid;

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
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	@Inject
	IEclipseContext partContext;

	@Inject
	EModelService model;

	@Inject
	EPartService partService;

	@Inject
	IEventBroker broker;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	@Inject
	@Preference(nodePath = "aero.minova.rcp.preferencewindow", value = "user")
	String employee;

	@Inject
	@Preference(nodePath = "aero.minova.rcp.preferencewindow", value = "timezone")
	String timezone;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.AUTO_RELOAD_INDEX)
	boolean autoReloadIndex;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.SHOW_DISCARD_CHANGES_DIALOG_INDEX)
	boolean showDiscardDialogIndex;

	private MDetail mDetail;

	private MPerspective perspective;

	private List<ArrayList> keys = null;

	private Table selectedTable;
	private HashMap<String, Table> selectedGrids;

	@Inject
	private Form form;

	IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);

	private WFCDetailPart wfcDetailPart;

	public void initializeCasRequestUtil(MDetail detail, MPerspective perspective, WFCDetailPart wfcDetailPart) {
		this.mDetail = detail;
		this.perspective = perspective;
		this.wfcDetailPart = wfcDetailPart;
		this.selectedGrids = new HashMap<>();

		// Timeouts aus Einstellungen lesen, in DataService setzten und Listener hinzufügen
		dataService.setTimeout(preferences.getInt(ApplicationPreferences.TIMEOUT_CAS, 15));
		dataService.setTimeoutOpenNotification(preferences.getInt(ApplicationPreferences.TIMEOUT_OPEN_NOTIFICATION, 1));
		preferences.addPreferenceChangeListener(event -> {
			if (event.getKey().equals(ApplicationPreferences.TIMEOUT_CAS)) {
				dataService.setTimeout(Integer.parseInt((String) event.getNewValue()));
			} else if (event.getKey().equals(ApplicationPreferences.TIMEOUT_OPEN_NOTIFICATION)) {
				dataService.setTimeoutOpenNotification(Integer.parseInt((String) event.getNewValue()));
			}
		});
	}

	/**
	 * Bei Auswahl eines Indexes wird anhand der in der Row vorhandenen Daten eine Anfrage an den CAS versendet, um sämltiche Informationen zu erhalten
	 *
	 * @param rows
	 */
	@Inject
	public void changeSelectedEntry(@Optional @Named(Constants.BROKER_ACTIVEROWS) List<Row> rows) {
		if (rows == null || rows.isEmpty()) {
			return;
		}

		Display.getDefault().asyncExec(() -> {
			if (showDiscardDialogIndex && !discardChanges()) {
				broker.send(Constants.BROKER_CLEARSELECTION, perspective);
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

				if (!newKeys.equals(keys)) {
					setKeys(newKeys);
				}

				Row r = builder.create();
				rowIndexTable.addRow(r);

				CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(rowIndexTable.getName(), rowIndexTable);
				tableFuture.thenAccept(t -> sync.asyncExec(() -> {
					selectedTable = t.getOutputParameters();
					updateSelectedEntry();
				}));

				for (MGrid g : mDetail.getGrids()) {
					Table gridRequestTable = TableBuilder.newTable(g.getProcedurePrefix() + "Read" + g.getProcedureSuffix()).create();
					RowBuilder gridRowBuilder = RowBuilder.newRow();
					Grid grid = g.getGrid();
					for (Field f : grid.getField()) {
						if (KeyType.PRIMARY.toString().equalsIgnoreCase(f.getKeyType())) {
							aero.minova.rcp.model.Column column = dataFormService.createColumnFromField(f, "");
							gridRequestTable.addColumn(column);

							// Entsprechenden Wert im Index finden
							boolean found = false;
							for (int i = 0; i < form.getIndexView().getColumn().size(); i++) {
								if (indexColumns.get(i).getName().equals(f.getName())
										|| (f.getSqlIndex().intValue() == 0 && indexColumns.get(i).getName().equals("KeyLong"))) {
									found = true;
									gridRowBuilder.withValue(row.getValue(i).getValue());
								}
							}
							if (!found) {
								gridRowBuilder.withValue(null);
							}
						}
					}
					Row gridRow = gridRowBuilder.create();
					gridRequestTable.addRow(gridRow);

					CompletableFuture<SqlProcedureResult> gridFuture = dataService.getGridDataAsync(gridRequestTable.getName(), gridRequestTable);
					gridFuture.thenAccept(t -> sync.asyncExec(() -> {
						if (t != null) {
							Table result = t.getResultSet();
							if (result.getName().equals(g.getDataTable().getName())) {
								selectedGrids.put(g.getProcedureSuffix(), result.copy());
								updateSelectedGrids();
							}
						}
					}));
				}
			}
		});
	}

	/**
	 * Verarbeitung der empfangenen Tabelle des CAS mit Bindung der Detailfelder mit den daraus erhaltenen Daten, dies erfolgt durch die Consume-Methode
	 */
	public void updateSelectedEntry() {
		if (selectedTable != null) {
			for (int i = 0; i < selectedTable.getColumnCount(); i++) {
				String name = selectedTable.getColumnName(i);
				MField c = mDetail.getField(name);
				if (c != null && c.getConsumer() != null) {
					try {
						c.indicateWaiting();
						c.setValue(selectedTable.getRows().get(0).getValue(i), false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		updateSelectedGrids();
		// Revert Button updaten
		broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, "aero.minova.rcp.rcp.handledtoolitem.revert");
	}

	public void updateSelectedGrids() {
		for (Entry<String, Table> gridEntry : selectedGrids.entrySet()) {
			MGrid mGrid = mDetail.getGrid(gridEntry.getKey());
			GridAccessor gVA = (GridAccessor) mGrid.getGridAccessor();
			SectionGrid sectionGrid = gVA.getSectionGrid();
			sectionGrid.setDataTable(gridEntry.getValue().copy());
			sectionGrid.clearDataChanges();
			sectionGrid.enableInsert(true);
		}
	}

	/**
	 * Erstellen einer Update-Anfrage oder einer Insert-Anfrage an den CAS,abhängig der gegebenen Keys
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
				MField field = mDetail.getField(formTable.getColumnName(valuePosition));
				if (field != null) {
					rb.withValue(field.getValue() != null ? field.getValue().getValue() : null);
				}
				valuePosition++;
			}

			// anhand der Maske wird der Defaultwert und der DataType des Fehlenden Row-Wertes ermittelt und der Row angefügt
			Row r = rb.create();
			formTable.addRow(r);
			sendSaveRequest(formTable);

			// Zeilen in Grids löschen, speichern und updaten
			for (MGrid g : mDetail.getGrids()) {
				SectionGrid sg = ((GridAccessor) g.getGridAccessor()).getSectionGrid();
				sg.closeEditor();

				Table gridDeleteTable = TableBuilder.newTable(g.getProcedurePrefix() + Constants.DELETE_REQUEST + g.getProcedureSuffix()).create();
				Table gridInsertTable = TableBuilder.newTable(g.getProcedurePrefix() + Constants.INSERT_REQUEST + g.getProcedureSuffix()).create();
				Table gridUpdateTable = TableBuilder.newTable(g.getProcedurePrefix() + Constants.UPDATE_REQUEST + g.getProcedureSuffix()).create();

				for (aero.minova.rcp.model.Column gridColumn : g.getDataTable().getColumns()) {
					aero.minova.rcp.model.Column c = new aero.minova.rcp.model.Column(gridColumn.getName(), gridColumn.getType());
					gridDeleteTable.addColumn(c);
					gridInsertTable.addColumn(c);
					gridUpdateTable.addColumn(c);
				}

				for (Row row : sg.getRowsToDelete()) {
					gridDeleteTable.addRow(row);
				}
				for (Row row : sg.getRowsToInsert()) {
					gridInsertTable.addRow(row);
				}
				for (Row row : sg.getRowsToUpdate()) {
					gridUpdateTable.addRow(row);
				}

				if (!gridDeleteTable.getRows().isEmpty()) {
					CompletableFuture<SqlProcedureResult> gridFuture = dataService.getGridDataAsync(gridDeleteTable.getName(), gridDeleteTable);
					gridFuture.thenAccept(t -> sync.asyncExec(() -> {
						// TODO: entsprechend reagieren
					}));
				}

				if (!gridInsertTable.getRows().isEmpty()) {
					CompletableFuture<SqlProcedureResult> gridFuture = dataService.getGridDataAsync(gridInsertTable.getName(), gridInsertTable);
					gridFuture.thenAccept(t -> sync.asyncExec(() -> {
						// TODO: entsprechend reagieren
					}));
				}

				if (!gridUpdateTable.getRows().isEmpty()) {
					CompletableFuture<SqlProcedureResult> gridFuture = dataService.getGridDataAsync(gridUpdateTable.getName(), gridUpdateTable);
					gridFuture.thenAccept(t -> sync.asyncExec(() -> {
						// TODO: entsprechend reagieren
					}));
				}
			}
		}
	}

	private void sendSaveRequest(Table t) {
		if (t.getRows() != null) {
			CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(t.getName(), t);

			tableFuture.thenAccept(tr -> sync.asyncExec(() -> {
				// Speichern wieder aktivieren
				broker.post(Constants.BROKER_SAVECOMPLETE, true);
				if (Objects.isNull(getKeys())) {
					checkNewEntryInsert(tr);
				} else {
					checkEntryUpdate(tr);
				}
			}));

			// Auch bei Fehler Speichern wieder aktivieren
			tableFuture.exceptionally(ex -> {
				broker.post(Constants.BROKER_SAVECOMPLETE, true);
				return null;
			});
		} else {
			openNotificationPopup(getTranslation("msg.ActionAborted"));
		}
	}

	/**
	 * Überprüft. ob das Update erfolgreich war
	 *
	 * @param response
	 */
	private void checkEntryUpdate(SqlProcedureResult response) {
		if (response == null) {
			return;
		}
		// Wenn es Hier negativ ist dann haben wir einen Fehler
		if (response.getReturnCode() == -1) {
			ErrorObject e = new ErrorObject(response.getResultSet(), dataService.getUserName());
			showErrorMessage(e);
		} else {
			openNotificationPopup(getTranslation("msg.DataUpdated"));
			handleUserAction(Constants.UPDATE_REQUEST);

			if (autoReloadIndex) {
				ParameterizedCommand cmd = commandService.createCommand("aero.minova.rcp.rcp.command.loadindex", null);
				handlerService.executeHandler(cmd);
			}
		}
	}

	/**
	 * Ruft den Helper auf, löscht die Felder im Detail nach der Eingabe
	 *
	 * @param updateRequest
	 */
	private void handleUserAction(String updateRequest) {
		Map<MPerspective, String> map = new HashMap<>();
		map.put(perspective, updateRequest);
		clearFields(map);
		if (mDetail.getHelper() != null) {
			mDetail.getHelper().handleDetailAction(ActionCode.SAVE);
		}
		focusFirstEmptyField();
	}

	/**
	 * Überprüft, ob der neue Eintrag erstellt wurde
	 *
	 * @param response
	 */
	private void checkNewEntryInsert(SqlProcedureResult response) {
		if (response == null) {
			return;
		}
		if (response.getReturnCode() == -1) {
			ErrorObject e = new ErrorObject(response.getResultSet(), dataService.getUserName());
			showErrorMessage(e);
		} else {
			openNotificationPopup(getTranslation("msg.DataSaved"));
			handleUserAction(Constants.INSERT_REQUEST);

			if (autoReloadIndex) {
				ParameterizedCommand cmd = commandService.createCommand("aero.minova.rcp.rcp.command.loadindex", null);
				handlerService.executeHandler(cmd);
			}
		}
	}

	/**
	 * Liefert das übersetzte Objekt zurück
	 *
	 * @param translate
	 * @return
	 */
	private String getTranslation(String translate) {
		String messageproperty = "@" + translate;
		return translationService.translate(messageproperty, null);
	}

	@Inject
	@Optional
	public void showErrorMessage(@UIEventTopic(Constants.BROKER_SHOWERRORMESSAGE) String message) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(perspective)) {
			// Fokus auf den Search Part legen, damit Fehlermeldungen nicht mehrmals angezeigt werden
			List<MPart> findElements = model.findElements(activePerspective, PartsID.SEARCH_PART, MPart.class);
			partService.activate(findElements.get(0));

			MessageDialog.openError(shell, "Error", getTranslation(message));
		}
	}

	@Inject
	@Optional
	public void showErrorMessage(@UIEventTopic(Constants.BROKER_SHOWERROR) ErrorObject et) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(perspective)) {
			Table errorTable = et.getErrorTable();
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
			value += "\n\nUser : " + et.getUser();
			value += "\nProcedure/View: " + et.getProcedureOrView();

			// Fokus auf den Search Part legen, damit Fehlermeldungen von Lookups nicht mehrmals angezeigt werden
			List<MPart> findElements = model.findElements(activePerspective, PartsID.SEARCH_PART, MPart.class);
			partService.activate(findElements.get(0));

			if (et.getT() == null) {
				MessageDialog.openError(shell, "Error", value);
			} else {
				ShowErrorDialogHandler.execute(shell, "Error", value, et.getT());
			}
		}
	}

	@Inject
	@Optional
	public void showNotification(@UIEventTopic(Constants.BROKER_SHOWNOTIFICATION) String message) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(perspective)) {
			openNotificationPopup(getTranslation(message));
		}
	}

	/**
	 * Sucht die aktiven Controls aus der XMLDetailPart und baut anhand deren Werte eine Abfrage an den CAS zusammen
	 *
	 * @param obj
	 */
	@Inject
	@Optional
	public void buildDeleteTable(@UIEventTopic(Constants.BROKER_DELETEENTRY) MPerspective perspective) {
		if (perspective == this.perspective && getKeys() != null) {
			String tablename = form.getIndexView() != null ? "sp" : "op";
			if ((!"sp".equals(form.getDetail().getProcedurePrefix()) && !"op".equals(form.getDetail().getProcedurePrefix()))) {
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
					if (ta != null) {
						deleteEntry(ta);
					}
				}));
			}

			// In allen Grids alle Zeilen löschen
			for (MGrid g : mDetail.getGrids()) {
				SectionGrid sg = ((GridAccessor) g.getGridAccessor()).getSectionGrid();
				sg.closeEditor();

				Table gridDeleteTable = TableBuilder.newTable(g.getProcedurePrefix() + Constants.DELETE_REQUEST + g.getProcedureSuffix()).create();
				for (aero.minova.rcp.model.Column gridColumn : g.getDataTable().getColumns()) {
					aero.minova.rcp.model.Column c = new aero.minova.rcp.model.Column(gridColumn.getName(), gridColumn.getType());
					gridDeleteTable.addColumn(c);
				}

				for (Row row : sg.getDataTable().getRows()) {
					gridDeleteTable.addRow(row);
				}

				if (!gridDeleteTable.getRows().isEmpty()) {
					CompletableFuture<SqlProcedureResult> gridFuture = dataService.getGridDataAsync(gridDeleteTable.getName(), gridDeleteTable);
					gridFuture.thenAccept(resTable -> sync.asyncExec(() -> {
						// TODO: entsprechend reagieren
					}));
				}
			}
		}
	}

	/**
	 * Versendet eine Ticketanfrage an den CAS, der Value wird immer ohne vorangegangenes '#' übergeben
	 *
	 * @param ticketvalue
	 */
	@Inject
	@Optional
	public void buildTicketTable(@UIEventTopic(Constants.BROKER_RESOLVETICKET) Value ticketvalue) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(perspective) && ticketvalue.getValue() != null) {
			System.out.println("Nachfrage an den CAS mit Ticket: #" + ticketvalue.getStringValue());
			Table ticketTable = TableBuilder.newTable("Ticket").withColumn(Constants.TABLE_TICKETNUMBER, DataType.INTEGER, OutputType.OUTPUT).create();
			Row r = RowBuilder.newRow().withValue(ticketvalue).create();
			ticketTable.addRow(r);

			ticketFieldsUpdate("...waiting for #" + ticketvalue.getStringValue(), false);
			CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(ticketTable.getName(), ticketTable);

			// Fehler abfangen, Felder wieder freigeben
			tableFuture.exceptionally(ex -> {
				// Im Display Thread ausführen
				Display.getDefault().syncExec(() -> ticketFieldsUpdate("...", true));
				return null;
			});

			// Hier wollen wir, dass der Benutzer warten muss wir bereitsn schon mal die Detailfelder vor
			tableFuture.thenAccept(ta -> sync.syncExec(() -> {
				ticketFieldsUpdate("...", true);
				if (ta != null && ta.getResultSet() != null && "Error".equals(ta.getResultSet().getName())) {
					ErrorObject e = new ErrorObject(ta.getResultSet(), "USER");
					showErrorMessage(e);
				} else if (ta != null) {
					selectedTable = ta.getResultSet();
					if (!selectedTable.getRows().isEmpty()) {
						updateSelectedEntry();
					} else {
						showErrorMessage("msg.TicketNotFound");
					}
				}
				updatePossibleLookupEntries();
			}));
		}
	}

	/**
	 * Für die Felder, die von einem Ticket gefüllt werden können (Service, ServiceObject, OrderReciever, ServiceContract, Description), wird die Message und
	 * Editability gesetzt
	 */
	private void ticketFieldsUpdate(String messageText, boolean editable) {
		MField field = mDetail.getField("Description");
		field.getValueAccessor().setEditable(editable);
		// Text mit Style SWT.MULTI unterstützt .setMessageText() nicht, deshalb workaround
		if (editable) {
			((TextValueAccessor) field.getValueAccessor()).setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
			((TextValueAccessor) field.getValueAccessor()).setText("");
		} else {
			((TextValueAccessor) field.getValueAccessor()).setColor(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
			((TextValueAccessor) field.getValueAccessor()).setText(messageText);
		}

		field = mDetail.getField("OrderReceiverKey");
		field.getValueAccessor().setEditable(editable);
		field.getValueAccessor().setMessageText(messageText);

		field = mDetail.getField("ServiceKey");
		field.getValueAccessor().setEditable(editable);
		field.getValueAccessor().setMessageText(messageText);

		field = mDetail.getField("ServiceContractKey");
		field.getValueAccessor().setEditable(editable);
		field.getValueAccessor().setMessageText(messageText);

		field = mDetail.getField("ServiceObjectKey");
		field.getValueAccessor().setEditable(editable);
		field.getValueAccessor().setMessageText(messageText);
	}

	/**
	 * Wenn ein Lookup keinen Wert enthält, nachdem das Ticket aufgelöst wurde, werden die möglichen Werte aktualisiert
	 */
	private void updatePossibleLookupEntries() {
		MField field = mDetail.getField("OrderReceiverKey");
		if (field.getValue() == null) {
			((LookupValueAccessor) field.getValueAccessor()).updatePossibleValues();
		}

		field = mDetail.getField("ServiceKey");
		if (field.getValue() == null) {
			((LookupValueAccessor) field.getValueAccessor()).updatePossibleValues();
		}

		field = mDetail.getField("ServiceContractKey");
		if (field.getValue() == null) {
			((LookupValueAccessor) field.getValueAccessor()).updatePossibleValues();
		}

		field = mDetail.getField("ServiceObjectKey");
		if (field.getValue() == null) {
			((LookupValueAccessor) field.getValueAccessor()).updatePossibleValues();
		}
	}

	/**
	 * Überprüft, ob die Anfrage erfolgreich war, falls nicht bleiben die Textfelder befüllt um die Anfrage anzupassen
	 *
	 * @param response
	 */
	public void deleteEntry(SqlProcedureResult response) {
		if (response.getReturnCode() == -1) {
			ErrorObject e = new ErrorObject(response.getResultSet(), dataService.getUserName());
			showErrorMessage(e);
		} else {
			if (autoReloadIndex) {
				ParameterizedCommand cmd = commandService.createCommand("aero.minova.rcp.rcp.command.loadindex", null);
				handlerService.executeHandler(cmd);
			}
			openNotificationPopup(getTranslation("msg.DataDeleted"));
			Map<MPerspective, String> map = new HashMap<>();
			map.put(perspective, Constants.DELETE_REQUEST);
			clearFields(map);
			// Helper-Klasse triggern, damit die Standard-Werte gesetzt werden können.
			if (mDetail.getHelper() != null) {
				mDetail.getHelper().handleDetailAction(ActionCode.DEL);
			}
			focusFirstEmptyField();
		}
	}

	/**
	 * Öffet ein Popup, welches dem Nutzer über den Erfolg oder das Scheitern seiner Anfrage informiert
	 *
	 * @param message
	 */
	public void openNotificationPopup(String message) {
		if (!shell.getDisplay().isDisposed()) {
			NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(), message,
					getTranslation("Notification"), shell);
			notificationPopUp.open();
		}
	}

	@Optional
	@Inject
	public void notifyUser(@UIEventTopic(Constants.BROKER_NOTIFYUSER) String message) {
		openNotificationPopup(message);
	}

	/**
	 * Diese Methode reagiert wird ausgeführt, wenn der Anwender einen neuen Datensatz eintragen möchte.
	 *
	 * @param origin
	 */
	@Optional
	@Inject
	public void newFields(@UIEventTopic(Constants.BROKER_NEWENTRY) Map<MPerspective, String> map) {
		if (map.keySet().iterator().next() != perspective) {
			return;
		}

		if (!discardChanges()) {
			return;
		}

		clearFields(map);
		// Helper-Klasse triggern, damit die Standard-Werte gesetzt werden können.
		if (mDetail.getHelper() != null) {
			mDetail.getHelper().handleDetailAction(ActionCode.NEW);
		}
		focusFirstEmptyField();
	}

	/**
	 * Fragt den Nutzer ob ungespeicherte Änderungen verworfen werden sollen. Wenn es keine Änderungen gibt wird true zurückgegeben
	 * 
	 * @return
	 */
	private boolean discardChanges() {
		if (wfcDetailPart.getDirtyFlag()) {
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), translationService.translate("@msg.ChangesDialog", null), null,
					translationService.translate("@msg.New.DirtyMessage", null), MessageDialog.CONFIRM,
					new String[] { translationService.translate("@Action.Discard", null), translationService.translate("@Abort", null) }, 0);
			return dialog.open() == 0;
		}
		return true;
	}

	/**
	 * Diese Methode bereiningt die Felder nach einer Erfolgreichen CAS-Anfrage
	 *
	 * @param origin
	 */
	@Optional
	@Inject
	public void clearFields(@UIEventTopic(Constants.BROKER_CLEARFIELDS) Map<MPerspective, String> map) {
		if (map.keySet().iterator().next() != perspective) {
			return;
		}

		// Felder leeren
		selectedTable = null;
		for (MField f : mDetail.getFields()) {
			setKeys(null);
			f.setValue(null, false);
			if (f instanceof MLookupField) {
				((MLookupField) f).setOptions(null);
			}
		}

		// Grids leeren
		selectedGrids.clear();
		for (MGrid g : mDetail.getGrids()) {
			SectionGrid sg = ((GridAccessor) g.getGridAccessor()).getSectionGrid();
			sg.clearGrid();
			sg.enableInsert(false);
		}

		// Revert Button updaten
		broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, "aero.minova.rcp.rcp.handledtoolitem.revert");
		// Auswahl im Index entfernen
		broker.send(Constants.BROKER_CLEARSELECTION, perspective);
	}

	/**
	 * Setzt die Detail-Felder wieder auf den Usprungszustand des Ausgewählten Eintrags zurück
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

	private void focusFirstEmptyField() {
		for (MSection section : mDetail.getPageList()) {
			for (MField field : section.getTabList()) {
				if (field.getValue() == null) {
					((AbstractValueAccessor) field.getValueAccessor()).getControl().setFocus();
					return;
				}
			}
		}

		// Falls kein leeres Feld gefunden wurde erstes Feld fokusieren
		((AbstractValueAccessor) mDetail.getPageList().get(0).getTabList().get(0).getValueAccessor()).getControl().setFocus();
	}

	public Table getSelectedTable() {
		return selectedTable;
	}

	/**
	 * Prüfung ob eine Wertänderung in Feldern oder Grids stattgefunden hat.
	 *
	 * @return
	 */
	public boolean checkDirty() {
		return checkFields() || checkGrids();
	}

	private boolean checkFields() {
		// Prüfung der mFields ob es einen Value ≠ null gibt
		if (getSelectedTable() == null || getSelectedTable().getRows().isEmpty()) {
			for (MField mfield : mDetail.getFields()) {
				if (mfield.getValue() != null) {
					return true;
				}
			}
			return false;
		}

		// vergleicht Feld-Wert mit Wert aus ausgeleser Tabelle (vom CAS)
		List<MField> checkedFields = new ArrayList<>();
		for (int i = 0; i < selectedTable.getColumnCount(); i++) {
			MField c = mDetail.getField(selectedTable.getColumnName(i));
			checkedFields.add(c);
			Value sV = selectedTable.getRows().get(0).getValue(i);
			if (c == null) {
				continue;
			}
			if (c instanceof MLookupField) {
				if (sV == null && c.getValue() != null || //
						sV != null && c.getValue() == null || //
						c.getValue() != null && !c.getValue().getIntegerValue().equals(sV.getIntegerValue())) {
					return true;
				}
			} else if ((c.getValue() == null && sV != null) || (c.getValue() != null && !c.getValue().equals(sV))) {
				return true;
			}
		}

		// Sind die Felder, die nicht in der ausgelesenen Tabelle sind, leer?
		for (MField mfield : mDetail.getFields()) {
			if (!checkedFields.contains(mfield) && mfield.getValue() != null) {
				return true;
			}
		}

		return false;
	}

	private boolean checkGrids() {
		// Prüfen, ob die MGrids Zeilen haben
		if (selectedGrids.isEmpty()) {
			for (MGrid grid : mDetail.getGrids()) {
				if (!grid.getDataTable().getRows().isEmpty()) {
					return true;
				}
			}
		}

		// Aktuellen Grids mit Werten der CAS-Zurückgabe vergleichen
		for (Entry<String, Table> entry : selectedGrids.entrySet()) {
			MGrid mGrid = mDetail.getGrid(entry.getKey());
			if (!entry.getValue().equals(mGrid.getDataTable())) {
				return true;
			}
		}

		return false;
	}

	public void setSelectedTable(Table table) {
		this.selectedTable = table;
	}

	/*
	 * Die übergebene Procedur wird aufgerufen. Als Parameter werden die Werte des aktuell geladenen Datensatzes übergeben
	 * @param p
	 */
	public void callProcedure(Procedure p) {
		Table t = new Table();
		t.setName(p.getName());
		Row r = new Row();
		for (EventParam ep : p.getParam()) {
			MField f = mDetail.getField(ep.getFieldName());
			t.addColumn(new aero.minova.rcp.model.Column(ep.getFieldName(), f.getDataType(), OutputType.valueOf(ep.getType())));
			r.addValue(f.getValue());
		}
		t.addRow(r);
		dataService.getDetailDataAsync(t.getName(), t);
	}
}
