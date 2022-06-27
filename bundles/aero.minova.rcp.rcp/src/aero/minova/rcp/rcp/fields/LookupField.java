package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.css.ICssStyler;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.rcp.accessor.LookupValueAccessor;
import aero.minova.rcp.widgets.LookupComposite;
import aero.minova.rcp.widgets.LookupContentProvider;

public class LookupField {

	public static final String AERO_MINOVA_RCP_LOOKUP = "LookUp";

	public static Control create(Composite composite, MField field, int row, int column, Locale locale, MPerspective perspective) {
		Label label = FieldLabel.create(composite, field);

		IEclipseContext context = perspective.getContext();

		LookupComposite lookupControl = new LookupComposite(composite, SWT.BORDER | SWT.LEFT);
		lookupControl.setMessage("...");
		lookupControl.setLabel(label);
		ContextInjectionFactory.inject(lookupControl, context); // In Context injected, damit TranslationService genutzt werden kann

		LookupContentProvider contentProvider = new LookupContentProvider(field.getLookupTable());
		contentProvider.setLookup(lookupControl);
		lookupControl.setContentProvider(contentProvider);
		ContextInjectionFactory.inject(contentProvider, context); // In Context injected, damit TranslationService genutzt werden kann

		Label descriptionLabel = LabelFactory.newLabel(SWT.LEFT).create(composite);

		FormData lookupFormData = new FormData();
		FormData labelFormData = new FormData();
		FormData descriptionLabelFormData = new FormData();

		LookupValueAccessor lookupValueAccessor = new LookupValueAccessor(field, lookupControl);
		ContextInjectionFactory.inject(lookupValueAccessor, context);
		field.setValueAccessor(lookupValueAccessor);
		lookupControl.setData(Constants.CONTROL_FIELD, field);

		lookupFormData.top = new FormAttachment(composite, FieldUtil.MARGIN_TOP + row * FieldUtil.COLUMN_HEIGHT);
		lookupFormData.left = new FormAttachment((column == 0) ? 25 : 75);
		if (column >= 2) {
			lookupFormData.right = new FormAttachment(100, -MARGIN_BORDER);
		} else {
			lookupFormData.right = new FormAttachment(50, -ICssStyler.CSS_SECTION_SPACING);
		}

		// Lookup-Felder sollen immer genau eine Zeile hoch sein
		CssData cssData = new CssData(CssType.TEXT_FIELD, column, row, field.getNumberColumnsSpanned(), 1, field.isFillToRight() || field.isFillHorizontal());
		lookupControl.setData(CssData.CSSDATA_KEY, cssData);

		labelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(lookupControl, FieldUtil.MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = FieldUtil.COLUMN_WIDTH;

		descriptionLabelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		descriptionLabelFormData.left = new FormAttachment(lookupControl, FieldUtil.UNIT_GAP, SWT.RIGHT); // etwas Abstand zw. LookupWidget und Text
		if (field.getNumberColumnsSpanned() == 4) {
			descriptionLabelFormData.width = FieldUtil.MARGIN_LEFT * 2 + FieldUtil.COLUMN_WIDTH * 2;
		} else {
			descriptionLabelFormData.width = 0;
		}
		if (field.getNumberRowsSpanned() > 1) { // ZB für Contact Lookups
			descriptionLabelFormData.height = field.getNumberRowsSpanned() * FieldUtil.COLUMN_HEIGHT;
			descriptionLabelFormData.top = new FormAttachment(lookupControl, 0, SWT.TOP);
		}

		label.setData(AERO_MINOVA_RCP_LOOKUP, lookupControl);
		FieldLabel.layout(label, lookupControl, row, column, field.getNumberRowsSpanned());

		lookupControl.setLayoutData(lookupFormData);
		lookupControl.setDescription(descriptionLabel);

		descriptionLabel.setLayoutData(descriptionLabelFormData);
		descriptionLabel.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "DescriptionLabel");

		lookupControl.addTraverseListener(e -> {
			Text text = ((Text) e.getSource());
			LookupComposite t = (LookupComposite) text.getParent();
			switch (e.detail) {
			case SWT.TRAVERSE_TAB_PREVIOUS:
			case SWT.TRAVERSE_TAB_NEXT:
			case SWT.TRAVERSE_RETURN:
				t.fillSelectedValue();
				e.doit = true;
				break;
			}
		});
		return lookupControl;
	}

	/**
	 * Auslesen aller bereits einhgetragenen key die mit diesem Controll in Zusammenhang stehen Es wird eine Liste von Ergebnissen Erstellt, diese wird dem
	 * benutzer zur verfügung gestellt.
	 *
	 * @param luc
	 */
	@Inject
	@Optional
	@Deprecated
	public static void requestLookUpEntriesAll(MField field, MDetail detail, LookupComposite lookup) {

		BundleContext bundleContext = FrameworkUtil.getBundle(LookupField.class).getBundleContext();
		ServiceReference<?> serviceReference = bundleContext.getServiceReference(IDataService.class.getName());
		IDataService dataService = (IDataService) bundleContext.getService(serviceReference);

		CompletableFuture<List<LookupValue>> listLookup = dataService.listLookup((MLookupField) field, false);

		try {
			List<LookupValue> l = listLookup.get();
			lookup.getContentProvider().setValues(l);
		} catch (InterruptedException | ExecutionException e) {}
	}

	/**
	 * Tauscht die Optionen aus, welche dem LookupField zur Verfügung stehen
	 *
	 * @param ta
	 * @param c
	 */
	@Deprecated
	public static void changeOptionsForLookupField(Table ta, LookupComposite lookupControl, boolean twisty) {
		MLookupField field = (MLookupField) lookupControl.getData(Constants.CONTROL_FIELD);
		field.setOptions(ta);
		changeSelectionBoxList(lookupControl, field, twisty);
	}

	/**
	 * Diese Mtethode setzt die den Ausgewählten Wert direkt in das Control oder lässt eine Liste aus möglichen Werten zur Auswahl erscheinen.
	 *
	 * @param lookUpControl
	 */
	@Deprecated
	public static void changeSelectionBoxList(LookupComposite lookUpControl, MLookupField field, boolean twisty) {
		if (field.getOptions() != null) {
			Table t = field.getOptions();
			// Existiert nur ein Wert für das gegebene Feld, so wird überprüft ob die
			// Eingabe gleich dem gesuchten Wert ist.
			// Ist dies der Fall, so wird dieser Wert ausgewählt.
			// Der Wert wird auserdem immer als Option aufgelistet
			if (t.getRows().size() == 1) {
				if (lookUpControl != null && lookUpControl.getText() != null && !twisty) {
					Value value = t.getRows().get(0).getValue(t.getColumnIndex(Constants.TABLE_KEYTEXT));
					if (value.getStringValue().toLowerCase().startsWith((lookUpControl.getText().toString().toLowerCase()))) {
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
									.startsWith(r.getValue(t.getColumnIndex(Constants.TABLE_DESCRIPTION)).getStringValue().toLowerCase()))) {
								filteredTable.addRow(r);
							}
						}

					}
					// Existiert genau 1 Treffer, so wird geschaut ob dieser bereits 100%
					// übereinstimmt. Tut er dies, so wird statt dem setzen des Proposals direkt der
					// Wert gesetzt
					if (filteredTable.getRows().size() == 1) {
						Row row = filteredTable.getRows().get(0);
						if ((row.getValue(filteredTable.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue().toLowerCase()
								.equals(lookUpControl.getText().toLowerCase()))
								|| (row.getValue(filteredTable.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null
										&& row.getValue(filteredTable.getColumnIndex(Constants.TABLE_DESCRIPTION)).getStringValue().toLowerCase()
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
	public static void changeProposals(LookupComposite lc, Table t) {
		LookupContentProvider contentProvider = lc.getContentProvider();
		contentProvider.setTable(t);
	}
}
