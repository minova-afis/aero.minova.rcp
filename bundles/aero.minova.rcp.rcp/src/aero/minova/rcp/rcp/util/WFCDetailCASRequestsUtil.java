package aero.minova.rcp.rcp.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.EventParam;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.form.model.xsd.Procedure;
import aero.minova.rcp.model.KeyType;
import aero.minova.rcp.model.OutputType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MParamStringField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.model.helper.ActionCode;
import aero.minova.rcp.model.util.ErrorObject;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.rcp.accessor.GridAccessor;
import aero.minova.rcp.rcp.accessor.SectionAccessor;
import aero.minova.rcp.rcp.handlers.ShowErrorDialogHandler;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.widgets.SectionGrid;
import aero.minova.rcp.widgets.MinovaNotifier;

public class WFCDetailCASRequestsUtil {

	private static final String ERROR = "Error";

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

	private Map<String, Value> keys = null;

	private Table selectedTable;
	private HashMap<String, Table> selectedOptionPages;
	private HashMap<String, Table> selectedGrids;

	@Inject
	private Form form;

	IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);

	private WFCDetailPart wfcDetailPart;

	private Table currentKeyTable;

	public void initializeCasRequestUtil(MDetail detail, MPerspective perspective, WFCDetailPart wfcDetailPart) {
		this.mDetail = detail;
		this.perspective = perspective;
		this.wfcDetailPart = wfcDetailPart;
		this.selectedOptionPages = new HashMap<>();
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
	public void readData(@Optional @Named(Constants.BROKER_ACTIVEROWS) Table table) {
		if (table == null || table.getRows().isEmpty()) {
			return;
		}

		currentKeyTable = table;
		Display.getDefault().asyncExec(() -> {
			if (showDiscardDialogIndex && !discardChanges()) {
				broker.send(Constants.BROKER_CLEARSELECTION, perspective);
				return;
			}

			sendEventToHelper(ActionCode.BEFOREREAD);

			// Hauptfelder
			Table rowIndexTable = createReadTableFromForm(form, table);
			CompletableFuture<SqlProcedureResult> tableFuture = dataService.callProcedureAsync(rowIndexTable);
			tableFuture.thenAccept(t -> sync.asyncExec(() -> {
				if (t != null) {
					selectedTable = t.getOutputParameters();
					updateSelectedEntry();
					// Grids auslesen, wenn Daten der Hauptmaske geladen sind
					updateGridLookupValues();
					for (MGrid g : mDetail.getGrids()) {
						readGrid(g, table);
					}

					sendEventToHelper(ActionCode.AFTERREAD);
				}
			}));

			// Option Pages
			selectedOptionPages.clear();
			for (Form opForm : mDetail.getOptionPages()) {
				Table opFormTable = createReadTableFromForm(opForm, table);
				CompletableFuture<SqlProcedureResult> opFuture = dataService.callProcedureAsync(opFormTable);
				opFuture.thenAccept(t -> sync.asyncExec(() -> {
					if (t != null) {
						selectedOptionPages.put(opForm.getDetail().getProcedureSuffix(), t.getOutputParameters());
						updateSelectedEntry();
					}
				}));
			}
		});
	}

	public void readGrid(MGrid g, Table keyTable) {
		Table gridRequestTable = TableBuilder.newTable(g.getProcedurePrefix() + "Read" + g.getProcedureSuffix()).create();
		RowBuilder gridRowBuilder = RowBuilder.newRow();
		Grid grid = g.getGrid();
		SectionGrid sg = ((GridAccessor) g.getGridAccessor()).getSectionGrid();
		boolean firstPrimary = true;
		for (Field f : grid.getField()) {
			if (KeyType.PRIMARY.toString().equalsIgnoreCase(f.getKeyType())) {
				aero.minova.rcp.model.Column column = dataFormService.createColumnFromField(f, "");
				gridRequestTable.addColumn(column);

				boolean found = false;
				if (!sg.getFieldnameToValue().isEmpty()) { // Zuordnung aus .xbs nutzen, Keys aus Detail nehmen
					if (sg.getFieldnameToValue().containsKey(f.getName())) {
						found = true;
						String fieldNameInMain = sg.getFieldnameToValue().get(f.getName());
						Value v = mDetail.getField(fieldNameInMain).getValue();
						gridRowBuilder.withValue(v);
					}

				} else { // Default Verhalten, entsprechenden Wert im Index finden

					int index = keyTable.getColumnIndex(f.getName());
					if (firstPrimary) {
						index = keyTable.getColumnIndex("KeyLong");
					}

					if (index >= 0) {
						found = true;
						Row row = keyTable.getRows().get(0);
						gridRowBuilder.withValue(row.getValue(index).getValue());
					}
				}
				if (!found) {
					gridRowBuilder.withValue(null);
				}
				firstPrimary = false;
			}
		}

		Row gridRow = gridRowBuilder.create();
		gridRequestTable.addRow(gridRow);

		CompletableFuture<SqlProcedureResult> gridFuture = dataService.callProcedureAsync(gridRequestTable);
		gridFuture.thenAccept(t -> sync.asyncExec(() -> {
			if (t != null && t.getResultSet() != null) {
				Table result = t.getResultSet();
				if (result.getName().equals(g.getDataTable().getName())) {
					setGridContent(g, result);
				}
			}
		}));
	}

	public void setGridContent(MGrid g, Table data) {
		selectedGrids.put(g.getId(), data.copy());
		updateSelectedGrid(g.getId());
	}

	/**
	 * Die Primary-Keys werden aus der übergebenen Tabelle gelesen. Nur die erste Zeile wird betrachtet
	 *
	 * @param tableForm
	 * @param keyTable
	 * @return
	 */
	private Table createReadTableFromForm(Form tableForm, Table keyTable) {
		Map<String, String> keysToValue = mDetail.getOptionPageKeys(tableForm.getDetail().getProcedureSuffix());
		boolean useColumnName = tableForm.equals(form) || keysToValue == null; // Hauptmaske oder keine key-zu-value Map in xbs gegeben

		RowBuilder builder = RowBuilder.newRow();

		Map<String, Value> newKeys = new HashMap<>();

		Row row = keyTable.getRows().get(0);

		for (Field f : dataFormService.getFieldsFromForm(tableForm)) {

			int indexInRow = -1;

			// Spalte für Feld finden
			if (useColumnName) {
				indexInRow = keyTable.getColumnIndex(f.getName());
			} else if (keysToValue.containsKey(f.getName())) {
				MField correspondingField = mDetail.getField(keysToValue.get(f.getName()));
				indexInRow = keyTable.getColumnIndex(correspondingField.getName());
			}

			// Wert in Zeile setzten
			if (indexInRow >= 0 && "primary".equals(f.getKeyType()) && row.getValue(indexInRow) != null) {
				builder.withValue(row.getValue(indexInRow).getValue());
				newKeys.put(f.getName(), row.getValue(indexInRow));
			} else {
				builder.withValue(null);
			}

		}

		// Keys nur für die Hauptmaske setzen
		if (!newKeys.equals(getKeys()) && tableForm.equals(form)) {
			setKeys(newKeys);
		}

		Table rowIndexTable = dataFormService.getTableFromFormDetail(tableForm, Constants.READ_REQUEST);
		Row r = builder.create();
		rowIndexTable.addRow(r);
		return rowIndexTable;
	}

	/**
	 * Verarbeitung der empfangenen Tabelle des CAS mit Bindung der Detailfelder mit den daraus erhaltenen Daten, dies erfolgt durch die Consume-Methode
	 */
	public void updateSelectedEntry() {
		// Hauptmaske
		if (selectedTable != null) {
			setFieldsFromTable(null, selectedTable);
		}

		// Option Pages
		for (Entry<String, Table> e : selectedOptionPages.entrySet()) {
			setFieldsFromTable(e.getKey(), e.getValue());
		}

		// Revert Button updaten
		broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, "aero.minova.rcp.rcp.handledtoolitem.revert");
	}

	/*
	 * Updatet die Felder mit der übergebenen Tabelle
	 */
	private void setFieldsFromTable(String opName, Table table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			String name = (opName == null ? "" : opName + ".") + table.getColumnName(i);
			MField c = mDetail.getField(name);
			if (c != null && c.getConsumer() != null) {
				try {
					c.indicateWaiting();
					c.setValue(table.getRows().get(0).getValue(i), false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateSelectedGrids() {
		for (String gridID : selectedGrids.keySet()) {
			updateSelectedGrid(gridID);
		}
	}

	public void updateSelectedGrid(String gridID) {
		MGrid mGrid = mDetail.getGrid(gridID);
		GridAccessor gVA = (GridAccessor) mGrid.getGridAccessor();
		SectionGrid sectionGrid = gVA.getSectionGrid();
		Table t = sectionGrid.setDataTable(selectedGrids.get(gridID).copy());
		sectionGrid.clearDataChanges();
		selectedGrids.put(gridID, t);
		broker.send(Constants.BROKER_CHECKDIRTY, "");
	}

	/**
	 * Erstellen einer Update-Anfrage oder einer Insert-Anfrage an den CAS, abhängig der gegebenen Keys
	 *
	 * @param obj
	 */
	@Inject
	@Optional
	public void buildSaveTable(@UIEventTopic(Constants.BROKER_SAVEENTRY) MPerspective perspective) {
		if (perspective == this.perspective) {
			sendEventToHelper(ActionCode.BEFORESAVE);

			updateGridLookupValues();

			// Zuerst nur die Hauptmaske speichern/updaten. Nur wenn dies erfolgreich war OPs und Grids speichern
			Table formTable = createInsertUpdateTableFromForm(form);
			sendSaveRequest(formTable);
		}
	}

	private void updateOPsAndGrids() {
		// Option Pages
		for (Form opForm : mDetail.getOptionPages()) {
			Table opFormTable = createInsertUpdateTableFromForm(opForm);
			dataService.callProcedureAsync(opFormTable);
		}

		// Grids
		updateGrids();
	}

	/**
	 * Erstellt eine Update oder Insert Tabelle aus der übergebenen Form, je nachdem ob Keys zur verfügung stehen.
	 *
	 * @param buildForm
	 * @return
	 */
	private Table createInsertUpdateTableFromForm(Form buildForm) {

		Map<String, String> keysToIndex = mDetail.getOptionPageKeys(buildForm.getDetail().getProcedureSuffix());
		Table formTable = getInsertUpdateTable(buildForm);
		RowBuilder rb = RowBuilder.newRow();

		int valuePosition = 0;

		for (Field f : dataFormService.getAllPrimaryFieldsFromForm(buildForm)) {

			if (getKeys() == null) {
				rb.withValue(null);
			} else if (keysToIndex != null && keysToIndex.containsKey(f.getName())) {
				// Für OPs Keywert aus Hauptmaske nutzen
				MField correspondingField = mDetail.getField(keysToIndex.get(f.getName()));
				rb.withValue(getKeys().get(correspondingField.getName()));
			} else if (getKeys().containsKey(f.getName())) {
				rb.withValue(getKeys().get(f.getName()));
			} else {
				continue;
			}
			valuePosition++;
		}

		while (valuePosition < formTable.getColumnCount()) {
			String fieldname = (buildForm == form ? "" : buildForm.getDetail().getProcedureSuffix() + ".") + formTable.getColumnName(valuePosition);
			MField field = mDetail.getField(fieldname);
			rb.withValue(field.getValue() != null ? field.getValue().getValue() : null);
			valuePosition++;
		}

		// anhand der Maske wird der Defaultwert und der DataType des Fehlenden Row-Wertes ermittelt und der Row angefügt
		Row r = rb.create();
		formTable.addRow(r);
		return formTable;
	}

	private Table getInsertUpdateTable(Form buildForm) {
		Table formTable;
		if (getKeys() != null) {
			formTable = dataFormService.getTableFromFormDetail(buildForm, Constants.UPDATE_REQUEST);
		} else {
			formTable = dataFormService.getTableFromFormDetail(buildForm, Constants.INSERT_REQUEST);
			// Bei Insert wird OUTPUT gesetzt, damit die Keys des neu erstellten Eintrags zurückgegeben werden
			for (aero.minova.rcp.model.Column c : formTable.getColumns()) {
				String fieldname = (buildForm == form ? "" : buildForm.getDetail().getProcedureSuffix() + ".") + c.getName();
				if (mDetail.getField(fieldname).isPrimary()) {
					c.setOutputType(OutputType.OUTPUT);
				}
			}
		}
		return formTable;
	}

	/*
	 * Führt Update, Insert und Delete Anfragen auf Zeilen in Grids aus
	 */
	private void updateGrids() {
		for (MGrid g : mDetail.getGrids()) {
			SectionGrid sg = ((GridAccessor) g.getGridAccessor()).getSectionGrid();
			sg.closeEditor();
			sg.setPrimaryKeys(getKeys());

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
				dataService.callProcedureAsync(gridDeleteTable);
			}

			if (!gridInsertTable.getRows().isEmpty()) {
				dataService.callProcedureAsync(gridInsertTable);
			}

			if (!gridUpdateTable.getRows().isEmpty()) {
				dataService.callProcedureAsync(gridUpdateTable);
			}
		}
	}

	private void sendSaveRequest(Table t) {
		if (t.getRows() != null) {
			CompletableFuture<SqlProcedureResult> tableFuture = dataService.callProcedureAsync(t);

			tableFuture.thenAccept(tr -> sync.asyncExec(() -> {
				// Speichern wieder aktivieren
				broker.post(Constants.BROKER_SAVECOMPLETE, true);
				if (getKeys() == null) {
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
			openNotificationPopup("msg.ActionAborted");
		}
	}

	/**
	 * Überprüft, ob der neue Eintrag erstellt wurde. Wenn ja können OPs und Grids mit den zurückgegebenen Keys gespeichert werden
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
			setKeysFromTable(response.getOutputParameters());
			updateOPsAndGrids();
			openNotificationPopup("msg.DataSaved");
			handleUserAction(Constants.INSERT_REQUEST, response.getOutputParameters());

			if (autoReloadIndex) {
				ParameterizedCommand cmd = commandService.createCommand(Constants.AERO_MINOVA_RCP_RCP_COMMAND_LOADINDEX, null);
				handlerService.executeHandler(cmd);
			}
		}
	}

	/**
	 * Setzt die Primary Keys anhand der übergebenen Tabelle
	 *
	 * @param t
	 */
	private void setKeysFromTable(Table t) {
		Map<String, Value> newKeys = new HashMap<>();
		for (Field f : dataFormService.getAllPrimaryFieldsFromForm(form)) {
			int index = t.getColumnIndex(f.getName());
			newKeys.put(f.getName(), t.getRows().get(0).getValue(index));
		}
		setKeys(newKeys);
	}

	/**
	 * Überprüft, ob das Update erfolgreich war. Wenn ja können OPs und Grids geupdated werden
	 *
	 * @param response
	 */
	private void checkEntryUpdate(SqlProcedureResult response) {
		if (response == null) {
			return;
		}
		if (response.getReturnCode() == -1) {
			ErrorObject e = new ErrorObject(response.getResultSet(), dataService.getUserName());
			showErrorMessage(e);
		} else {
			updateOPsAndGrids();
			openNotificationPopup("msg.DataUpdated");
			handleUserAction(Constants.UPDATE_REQUEST, currentKeyTable);

			if (autoReloadIndex) {
				ParameterizedCommand cmd = commandService.createCommand(Constants.AERO_MINOVA_RCP_RCP_COMMAND_LOADINDEX, null);
				handlerService.executeHandler(cmd);
			}
		}
	}

	/**
	 * Ruft den Helper auf, löscht die Felder im Detail oder lädt Datensatz neu nach der Eingabe
	 *
	 * @param updateRequest
	 */
	private void handleUserAction(String updateRequest, Table keyTable) {
		Map<MPerspective, String> map = new HashMap<>();
		map.put(perspective, updateRequest);

		if (mDetail.isClearAfterSave()) {
			clearFields(map);
		} else {
			reloadFields(keyTable);
		}

		sendEventToHelper(ActionCode.AFTERSAVE);
		focusFirstEmptyField();
	}

	/**
	 * Liefert das übersetzte Objekt zurück
	 *
	 * @param translate
	 * @return
	 */
	private String getTranslation(String translate) {
		if (!translate.startsWith("@")) {
			translate = "@" + translate;
		}
		return translationService.translate(translate, null);
	}

	@Inject
	@Optional
	public void showErrorMessage(@UIEventTopic(Constants.BROKER_SHOWERRORMESSAGE) String message) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(perspective)) {
			// Fokus auf den Search Part legen, damit Fehlermeldungen nicht mehrmals angezeigt werden
			List<MPart> findElements = model.findElements(activePerspective, Constants.SEARCH_PART, MPart.class);
			partService.activate(findElements.get(0));

			MessageDialog.openError(shell, ERROR, getTranslation(message));
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
			List<MPart> findElements = model.findElements(activePerspective, Constants.SEARCH_PART, MPart.class);
			partService.activate(findElements.get(0));

			if (et.getT() == null) {
				MessageDialog.openError(shell, ERROR, value);
			} else {
				ShowErrorDialogHandler.execute(shell, ERROR, value, et.getT());
			}
		}
	}

	@Inject
	@Optional
	public void showNotification(@UIEventTopic(Constants.BROKER_SHOWNOTIFICATION) String message) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(perspective)) {
			openNotificationPopup(message);
		}
	}

	@Inject
	@Optional
	public void reloadIndex(@UIEventTopic(Constants.BROKER_RELOADINDEX) String message) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(perspective)) {
			ParameterizedCommand cmd = commandService.createCommand(Constants.AERO_MINOVA_RCP_RCP_COMMAND_LOADINDEX, null);
			handlerService.executeHandler(cmd);
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

			sendEventToHelper(ActionCode.BEFOREDEL);
			updateGridLookupValues();

			// Hauptmaske
			Table t = createDeleteTableFromForm(form);
			if (t.getRows() != null) {
				CompletableFuture<SqlProcedureResult> tableFuture = dataService.callProcedureAsync(t);
				tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
					if (ta != null) {
						deleteEntry(ta);
					}
				}));
			}
		}
	}

	/**
	 * Sucht die aktiven Controls aus der XMLDetailPart und baut anhand deren Werte eine Abfrage an den CAS zusammen
	 */
	private Table createDeleteTableFromForm(Form deleteForm) {
		String tablename = deleteForm.getIndexView() != null ? "sp" : "op";
		if ((!"sp".equals(deleteForm.getDetail().getProcedurePrefix()) && !"op".equals(deleteForm.getDetail().getProcedurePrefix()))) {
			tablename = deleteForm.getDetail().getProcedurePrefix();
		}
		tablename += "Delete";
		tablename += deleteForm.getDetail().getProcedureSuffix();
		TableBuilder tb = TableBuilder.newTable(tablename);
		RowBuilder rb = RowBuilder.newRow();
		for (Field f : dataFormService.getAllPrimaryFieldsFromForm(deleteForm)) {
			String fieldName = (deleteForm == form ? "" : deleteForm.getDetail().getProcedureSuffix() + ".") + f.getName();
			tb.withColumn(f.getName(), mDetail.getField(fieldName).getDataType());
			rb.withValue(mDetail.getField(fieldName).getValue());
		}
		Table t = tb.create();
		Row r = rb.create();
		t.addRow(r);
		return t;
	}

	/**
	 * Überprüft, ob die Anfrage erfolgreich war, falls nicht bleiben die Textfelder befüllt um die Anfrage anzupassen. Bei erfolgreicher Anfrage werden auch
	 * OPs und Grids gelöscht
	 *
	 * @param response
	 */
	public void deleteEntry(SqlProcedureResult response) {
		if (response.getReturnCode() == -1) {
			ErrorObject e = new ErrorObject(response.getResultSet(), dataService.getUserName());
			showErrorMessage(e);
		} else {
			if (autoReloadIndex) {
				ParameterizedCommand cmd = commandService.createCommand(Constants.AERO_MINOVA_RCP_RCP_COMMAND_LOADINDEX, null);
				handlerService.executeHandler(cmd);
			}
			deleteOPsAndGrids();
			openNotificationPopup("msg.DataDeleted");
			Map<MPerspective, String> map = new HashMap<>();
			map.put(perspective, Constants.DELETE_REQUEST);
			clearFields(map);
			// Helper-Klasse triggern, damit die Standard-Werte gesetzt werden können.
			sendEventToHelper(ActionCode.AFTERDEL);
			focusFirstEmptyField();
		}

	}

	/**
	 * OPs und Grids sollen erst gelöscht werden, wenn der Hauptlöschaufruf erfolgreich war
	 */
	private void deleteOPsAndGrids() {

		// Option Pages
		for (Form opForm : mDetail.getOptionPages()) {
			Table opFormTable = createDeleteTableFromForm(opForm);
			dataService.callProcedureAsync(opFormTable);
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
				dataService.callProcedureAsync(gridDeleteTable);
			}
		}
	}

	/**
	 * Ruft eine Prozedur mit der übergebenen Tabelle auf. Über den Broker kann auf die Ergebnisse gehört werden
	 *
	 * @param table
	 */
	@Inject
	@Optional
	public void callProcedureWithTable(@UIEventTopic(Constants.BROKER_PROCEDUREWITHTABLE) Table table) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(perspective) && table != null) {

			CompletableFuture<SqlProcedureResult> tableFuture = dataService.callProcedureAsync(table);

			// Fehler abfangen
			tableFuture.exceptionally(ex -> {
				broker.send(Constants.BROKER_PROCEDUREWITHTABLEERROR, ex);
				return null;
			});

			// Hier wollen wir, dass der Benutzer warten muss wir bereitsn schon mal die Detailfelder vor
			tableFuture.thenAccept(ta -> sync.syncExec(() -> {
				broker.send(Constants.BROKER_PROCEDUREWITHTABLESUCCESS, ta);
				if (ta != null && ta.getResultSet() != null && ERROR.equals(ta.getResultSet().getName())) {
					ErrorObject e = new ErrorObject(ta.getResultSet(), "USER");
					showErrorMessage(e);
				} else if (ta != null) {
					selectedTable = ta.getResultSet();
					if (!selectedTable.getRows().isEmpty()) {
						updateSelectedEntry();
					} else {
						broker.send(Constants.BROKER_PROCEDUREWITHTABLEEMPTYRESPONSE, ta);
					}
				}
				broker.send(Constants.BROKER_PROCEDUREWITHTABLESUCCESSFINISHED, ta);
			}));
		}
	}

	/**
	 * Öffet ein Popup, welches dem Nutzer über den Erfolg oder das Scheitern seiner Anfrage informiert
	 *
	 * @param message
	 */
	public void openNotificationPopup(String message) {
		if (!shell.getDisplay().isDisposed()) {
			MinovaNotifier.show(shell, getTranslation(message), getTranslation("Notification"));
		}
	}

	/**
	 * Diese Methode wird ausgeführt, wenn der Anwender einen neuen Datensatz eintragen möchte.
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
		sendEventToHelper(ActionCode.BEFORENEW);
		clearFields(map);
		updateGridLookupValues();
		// Helper-Klasse triggern, damit die Standard-Werte gesetzt werden können.
		sendEventToHelper(ActionCode.AFTERNEW);
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
		selectedOptionPages.clear();
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
		}

		// Revert Button updaten
		broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, "aero.minova.rcp.rcp.handledtoolitem.revert");
		// Auswahl im Index entfernen
		broker.send(Constants.BROKER_CLEARSELECTION, perspective);
	}

	/**
	 * Lädt den aktuellen Datensatz neu
	 */
	@Inject
	@Optional
	public void reloadFields(@UIEventTopic(Constants.BROKER_RELOADFIELDS) Table keyTable) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(perspective)) {
			if (keyTable == null) {
				keyTable = currentKeyTable;
			}
			readData(keyTable);
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
			sendEventToHelper(ActionCode.BEFOREREVERT);
			updateSelectedEntry();
			updateSelectedGrids();
			sendEventToHelper(ActionCode.AFTERREVERT);
		}
	}

	public Map<String, Value> getKeys() {
		return keys;
	}

	public void setKeys(Map<String, Value> map) {
		this.keys = map;
	}

	private void focusFirstEmptyField() {
		for (MSection section : mDetail.getMSectionList()) {
			for (MField field : section.getTabList()) {
				if (field.getValue() == null) {
					((AbstractValueAccessor) field.getValueAccessor()).getControl().setFocus();
					return;
				}
			}
		}

		// Falls kein leeres Feld gefunden wurde erstes Feld fokusieren
		((AbstractValueAccessor) mDetail.getMSectionList().get(0).getTabList().get(0).getValueAccessor()).getControl().setFocus();
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
		return checkFields() || checkOPs() || checkGrids();
	}

	private boolean checkFields() {
		// Prüfung der mFields ob es einen Value ≠ null gibt
		if (getSelectedTable() == null || getSelectedTable().getRows().isEmpty()) {
			for (MField mfield : mDetail.getFields()) {
				if (mfield instanceof MBooleanField) { // Boolean Felder haben nie null Wert -> Prüfung auf false
					if (mfield.getValue().getBooleanValue()) {
						return true;
					}
				} else if (mfield.getValue() != null) {
					return true;
				}
			}
			return false;
		}

		return checkFieldsWithTable(selectedTable, form);
	}

	/**
	 * Vergleicht Feld-Wert mit Wert aus Tabelle (vom CAS oder vorbelegte Werte aus Helpern)
	 */
	private boolean checkFieldsWithTable(Table t, Form f) {
		String fieldPrefix = f == form ? "" : f.getDetail().getProcedureSuffix() + "."; // OP-Felder haben OP-Namen als Prefix
		List<MField> checkedFields = new ArrayList<>();
		for (int i = 0; i < t.getColumnCount(); i++) {
			MField c = mDetail.getField(fieldPrefix + t.getColumnName(i));
			checkedFields.add(c);
			Value sV = t.getRows().get(0).getValue(i);
			if (c == null) {
				continue;
			}
			if (c instanceof MLookupField) {
				// LU mit index 0 gibt es nie
				if (c.getValue() == null && sV != null && sV.getIntegerValue() == 0) {
					continue;
				}

				if (sV == null && c.getValue() != null || //
						sV != null && c.getValue() == null || //
						c.getValue() != null && !c.getValue().getIntegerValue().equals(sV.getIntegerValue())) {
					return true;
				}
			} else if (c instanceof MBooleanField) { // Boolean Felder haben nie null Wert -> spezielle Prüfung
				if (sV == null && !c.getValue().getBooleanValue()) { // TableValue null -> Booleanfeld Wert soll false sein
					continue;
				}

				if (!c.getValue().equals(sV)) {
					return true;
				}
			} else if ((c.getValue() == null && sV != null) || (c.getValue() != null && !c.getValue().equals(sV))) {
				return true;
			}
		}

		// Sind die Felder in der Maske, die nicht in der ausgelesenen Tabelle sind, leer?
		for (Field field : dataFormService.getFieldsFromForm(f)) {
			MField mfield = mDetail.getField(fieldPrefix + field.getName());

			if (mfield instanceof MBooleanField) { // Boolean Felder haben nie null Wert -> Prüfung auf false
				if (!checkedFields.contains(mfield) && mfield.getValue().getBooleanValue()) {
					return true;
				}
			} else if (!checkedFields.contains(mfield) && mfield.getValue() != null) {
				return true;
			}
		}

		return false;
	}

	private boolean checkOPs() {
		// Sind die OP Felder leer?
		if (selectedOptionPages.isEmpty()) {
			for (Form opform : mDetail.getOptionPages()) {
				for (Field field : dataFormService.getFieldsFromForm(opform)) {
					String fieldName = opform.getDetail().getProcedureSuffix() + "." + field.getName();
					if (mDetail.getField(fieldName).getValue() != null) {
						return true;
					}
				}
			}
		}

		// Felder der OPs mit Werten vom CAS vergleichen
		for (Entry<String, Table> entry : selectedOptionPages.entrySet()) {
			Form opform = mDetail.getOptionPage(entry.getKey());
			Table table = entry.getValue();
			if (checkFieldsWithTable(table, opform)) {
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

	private void sendEventToHelper(ActionCode code) {
		if (mDetail.getHelper() != null) {
			mDetail.getHelper().handleDetailAction(code);
		}
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
		dataService.callProcedureAsync(t);
	}

	/**
	 * Zeichnet alle Felder der MSection neu. Falls Param-String Felder enthalten sind werden die aktuellen MFields dieser gezeichnet, alte Felder werden
	 * entfernt
	 *
	 * @param sectionID
	 */
	public void redrawSection(MSection mSection) {

		// SubFelder von Param-String Feldern entfernen
		List<MField> toRemove = new ArrayList<>();
		for (MField mField : mDetail.getFields()) {
			if (mField instanceof MParamStringField && mField.getmSection().equals(mSection)) {
				toRemove.addAll(((MParamStringField) mField).getSubMFields());
				((MParamStringField) mField).clearSubMFields();
			}
		}
		mSection.getTabList().removeAll(toRemove);
		Section section = ((SectionAccessor) mSection.getSectionAccessor()).getSection();
		mDetail.getFields().removeAll(toRemove);

		List<MField> visibleMFields = new ArrayList<>();

		List<MField> toTraverse = new ArrayList<>();
		toTraverse.addAll(mSection.getTabList());
		for (MField f : toTraverse) {
			visibleMFields.add(f);

			if (f instanceof MParamStringField) {
				String name = f.getName();
				String suffix = name.contains("\\.") ? name.substring(name.lastIndexOf("\\."), name.length()) : "";

				MParamStringField mParamString = (MParamStringField) f;
				for (Field subField : mParamString.getSubFields()) {
					MField subMField = wfcDetailPart.createMField(subField, mSection, suffix);
					visibleMFields.add(subMField);
					mParamString.addSubMField(subMField);

				}

			}
		}

		// Ganzen Body/ Client Area der Section entfernen
		section.getClient().dispose();

		// Neuen Body erstellen
		Composite clientComposite = wfcDetailPart.getFormToolkit().createComposite(section);
		clientComposite.setLayout(new FormLayout());
		wfcDetailPart.getFormToolkit().paintBordersFor(clientComposite);
		section.setClient(clientComposite);

		// Felder zeichnen
		wfcDetailPart.createUIFields(visibleMFields, clientComposite);

		// Sortieren der Fields nach Tab-Index.
		TabUtil.sortTabList(mSection);
		// Setzen der TabListe für die einzelnen Sections.
		clientComposite.setTabList(TabUtil.getTabListForSectionComposite(mSection, clientComposite));
		// Setzen der TabListe der Sections im Part.
		clientComposite.getParent().setTabList(TabUtil.getTabListForSection(clientComposite.getParent(), mSection, wfcDetailPart.isSelectAllControls()));

		section.requestLayout();
		TranslateUtil.translate(clientComposite, translationService, wfcDetailPart.getLocale());

	}

	/**
	 * Das übergebene MParamStringField wird mit den Feldern aus der Maske mit formName geladen. Dafür wird die gesamte Section neu gezeichnet
	 *
	 * @param mParamString
	 * @param formName
	 */
	public void updateParamStringField(MParamStringField mParamString, String formName) {
		Form f = dataFormService.getForm(formName);

		List<Field> subfields = new ArrayList<>();

		for (Object o : f.getDetail().getHeadAndPageAndGrid()) {
			if (o instanceof Head) {
				for (Object fieldOrGrid : ((Head) o).getFieldOrGrid()) {
					if (fieldOrGrid instanceof Field) {
						subfields.add((Field) fieldOrGrid);
					}
				}
			} else if (o instanceof Page) {
				for (Object fieldOrGrid : ((Page) o).getFieldOrGrid()) {
					if (fieldOrGrid instanceof Field) {
						subfields.add((Field) fieldOrGrid);
					}
				}
			}
		}

		mParamString.getSubFields().clear();
		mParamString.getSubFields().addAll(subfields);
		redrawSection(mParamString.getmSection());
	}

	private void updateGridLookupValues() {
		for (MGrid g : mDetail.getGrids()) {
			SectionGrid sg = ((GridAccessor) g.getGridAccessor()).getSectionGrid();
			sg.updateGridLookupValues();
		}
	}

}
