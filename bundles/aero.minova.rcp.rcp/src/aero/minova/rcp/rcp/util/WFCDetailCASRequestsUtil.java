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

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dialogs.NotificationPopUp;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.DataType;
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
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.helper.ActionCode;
import aero.minova.rcp.model.util.ErrorObject;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.LookupValueAccessor;
import aero.minova.rcp.rcp.accessor.TextValueAccessor;

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

	private MDetail detail;

	private MPerspective perspective = null;

	private Map<String, Integer> lookups = new HashMap<>();

	private List<ArrayList> keys = null;

	private Table selectedTable = null;

	@Inject
	private Form form;

	Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);

	/**
	 * Bei Auswahl eines Indexes wird anhand der in der Row vorhandenen Daten eine Anfrage an den CAS versendet, um sämltiche Informationen zu erhalten
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
		}
	}

	/**
	 * Verarbeitung der empfangenen Tabelle des CAS mit Bindung der Detailfelder mit den daraus erhaltenen Daten, dies erfolgt durch die Consume-Methode
	 */
	public void updateSelectedEntry() {
		if (selectedTable != null) {
			for (int i = 0; i < selectedTable.getColumnCount(); i++) {
				String name = selectedTable.getColumnName(i);
				MField c = detail.getField(name);
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
			NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(), "Entry not possible, check for wrong inputs in your messured Time",
					shell);
			notificationPopUp.open();
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
		if (detail.getHelper() != null) {
			detail.getHelper().handleDetailAction(ActionCode.SAVE);
		}
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
			// Fokus weg von den Lookup setzten (auf search part), damit sich das Fehlerfenster nicht immer wieder öffnet
			List<MPart> findElements = model.findElements(perspective, PartsID.SEARCH_PART, MPart.class);
			partService.activate(findElements.get(0), true);

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

			if (et.getT() == null) {
				MessageDialog.openError(shell, "Error", value);
			} else {
				ShowErrorDialogHandler.execute(shell, "Error", value, et.getT());
			}
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
		if (perspective == this.perspective) {
			if (getKeys() != null) {
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
			}
		}
	}

	/**
	 * Versnedet eine Ticketanfrage an den CAS, der Value wird immer ohne vorangegangenes '#' übergeben
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
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						ticketFieldsUpdate("...", true);
					}
				});
				return null;
			});

			// Hier wollen wir, dass der Benutzer warten muss wir bereitsn schon mal die Detailfelder vor
			tableFuture.thenAccept(ta -> sync.syncExec(() -> {
				ticketFieldsUpdate("...", true);
				if (ta.getResultSet() != null && "Error".equals(ta.getResultSet().getName())) {
					ErrorObject e = new ErrorObject(ta.getResultSet(), "USER");
					showErrorMessage(e);
				} else {
					selectedTable = ta.getResultSet();
					updateSelectedEntry();
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
		MField field = detail.getField("Description");
		field.getValueAccessor().setEditable(editable);
		// Text mit Style SWT.MULTI unterstützt .setMessageText() nicht, deshalb workaround
		if (editable) {
			((TextValueAccessor) field.getValueAccessor()).setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
			((TextValueAccessor) field.getValueAccessor()).setText("");
		} else {
			((TextValueAccessor) field.getValueAccessor()).setColor(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
			((TextValueAccessor) field.getValueAccessor()).setText(messageText);
		}

		field = detail.getField("OrderReceiverKey");
		field.getValueAccessor().setEditable(editable);
		field.getValueAccessor().setMessageText(messageText);

		field = detail.getField("ServiceKey");
		field.getValueAccessor().setEditable(editable);
		field.getValueAccessor().setMessageText(messageText);

		field = detail.getField("ServiceContractKey");
		field.getValueAccessor().setEditable(editable);
		field.getValueAccessor().setMessageText(messageText);

		field = detail.getField("ServiceObjectKey");
		field.getValueAccessor().setEditable(editable);
		field.getValueAccessor().setMessageText(messageText);
	}

	/**
	 * Wenn ein Lookup keinen Wert enthält, nachdem das Ticket aufgelöst wurde, werden die möglichen Werte aktualisiert
	 */
	private void updatePossibleLookupEntries() {
		MField field = detail.getField("OrderReceiverKey");
		if (field.getValue() == null) {
			((LookupValueAccessor) field.getValueAccessor()).updatePossibleValues();
		}

		field = detail.getField("ServiceKey");
		if (field.getValue() == null) {
			((LookupValueAccessor) field.getValueAccessor()).updatePossibleValues();
		}

		field = detail.getField("ServiceContractKey");
		if (field.getValue() == null) {
			((LookupValueAccessor) field.getValueAccessor()).updatePossibleValues();
		}

		field = detail.getField("ServiceObjectKey");
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
			if (detail.getHelper() != null) {
				detail.getHelper().handleDetailAction(ActionCode.DEL);
			}
		}
	}

	/**
	 * Öffet ein Popup, welches dem Nutzer über den Erfolg oder das Scheitern seiner Anfrage informiert
	 *
	 * @param message
	 */
	public void openNotificationPopup(String message) {
		NotificationPopUp notificationPopUp = new NotificationPopUp(shell.getDisplay(), message, shell);
		notificationPopUp.open();
	}

	@Optional
	@Inject
	public void newFields(@UIEventTopic(Constants.BROKER_NOTIFYUSER) String message) {
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
}
