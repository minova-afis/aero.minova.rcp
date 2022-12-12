package aero.minova.rcp.rcp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.widgets.MinovaSection;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.EventParam;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.form.model.xsd.Procedure;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.KeyType;
import aero.minova.rcp.model.OutputType;
import aero.minova.rcp.model.ReferenceValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.TransactionEntry;
import aero.minova.rcp.model.TransactionResultEntry;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.form.MBrowser;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MParamStringField;
import aero.minova.rcp.model.form.MQuantityField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.model.helper.ActionCode;
import aero.minova.rcp.model.helper.IHelper;
import aero.minova.rcp.model.util.ErrorObject;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.rcp.accessor.BrowserAccessor;
import aero.minova.rcp.rcp.accessor.DetailAccessor;
import aero.minova.rcp.rcp.accessor.GridAccessor;
import aero.minova.rcp.rcp.accessor.SectionAccessor;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.widgets.BrowserSection;
import aero.minova.rcp.rcp.widgets.SectionGrid;

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
	IEventBroker broker;

	@Inject
	Logger logger;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.AUTO_RELOAD_INDEX)
	boolean autoReloadIndex;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.SHOW_DISCARD_CHANGES_DIALOG_INDEX)
	boolean showDiscardDialogIndex;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	public String timezone;

	@Inject
	DirtyFlagUtil dirtyFlagUtil;

	private MDetail mDetail;

	private MPerspective perspective;

	private Map<String, Value> keys = null;

	private Table selectedTable;
	private Map<String, Table> selectedOptionPages;
	private Map<String, Table> selectedGrids;

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
	}

	/**
	 * Bei Auswahl eines Indexes wird anhand der in der Row vorhandenen Daten eine Anfrage an den CAS versendet, um sämltiche Informationen zu erhalten
	 *
	 * @param rows
	 */
	@Inject
	public void readData(@Optional @Named(Constants.BROKER_ACTIVEROWS) Table table) {
		readData(table, true); // Wenn aus dem Index oder aus Helper soll prinzipiell die Discard-Message angezeigt werden
	}

	public void readData(@Optional @Named(Constants.BROKER_ACTIVEROWS) Table table, boolean showDiscard) {
		if (table == null || table.getRows().isEmpty()) {
			return;
		}

		Display.getDefault().asyncExec(() -> {
			if (showDiscardDialogIndex && showDiscard && !discardChanges()) {
				broker.send(Constants.BROKER_CLEARSELECTION, perspective);
				return;
			}
			currentKeyTable = table;

			sendEventToHelper(ActionCode.BEFOREREAD);
			updateGridLookupValues();

			// Hauptfelder, OPs und Grids in einer Transaktion lesen
			List<TransactionEntry> procedureList = new ArrayList<>();

			Table rowIndexTable = createReadTableFromForm(form, table);
			procedureList.add(new TransactionEntry(Constants.TRANSACTION_PARENT, rowIndexTable));

			for (Form opForm : mDetail.getOptionPages()) {
				Table opFormTable = createReadTableFromForm(opForm, table);
				procedureList.add(new TransactionEntry(Constants.OPTION_PAGE + "_" + opForm.getDetail().getProcedureSuffix(), opFormTable));
			}

			addGridsToReadList(table, procedureList);

			CompletableFuture<List<TransactionResultEntry>> transactionResult = dataService.callTransactionAsync(procedureList);

			transactionResult.thenAccept(list -> sync.asyncExec(() -> {
				if (list != null && !list.isEmpty()) {

					for (TransactionResultEntry resEntry : list) {
						String id = resEntry.getId();
						SqlProcedureResult res = resEntry.getSQLProcedureResult();
						if (id.equals(Constants.TRANSACTION_PARENT)) { // Hauptmaske
							selectedTable = res.getOutputParameters();
						} else if (id.startsWith(Constants.OPTION_PAGE + "_")) { // OP
							String opID = id.substring(id.indexOf("_") + 1);
							getSelectedOptionPages().put(opID, res.getOutputParameters());
						} else if (id.startsWith(Constants.GRID + "_")) { // Grid
							MGrid g = mDetail.getGrid(id.substring(id.indexOf("_") + 1));
							if (g != null && res.getResultSet() != null) {
								setGridContent(g, res.getResultSet());
							}
						}
					}

					// Alle Browser im Detailbereich leeren
					for (MBrowser mB : mDetail.getBrowsers()) {
						BrowserAccessor bA = (BrowserAccessor) mB.getBrowserAccessor();
						bA.getBrowserSection().clear();
					}

					updateSelectedEntry(false);
					sendEventToHelper(ActionCode.AFTERREAD);
					broker.send(Constants.BROKER_CHECKDIRTY, "");
				}
			}));
		});
	}

	private void addGridsToReadList(Table keyTable, List<TransactionEntry> procedureList) {
		for (MGrid g : mDetail.getGrids()) {
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
					if (!sg.getFieldnameToValue().isEmpty()) { // Zuordnung aus .xbs nutzen, Keys aus keyTable
						if (sg.getFieldnameToValue().containsKey(f.getName())) {
							found = true;
							String valueFromXBS = sg.getFieldnameToValue().get(f.getName());
							Value v = null;
							if (!valueFromXBS.startsWith(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL)) {
								Row row = keyTable.getRows().get(0);
								v = row.getValue(keyTable.getColumnIndex(valueFromXBS));
							} else {
								v = new Value(valueFromXBS.substring(1), sg.getDataTable().getColumn(f.getName()).getType());
							}
							gridRowBuilder.withValue(v);
						}

					} else { // Default Verhalten, entsprechenden Wert über Namen aus keyTable holen, erster Primary bekommt KeyLong
						int index = keyTable.getColumnIndex(f.getName());
						if (firstPrimary) {
							index = keyTable.getColumnIndex(Constants.TABLE_KEYLONG);
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

			procedureList.add(new TransactionEntry(Constants.GRID + "_" + g.getId(), gridRequestTable));
		}
	}

	public void setGridContent(MGrid g, Table data) {
		getSelectedGrids().put(g.getId(), data.copy());
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
			} else if (keysToValue.containsKey(f.getName()) && !keysToValue.get(f.getName()).startsWith(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL)) {
				MField correspondingField = mDetail.getField(keysToValue.get(f.getName()));
				indexInRow = keyTable.getColumnIndex(correspondingField.getName());
			}

			// Wert in Zeile setzten
			if (indexInRow >= 0 && "primary".equals(f.getKeyType()) && row.getValue(indexInRow) != null) {
				builder.withValue(row.getValue(indexInRow).getValue());
				newKeys.put(f.getName(), row.getValue(indexInRow));
			} else {
				builder.withValue(null);
				if (f.getQuantity() != null) {
					builder.withValue(null);
				}
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
	 * Setzt die Werte aus der selectedTable und selectedOptionPages in die Felder
	 *
	 * @param emptyFieldsNotInTable
	 *            Wenn true, werden Felder, die nicht in selectedTable/selectedOptionPages vorkommen auf Wert null gesetzt. Wenn false bleiben sie unverändert
	 */
	public void updateSelectedEntry(boolean emptyFieldsNotInTable) {
		List<MField> checkedFields = new ArrayList<>();

		// Hauptmaske
		if (selectedTable != null) {
			checkedFields.addAll(setFieldsFromTable(null, selectedTable));
		}

		// Option Pages
		for (Entry<String, Table> e : getSelectedOptionPages().entrySet()) {
			checkedFields.addAll(setFieldsFromTable(e.getKey(), e.getValue()));
		}

		if (emptyFieldsNotInTable) {
			for (MField f : mDetail.getFields()) {
				if (!checkedFields.contains(f)) {
					f.setValue(null, false);
				}
			}
		}

		// Revert Button updaten
		broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, Constants.REVERT_DETAIL_BUTTON);
	}

	/*
	 * Updatet die Felder mit der übergebenen Tabelle
	 */
	private List<MField> setFieldsFromTable(String opName, Table table) {
		List<MField> checkedFields = new ArrayList<>();
		for (int i = 0; i < table.getColumnCount(); i++) {
			String name = (opName == null ? "" : opName + ".") + table.getColumnName(i);
			MField f = mDetail.getField(name);
			if (f != null) {
				checkedFields.add(f);
				if (f instanceof MQuantityField) {
					int columnI = table.getColumnIndex(((MQuantityField) f).getUnitFieldName());
					f.setUnitText(translationService.translate(table.getRows().get(0).getValue(columnI).getStringValue(), null));
				}
				f.setValue(table.getRows().get(0).getValue(i), false);
			}
		}

		return checkedFields;
	}

	public void updateSelectedGrids() {
		for (String gridID : getSelectedGrids().keySet()) {
			updateSelectedGrid(gridID);
		}
	}

	public void updateSelectedGrid(String gridID) {
		MGrid mGrid = mDetail.getGrid(gridID);
		GridAccessor gVA = (GridAccessor) mGrid.getGridAccessor();
		SectionGrid sectionGrid = gVA.getSectionGrid();
		Table t = sectionGrid.setDataTable(getSelectedGrids().get(gridID).copy());
		sectionGrid.clearDataChanges();
		getSelectedGrids().put(gridID, t);
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

			// Hauptmaske, OPs und Grids werden in einer Transaktion aktualisiert/gespeichert
			List<TransactionEntry> procedureList = new ArrayList<>();
			Table formTable = createInsertUpdateTableFromForm(form);
			procedureList.add(new TransactionEntry(Constants.TRANSACTION_PARENT, formTable));

			for (Form opForm : mDetail.getOptionPages()) {
				Table opFormTable = createInsertUpdateTableFromForm(opForm);
				procedureList.add(new TransactionEntry(Constants.OPTION_PAGE + "_" + opForm.getDetail().getProcedureSuffix(), opFormTable));
			}

			addGridsToUpdateInsertTransaction(procedureList);

			sendSaveRequest(procedureList);
		}
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

			if (getKeys() == null && buildForm.equals(form)) { // Hauptmaske, keine Keys gegeben (Insert)
				rb.withValue(null);
			} else if (keysToIndex != null && keysToIndex.containsKey(f.getName())) { // OPs
				if (getKeys() != null) { // Bei Update Key-Wert aus Hauptmaske
					MField correspondingField = mDetail.getField(keysToIndex.get(f.getName()));
					rb.withValue(getKeys().get(correspondingField.getName()));
				} else { // Bei Insert ReferenceValue auf Hauptmasken-Wert setzen
					rb.withValue(new ReferenceValue(Constants.TRANSACTION_PARENT, keysToIndex.get(f.getName())));
				}
			} else if (getKeys() != null && getKeys().containsKey(f.getName())) { // Hauptmaske, Keys gegeben (Update)
				rb.withValue(getKeys().get(f.getName()));
			} else {
				rb.withValue(null);
			}
			valuePosition++;
		}

		while (valuePosition < formTable.getColumnCount()) {
			String fieldname = (buildForm == form ? "" : buildForm.getDetail().getProcedureSuffix() + ".") + formTable.getColumnName(valuePosition);
			MField field = mDetail.getField(fieldname);
			rb.withValue(field.getValue());
			if(field instanceof MQuantityField) {
				rb.withValue(new Value(field.getUnitText(), DataType.STRING));
				valuePosition++;
			}
			valuePosition++;
		}

		// anhand der Maske wird der Defaultwert und der DataType des Fehlenden Row-Wertes ermittelt und der Row angefügt
		Row r = rb.create();
		formTable.addRow(r);
		return formTable;
	}

	public void setValuesAccordingToXBS() {
		for (Form optionPage : mDetail.getOptionPages()) {
			String optionPageName = optionPage.getDetail().getProcedureSuffix();
			for (Entry<String, String> e : mDetail.getOptionPageKeys(optionPageName).entrySet()) {
				String value = e.getValue();
				MField opField = mDetail.getField(optionPageName + "." + e.getKey());

				if (value.startsWith(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL)) {

					try {
						Value v = StaticXBSValueUtil.stringToValue(value.substring(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL.length()), opField.getDataType(),
								opField.getDateTimeType(), translationService, timezone);
						opField.setValue(v, false);
						setValueAsCleanForDirtyFlag(v, e.getKey(), optionPageName);
					} catch (Exception exception) {
						NoSuchFieldException error = new NoSuchFieldException("String \"" + value.substring(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL.length())
								+ "\" can't be parsed to Type \"" + opField.getDataType() + "\" of Field \"" + e.getKey() + "\"! (As defined in .xbs)");
						logger.error(error);
						MessageDialog.openError(Display.getCurrent().getActiveShell(), ERROR, error.getMessage());
					}
				} else {
					opField.setValue(mDetail.getField(value).getValue(), false);
					setValueAsCleanForDirtyFlag(mDetail.getField(value).getValue(), e.getKey(), optionPageName);
				}
			}
		}
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
				boolean isAField = mDetail.getField(fieldname) != null ? true : false;
				if (isAField && mDetail.getField(fieldname).isPrimary()) {
					c.setOutputType(OutputType.OUTPUT);
				}
			}
		}
		return formTable;
	}

	/*
	 * Führt Update, Insert und Delete Anfragen auf Zeilen in Grids aus
	 */
	private void addGridsToUpdateInsertTransaction(List<TransactionEntry> procedureList) {
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
				procedureList.add(new TransactionEntry(Constants.GRID + "_" + g.getId() + "_" + Constants.DELETE_REQUEST, gridDeleteTable));
			}
			if (!gridInsertTable.getRows().isEmpty()) {
				procedureList.add(new TransactionEntry(Constants.GRID + "_" + g.getId() + "_" + Constants.INSERT_REQUEST, gridInsertTable));
			}
			if (!gridUpdateTable.getRows().isEmpty()) {
				procedureList.add(new TransactionEntry(Constants.GRID + "_" + g.getId() + "_" + Constants.UPDATE_REQUEST, gridUpdateTable));
			}
		}
	}

	private void sendSaveRequest(List<TransactionEntry> procedureList) {
		if (!procedureList.isEmpty()) {
			CompletableFuture<List<TransactionResultEntry>> transactionResult = dataService.callTransactionAsync(procedureList);

			transactionResult.thenAccept(tr -> sync.asyncExec(() -> {
				// Speichern wieder aktivieren
				broker.post(Constants.BROKER_SAVECOMPLETE, true);
				checkNewEntryInsertUpdate(tr, getKeys() == null);
			}));

			// Auch bei Fehler Speichern wieder aktivieren
			transactionResult.exceptionally(ex -> {
				broker.post(Constants.BROKER_SAVECOMPLETE, true);
				return null;
			});
		} else {
			broker.send(Constants.BROKER_SHOWNOTIFICATION, "msg.ActionAborted");
		}
	}

	/**
	 * Überprüft, ob der neue Eintrag erstellt/geupdatet wurde.
	 *
	 * @param resultList
	 */
	private void checkNewEntryInsertUpdate(List<TransactionResultEntry> resultList, boolean insert) {
		if (resultList == null) {
			return;
		}

		// Überprüfen ob es einen Fehler gab, dann darf Detail nicht geleert/neugeladen werden. Fehlermeldung wird schon vom DataService angezeigt
		for (TransactionResultEntry entry : resultList) {
			if (entry.getSQLProcedureResult().getReturnCode() == -1) {
				return;
			}
		}

		SqlProcedureResult mainResult = resultList.get(0).getSQLProcedureResult();
		if (insert) {
			setKeysFromTable(mainResult.getOutputParameters());
			broker.send(Constants.BROKER_SHOWNOTIFICATION, "msg.DataSaved");
			handleUserAction(Constants.INSERT_REQUEST, mainResult.getOutputParameters());
		} else {
			broker.send(Constants.BROKER_SHOWNOTIFICATION, "msg.DataUpdated");
			handleUserAction(Constants.UPDATE_REQUEST, currentKeyTable);
		}

		if (autoReloadIndex) {
			ParameterizedCommand cmd = commandService.createCommand(Constants.AERO_MINOVA_RCP_RCP_COMMAND_LOADINDEX, null);
			handlerService.executeHandler(cmd);
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

			updateGridLookupValues();
			sendEventToHelper(ActionCode.BEFOREDEL);

			// Hauptmaske, OPs und Grids werden in einer Transaktion gelöscht
			List<TransactionEntry> procedureList = new ArrayList<>();
			Table t = createDeleteTableFromForm(form);
			procedureList.add(new TransactionEntry(Constants.TRANSACTION_PARENT, t));

			for (Form opForm : mDetail.getOptionPages()) {
				Table opFormTable = createDeleteTableFromForm(opForm);
				procedureList.add(new TransactionEntry(Constants.OPTION_PAGE + "_" + opForm.getDetail().getProcedureSuffix(), opFormTable));
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
					procedureList.add(new TransactionEntry(Constants.GRID + "_" + g.getId(), gridDeleteTable));
				}
			}

			CompletableFuture<List<TransactionResultEntry>> transactionResult = dataService.callTransactionAsync(procedureList);
			try {
				deleteEntry(transactionResult.get());
			} catch (ExecutionException e) {
				logger.error(e);
			} catch (InterruptedException e) {
				logger.error(e);
				Thread.currentThread().interrupt();
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
	 * Überprüft, ob die Anfrage erfolgreich war, falls nicht bleiben die Textfelder befüllt um die Anfrage anzupassen.
	 *
	 * @param response
	 */
	public void deleteEntry(List<TransactionResultEntry> resultList) {

		// Überprüfen ob es einen Fehler gab, dann darf Detail nicht geleert werden. Fehlermeldung wird schon vom DataService angezeigt
		for (TransactionResultEntry entry : resultList) {
			if (entry.getSQLProcedureResult().getReturnCode() == -1) {
				return;
			}
		}

		if (autoReloadIndex) {
			ParameterizedCommand cmd = commandService.createCommand(Constants.AERO_MINOVA_RCP_RCP_COMMAND_LOADINDEX, null);
			handlerService.executeHandler(cmd);
		}
		broker.send(Constants.BROKER_SHOWNOTIFICATION, "msg.DataDeleted");
		Map<MPerspective, String> map = new HashMap<>();
		map.put(perspective, Constants.DELETE_REQUEST);
		clearFields(map);
		sendEventToHelper(ActionCode.AFTERDEL);
		focusFirstEmptyField();
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
					ErrorObject e = new ErrorObject(ta.getResultSet(), dataService.getUserName());
					broker.send(Constants.BROKER_SHOWERROR, e);
				} else if (ta != null) {
					selectedTable = ta.getResultSet();
					if (!selectedTable.getRows().isEmpty()) {
						updateSelectedEntry(false);
					} else {
						broker.send(Constants.BROKER_PROCEDUREWITHTABLEEMPTYRESPONSE, ta);
					}
				}
				broker.send(Constants.BROKER_PROCEDUREWITHTABLESUCCESSFINISHED, ta);
			}));
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
		updateGridLookupValues();
		sendEventToHelper(ActionCode.BEFORENEW);
		clearFields(map);
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
		if (dirtyFlagUtil.isDirty()) {
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

		selectedTable = null;
		getSelectedOptionPages().clear();
		setKeys(null);

		// Entfernen der Sub-Fields von den paramString Feldern
		ArrayList<MField> paramfields = new ArrayList<>();
		for (MField f : mDetail.getFields()) {
			if (f instanceof MParamStringField) {
				paramfields.addAll(((MParamStringField) f).getSubMFields());
			}
		}
		mDetail.getFields().removeAll(paramfields);
		// Felder auf Null setzen!
		for (MField f : mDetail.getFields()) {
			if (f instanceof MQuantityField) {
				f.setUnitText(translationService.translate(((MQuantityField) f).getOriginalUnitText(), null));
			}
			f.setValue(null, false);
			if (f instanceof MLookupField) {
				((MLookupField) f).setOptions(null);
			}
		}

		// Grids leeren
		getSelectedGrids().clear();
		for (MGrid g : mDetail.getGrids()) {
			SectionGrid sg = ((GridAccessor) g.getGridAccessor()).getSectionGrid();
			sg.clearGrid();
		}

		// Browser leeren
		for (MBrowser b : mDetail.getBrowsers()) {
			BrowserSection bs = ((BrowserAccessor) b.getBrowserAccessor()).getBrowserSection();
			bs.clear();
		}

		// In XBS gegebene Felder wieder füllen
		setValuesAccordingToXBS();

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
			readData(keyTable, false); // Nach Speichern soll discard-Nachricht nicht angezeigt werden
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
			updateSelectedEntry(true);
			updateSelectedGrids();
			sendEventToHelper(ActionCode.AFTERREVERT);
		}
	}

	public Map<String, Value> getKeys() {
		return keys;
	}

	public void setKeys(Map<String, Value> map) {
		this.keys = map;
		updateKeytypeUserReadonly();
	}

	private void focusFirstEmptyField() {
		for (MSection section : mDetail.getMSectionList()) {
			for (MField field : section.getTabList()) {
				if (field.getValue() == null && !field.isReadOnly() && !field.isLabelText()) {
					((AbstractValueAccessor) field.getValueAccessor()).getControl().setFocus();
					return;
				}
			}
		}

		// Falls kein leeres Feld gefunden wurde erstes Feld fokusieren
		List<MField> tabList = mDetail.getMSectionList().get(0).getTabList();
		if (!tabList.isEmpty()) {
			((AbstractValueAccessor) tabList.get(0).getValueAccessor()).getControl().setFocus();
		}
	}

	public Table getSelectedTable() {
		return selectedTable;
	}

	public void setSelectedTable(Table table) {
		this.selectedTable = table;
	}

	public Map<String, Table> getSelectedOptionPages() {
		return selectedOptionPages;
	}

	public Map<String, Table> getSelectedGrids() {
		return selectedGrids;
	}

	public void clearSelectedGrids() {
		this.getSelectedGrids().clear();
	}

	@Inject
	@Optional
	private void sendEventToHelper(@UIEventTopic(Constants.BROKER_SENDEVENTTOHELPER) ActionCode code) {
		for (IHelper helper : mDetail.getHelpers()) {
			helper.handleDetailAction(code);
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

		// Selektiertes Feld herausfinden, um es nach dem Neu-Zeichnen wieder auszuwählen
		Control focussedControl = ((DetailAccessor) mDetail.getDetailAccessor()).getSelectedControl();
		MField focussed = null;
		if (focussedControl != null && !focussedControl.isDisposed()) {
			focussed = (MField) focussedControl.getData(Constants.CONTROL_FIELD);
		}

		// SubFelder von Param-String Feldern entfernen
		List<MField> toRemove = new ArrayList<>();
		for (MField mField : mDetail.getFields()) {
			if (mField instanceof MParamStringField && mField.getMSection().equals(mSection)) {
				toRemove.addAll(((MParamStringField) mField).getSubMFields());
				((MParamStringField) mField).clearSubMFields();
			}
		}
		mSection.getMFields().removeAll(toRemove);
		mDetail.getFields().removeAll(toRemove);

		List<MField> visibleMFields = new ArrayList<>();

		List<MParamStringField> paramStringFields = new ArrayList<>();
		List<MField> tmp = new ArrayList<>();
		tmp.addAll(mSection.getMFields());
		for (MField f : tmp) {
			if (f.isVisible()) {
				visibleMFields.add(f);
			}

			if (f instanceof MParamStringField) {
				String name = f.getName();
				String suffix = name.contains("\\.") ? name.substring(name.lastIndexOf("\\."), name.length()) : "";

				MParamStringField mParamString = (MParamStringField) f;
				paramStringFields.add(mParamString);
				for (Field subField : mParamString.getSubFields()) {
					MField subMField = wfcDetailPart.createMField(subField, mSection, suffix);
					if (subMField.isVisible()) {
						visibleMFields.add(subMField);
					}
					mParamString.addSubMField(subMField);
				}
			}
		}

		// Ganzen Body/ Client Area der Section entfernen
		MinovaSection section = ((SectionAccessor) mSection.getSectionAccessor()).getSection();
		section.getClient().dispose();

		// Neuen Body erstellen
		Composite clientComposite = wfcDetailPart.getFormToolkit().createComposite(section);
		clientComposite.setLayout(new FormLayout());
		wfcDetailPart.getFormToolkit().paintBordersFor(clientComposite);
		section.setClient(clientComposite);

		// Felder zeichnen
		wfcDetailPart.createUIFields(visibleMFields, clientComposite);

		// Setzen der TabListe für die einzelnen Sections
		TabUtil.updateTabListOfSectionComposite(clientComposite);
		// Setzen der TabListe der Sections im Part.
		clientComposite.getParent().setTabList(TabUtil.getTabListForSection(clientComposite.getParent(), mSection, wfcDetailPart.isSelectAllControls()));

		// Vorherige Wert wieder in UI eintragen
		for (MField f : mSection.getMFields()) {
			if (f.getValueAccessor() != null) {
				f.getValueAccessor().setValue(f.getValue(), false);
			}
		}
		paramStringFields.stream().forEach(f -> f.setValue(new Value(f.getCacheValue().getStringValue() + " "), false));

		section.requestLayout();
		section.style();
		TranslateUtil.translate(clientComposite, translationService, wfcDetailPart.getLocale());

		// Zuvor selektiertes Feld wieder auswählen
		if (focussed != null && focussed.getValueAccessor() != null && !((AbstractValueAccessor) focussed.getValueAccessor()).getControl().isDisposed()) {
			((AbstractValueAccessor) focussed.getValueAccessor()).getControl().setFocus();
		}

	}

	/**
	 * Das übergebene MParamStringField wird mit den Feldern aus der Maske mit formName geladen. Dafür wird die gesamte Section neu gezeichnet
	 *
	 * @param mParamString
	 * @param formName
	 */
	public void updateParamStringField(MParamStringField mParamString, String formName) {

		List<Field> subfields = new ArrayList<>();

		if (formName != null) {
			Form f = dataFormService.getForm(formName);
			for (Object o : f.getDetail().getHeadAndPageAndGrid()) {
				List<Object> fieldsOrGrids = new ArrayList<>();

				if (o instanceof Head) {
					fieldsOrGrids = ((Head) o).getFieldOrGrid();
				} else if (o instanceof Page) {
					fieldsOrGrids = ((Page) o).getFieldOrGrid();
				}

				for (Object fieldOrGrid : fieldsOrGrids) {
					if (fieldOrGrid instanceof Field) {
						subfields.add((Field) fieldOrGrid);
					}
				}
			}
		}

		mParamString.getSubFields().clear();
		mParamString.getSubFields().addAll(subfields);
		redrawSection(mParamString.getMSection());
	}

	private void updateGridLookupValues() {
		for (MGrid g : mDetail.getGrids()) {
			SectionGrid sg = ((GridAccessor) g.getGridAccessor()).getSectionGrid();
			sg.updateGridLookupValues();
		}
	}

	/**
	 * Setzt Felder mit key-type="user" (v.a. Matchcodes) auf read-only, wenn der Datensatz bereits einmal gespeichert wurde. Ansonsten können sie wie in der
	 * Maske angegeben bearbeitet werden.
	 */
	private void updateKeytypeUserReadonly() {
		for (MField f : mDetail.getFields()) {
			if (!f.isKeyTypeUser()) {
				continue;
			}
			if (getKeys() == null) {
				f.resetReadOnlyAndRequired();
			} else {
				f.setReadOnly(true);
			}
		}
	}

	/**
	 * Mit dieser Methode können Values gesetzt werden, sodass sie vom Dirty-Flag als "clean" angesehen werden. Solange das Feld also diesen Wert hat springt
	 * das Dirty-Flag für dieses nicht an. Das kann z.B. in Helpern genutzt werden, um Felder vorzubelegen.
	 * 
	 * @param v
	 *            Wert, der als clean angesehen werden soll
	 * @param fieldName
	 *            Name des Feldes. Für Felder in OptionPages OHNE den Prefix (Name der OptionPage)
	 * @param opName
	 *            ProcedureSuffix der OptionPage in der das Feld ist, oder null für Feld der Hauptmaske
	 */
	public void setValueAsCleanForDirtyFlag(Value v, String fieldName, String opName) {

		Table t = null;
		if (opName != null) {
			t = selectedOptionPages.get(opName);
		} else {
			t = selectedTable;
		}

		if (t == null) {
			t = new Table();
			t.addRow();
		}

		Row r = t.getRows().get(0);

		// Spalte existiert noch nicht, muss erstellt werden
		if (t.getColumnIndex(fieldName) == -1) {
			t.getRows().clear();
			String suffix = opName != null ? opName + "." : "";
			t.addColumn(new Column(fieldName, mDetail.getField(suffix + fieldName).getDataType()));
			r.addValue(v);
			t.addRow(r);
		}

		t.setValue(fieldName, r, v);

		if (opName != null) {
			selectedOptionPages.put(opName, t);
		} else {
			selectedTable = t;
		}

		broker.post(Constants.BROKER_CHECKDIRTY, ""); // Check triggern
	}

	/**
	 * @deprecated Stattdessen {@link DirtyFlagUtil#checkDirty()} nutzen. Einige Helper benutzen diese Methode, deshalb bleibt sie
	 * @return
	 */
	@Deprecated(since = "12.0.36", forRemoval = false)
	public boolean checkDirty() {
		return dirtyFlagUtil.checkDirty();
	}
}
