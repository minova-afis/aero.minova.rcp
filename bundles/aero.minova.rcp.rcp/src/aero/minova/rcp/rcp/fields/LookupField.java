package aero.minova.rcp.rcp.fields;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.core.runtime.ServiceCaller;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.rcp.accessor.LookupValueAccessor;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.LookupCASRequestUtil;
import aero.minova.rcp.rcp.widgets.Lookup;
import aero.minova.rcp.rcp.widgets.LookupContentProvider;

public class LookupField {

	private static final String AERO_MINOVA_RCP_TRANSLATE_PROPERTY = "aero.minova.rcp.translate.property";
	public static final String AERO_MINOVA_RCP_LOOKUP = "LookUp";
	private static final int COLUMN_WIDTH = 140;
	private static final int MARGIN_LEFT = 5;
	private static final int MARGIN_TOP = 5; // Zwischenräume
	private static final int COLUMN_HEIGHT = 28;

	public static Control create(Composite composite, MField field, int row, int column, FormToolkit formToolkit,
			IEventBroker broker, MPerspective perspective, ILocalDatabaseService localDatabaseService, MDetail detail, Locale locale) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		LookupContentProvider lookUpContentProvider = new LookupContentProvider();
		Lookup lookupControl = new Lookup(composite, SWT.BORDER | SWT.LEFT, lookUpContentProvider);
		// TODO übersetzen
		lookupControl.setMessage("...");
		lookupControl.setNumberOfLines(50);
		lookupControl.setLabel(label);

		Label descriptionLabel = formToolkit.createLabel(composite, "", SWT.LEFT);
		FormData lookupFormData = new FormData();
		FormData labelFormData = new FormData();
		FormData descriptionLabelFormData = new FormData();

		IEclipseContext context = perspective.getContext();
		LookupValueAccessor lookupValueAccessor = new LookupValueAccessor(field, detail, lookupControl, descriptionLabel);
		ContextInjectionFactory.inject(lookupValueAccessor, context);
		field.setValueAccessor(lookupValueAccessor);
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
		label.setData(AERO_MINOVA_RCP_LOOKUP, lookupControl);
		label.setLayoutData(labelFormData);

		lookupControl.setLayoutData(lookupFormData);
		lookupControl.setDescription(descriptionLabel);

		descriptionLabel.setLayoutData(descriptionLabelFormData);

		lookupControl.addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				Text text = ((Text) e.getSource());
				Lookup t = (Lookup) text.getParent();
				System.out.println("Pressed key: " + e.keyCode);
				switch (e.detail) {
				case SWT.TRAVERSE_TAB_PREVIOUS:
					t.fillSelectedValue();
					e.doit = true;
					// entfernen des Details, damit niemand mehr dieses TRAVERSE_RETURN Event
					// verwenden kann.
					break;
				case SWT.TRAVERSE_TAB_NEXT:
				case SWT.TRAVERSE_RETURN:
					// Hier übernehmen wir den ersten Treffer der Liste, falls es einen Eintrag gibt
					// edit same column previous row
					t.fillSelectedValue();
					e.doit = true;
					// entfernen des Details, damit niemand mehr dieses TRAVERSE_RETURN Event
					// verwenden kann.
					e.detail = SWT.TRAVERSE_TAB_NEXT;
					break;
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
	public static void requestLookUpEntriesAll(MField field, MDetail detail, Lookup lookUpControl) {
		CompletableFuture<?> tableFuture;

		BundleContext bundleContext = FrameworkUtil.getBundle(LookupField.class).getBundleContext();
		ServiceReference<?> serviceReference = bundleContext.getServiceReference(IDataService.class.getName());
		IDataService dataService = (IDataService) bundleContext.getService(serviceReference);

		ServiceCaller<ILocalDatabaseService> localDatabaseService = new ServiceCaller<>(LookupField.class,
				ILocalDatabaseService.class);

		tableFuture = LookupCASRequestUtil.getRequestedTable(0, null, field, detail, dataService, "List");
		lookUpControl.setMessage("...");
		tableFuture.thenAccept(ta -> Display.getDefault().asyncExec(() -> {
			if (ta instanceof SqlProcedureResult) {
				SqlProcedureResult sql = (SqlProcedureResult) ta;
				localDatabaseService.current().get().replaceResultsForLookupField(field.getName(), sql.getResultSet());

				changeOptionsForLookupField(sql.getResultSet(), lookUpControl, true);
			} else if (ta instanceof Table) {
				Table t = (Table) ta;
				localDatabaseService.current().get().replaceResultsForLookupField(field.getName(), t);
				changeOptionsForLookupField(t, lookUpControl, true);
				lookUpControl.showAllElements("%");
			}
		}));
	}

	/**
	 * Tauscht die Optionen aus, welche dem LookupField zur Verfügung stehen
	 *
	 * @param ta
	 * @param c
	 */
	public static void changeOptionsForLookupField(Table ta, Lookup lookupControl, boolean twisty) {
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
	public static void changeSelectionBoxList(Lookup lookUpControl, MLookupField field, boolean twisty) {
		if (field.getOptions() != null) {
			Table t = field.getOptions();
			// Existiert nur ein Wert für das gegebene Feld, so wird überprüft ob die
			// Eingabe gleich dem gesuchten Wert ist.
			// Ist dies der Fall, so wird dieser Wert ausgewählt.
			// Der Wert wird auserdem immer als Option aufgelistet
			if (t.getRows().size() == 1) {
				if (lookUpControl != null && lookUpControl.getText() != null && !twisty) {
					Value value = t.getRows().get(0).getValue(t.getColumnIndex(Constants.TABLE_KEYTEXT));
					if (value.getStringValue().toLowerCase()
							.startsWith((lookUpControl.getText().toString().toLowerCase()))) {
						field.setValue(t.getRows().get(0).getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)), false);
					}
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
									.startsWith(r.getValue(t.getColumnIndex(Constants.TABLE_DESCRIPTION))
											.getStringValue().toLowerCase()))) {
								filteredTable.addRow(r);
							}
						}

					}
					// Existiert genau 1 Treffer, so wird geschaut ob dieser bereits 100%
					// übereinstimmt. Tut er dies, so wird statt dem setzen des Proposals direkt der
					// Wert gesetzt
					if (filteredTable.getRows().size() == 1) {
						Row row = filteredTable.getRows().get(0);
						if ((row.getValue(filteredTable.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue()
								.toLowerCase().equals(lookUpControl.getText().toLowerCase()))
								|| (row.getValue(filteredTable.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null
										&& row.getValue(filteredTable.getColumnIndex(Constants.TABLE_DESCRIPTION))
												.getStringValue().toLowerCase()
												.equals(lookUpControl.getText().toLowerCase()))) {
							field.setValue(row.getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)), false);
						}
						changeProposals(lookUpControl, filteredTable);
						// Setzen der Proposals/Optionen
					} else if (filteredTable.getRows().size() != 0) {
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
	public static void changeProposals(Lookup lc, Table t) {
		LookupContentProvider contentProvider = lc.getContentProvider();
		contentProvider.setTable(t);
	}
}
