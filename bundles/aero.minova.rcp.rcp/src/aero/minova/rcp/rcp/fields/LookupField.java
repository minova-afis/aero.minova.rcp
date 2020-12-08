package aero.minova.rcp.rcp.fields;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.core.runtime.ServiceCaller;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.ILocalDatabaseService;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.rcp.accessor.LookUpValueAccessor;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.LookupCASRequestUtil;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class LookupField {

	private static final String AERO_MINOVA_RCP_TRANSLATE_PROPERTY = "aero.minova.rcp.translate.property";
	private static final int COLUMN_WIDTH = 140;
	private static final int MARGIN_LEFT = 5;
	private static final int MARGIN_TOP = 5; // Zwischenräume
	private static final int COLUMN_HEIGHT = 28;

	public static Control create(Composite composite, MField field, int row, int column, FormToolkit formToolkit,
			IEventBroker broker, MPerspective perspective, ILocalDatabaseService localDatabaseService, MDetail detail) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		LookupControl lookupControl = new LookupControl(composite, SWT.LEFT);
		Label descriptionLabel = formToolkit.createLabel(composite, "", SWT.LEFT);
		FormData lookupFormData = new FormData();
		FormData labelFormData = new FormData();
		FormData descriptionLabelFormData = new FormData();

		IEclipseContext context = perspective.getContext();
		LookUpValueAccessor lookUpValueAccessor = new LookUpValueAccessor(field, detail, lookupControl);
		ContextInjectionFactory.inject(lookUpValueAccessor, context);
		field.setValueAccessor(lookUpValueAccessor);
		lookupControl.setData(Constants.CONTROL_FIELD, field);

		lookupFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		lookupFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		lookupFormData.width = COLUMN_WIDTH;

		labelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(lookupControl, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		descriptionLabelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		descriptionLabelFormData.left = new FormAttachment(lookupControl, 0, SWT.RIGHT);
		if (field.getNumberColumnsSpanned() != null && field.getNumberColumnsSpanned().intValue() == 4) {
			descriptionLabelFormData.width = MARGIN_LEFT * 2 + COLUMN_WIDTH * 2;
		} else {
			descriptionLabelFormData.width = 0;
		}

		label.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		lookupControl.setLayoutData(lookupFormData);
		lookupControl.setDescription(descriptionLabel);

		descriptionLabel.setLayoutData(descriptionLabelFormData);

		lookupControl.addTwistieMouseListener(new MouseAdapter() {

			@Override
			/*
			 * Aufruf der Prozedur mit um den Datensatz zu laden. prüfen ob noch andere
			 * LookUpFelder eingetragen wurden
			 */
			public void mouseDown(MouseEvent e) {
				requestLookUpEntriesAll(field, detail, lookupControl);
			}
		});

		// Hinzufügen von Keylistenern, sodass die Felder bei Eingaben
		// ihre Optionen auflisten können und ihren Wert bei einem Treffer übernehmen
		lookupControl.addKeyListener(new KeyListener() {
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
					if (e.keyCode != SWT.ARROW_DOWN && e.keyCode != SWT.ARROW_LEFT && e.keyCode != SWT.ARROW_RIGHT && e.keyCode != SWT.ARROW_UP
							&& e.keyCode != SWT.TAB && e.keyCode != SWT.CR && e.keyCode != SWT.SPACE && !lookupControl.getText().startsWith("#")) {
								if (((MLookupField) field).getOptions() == null) {
									requestLookUpEntriesAll(field, detail, lookupControl);
								} else {
									changeSelectionBoxList(lookupControl, (MLookupField) field, false);
								}
							} else
						if (e.keyCode == SWT.SPACE && controlPressed == true) {
							requestLookUpEntriesAll(field, detail, lookupControl);
						} else if (e.keyCode == SWT.ARROW_DOWN && lookupControl.isProposalPopupOpen() == false) {
							if (((MLookupField) field).getOptions() != null) {
								changeSelectionBoxList(lookupControl, (MLookupField) field, false);
							} else {
								requestLookUpEntriesAll(field, detail, lookupControl);
							}
						}
			}

		});
		return lookupControl;
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
	public static void requestLookUpEntriesAll(MField field, MDetail detail, LookupControl lookUpControl) {
		CompletableFuture<?> tableFuture;

		BundleContext bundleContext = FrameworkUtil.getBundle(LookupField.class).getBundleContext();
		ServiceReference<?> serviceReference = bundleContext.getServiceReference(IDataService.class.getName());
		IDataService dataService = (IDataService) bundleContext.getService(serviceReference);

		ServiceCaller<ILocalDatabaseService> localDatabaseService = new ServiceCaller<>(LookupField.class,
				ILocalDatabaseService.class);

		tableFuture = LookupCASRequestUtil.getRequestedTable(0, null, field, detail, dataService,
				"List");
		lookUpControl.getTextControl().setMessage("...");
		tableFuture.thenAccept(ta -> Display.getDefault().asyncExec(() -> {
			if (ta instanceof SqlProcedureResult) {
				SqlProcedureResult sql = (SqlProcedureResult) ta;
				localDatabaseService.current().get().replaceResultsForLookupField(field.getName(), sql.getResultSet());

				changeOptionsForLookupField(sql.getResultSet(), lookUpControl, true);
			} else if (ta instanceof Table) {
				Table t = (Table) ta;
				localDatabaseService.current().get().replaceResultsForLookupField(field.getName(), t);
				changeOptionsForLookupField(t, lookUpControl, true);
			}
		}));
	}

	/**
	 * Tauscht die Optionen aus, welche dem LookupField zur Verfügung stehen
	 *
	 * @param ta
	 * @param c
	 */
	public static void changeOptionsForLookupField(Table ta, LookupControl lookupControl, boolean twisty) {
		MLookupField field = (MLookupField) lookupControl.getData(Constants.CONTROL_FIELD);
		field.setOptions(ta);
		changeSelectionBoxList(lookupControl, field, twisty);
	}

	/**
	 * Diese Mtethode setzt die den Ausgewählten Wert direkt in das Control oder
	 * lässt eine Liste aus möglichen Werten zur Auswahl erscheinen.
	 *
	 * @param lookUpControl
	 */
	public static void changeSelectionBoxList(LookupControl lookUpControl, MLookupField field, boolean twisty) {
		if (field.getOptions() != null) {
			Table t = (Table) field.getOptions();
			// Existiert nur ein Wert für das gegebene Feld, so wird überprüft ob die
			// Eingabe gleich dem gesuchten Wert ist.
			// Ist dies der Fall, so wird dieser Wert ausgewählt. Ansonsten wird der Wert
			// aus dem CAS als Option/Proposal aufgelistet
			if (t.getRows().size() == 1) {
				if (lookUpControl != null && lookUpControl.getText() != null && !twisty) {
					updateSelectedLookupEntry(t, lookUpControl);
				}
				changeProposals(lookUpControl, t);
			} else {
				if (lookUpControl != null && lookUpControl.getText() != null && !twisty) {
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
								.startsWith(lookUpControl.getText().toLowerCase()))) {
							filteredTable.addRow(r);
						} else if (r.getValue(t.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
							if ((r.getValue(t.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue().toLowerCase()
									.startsWith(r.getValue(t.getColumnIndex(Constants.TABLE_DESCRIPTION)).getStringValue().toLowerCase()))) {
								filteredTable.addRow(r);
							}
						}

					}
					if (filteredTable.getRows().size() != 0) {
						changeProposals(lookUpControl, filteredTable);
					} else {
						changeProposals(lookUpControl, t);
					}
					// Setzen der Proposals/Optionen
				} else {
					changeProposals(lookUpControl, t);
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
	public static void changeProposals(LookupControl lc, Table t) {
		lc.setProposals(t);
	}

	public static void updateSelectedLookupEntry(Table ta, Control c) {
		Row r = ta.getRows().get(0);
		LookupControl lc = (LookupControl) c;
		int index = ta.getColumnIndex(Constants.TABLE_KEYTEXT);
		Value v = r.getValue(index);

		lc.setText((String) ValueBuilder.value(v).create());
		lc.getTextControl().setMessage("");
		if (lc.getDescription() != null && ta.getColumnIndex(Constants.TABLE_DESCRIPTION) > -1) {
			if (r.getValue(ta.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
				lc.getDescription().setText((String) ValueBuilder.value(r.getValue(ta.getColumnIndex(Constants.TABLE_DESCRIPTION))).create());
			} else {
				lc.getDescription().setText("");
			}
		}

	}
}
