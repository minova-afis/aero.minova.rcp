package aero.minova.rcp.rcp.util;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class WFCDetailsLookupUtil {

	@Inject
	protected UISynchronize sync;

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private IDataService dataService;

	private Map<String, Control> controls = null;

	public WFCDetailsLookupUtil(Map<String, Control> controls) {
		this.controls = controls;

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
					} else {
						// Setzen der Proposals/Optionen
						changeProposals((LookupControl) c, t);
					}
				} else {
					sync.asyncExec(() -> DetailUtil.updateSelectedLookupEntry(t, c));
					System.out.println(t.getRows().get(0).getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)));
					c.setData(Constants.CONTROL_KEYLONG,
							t.getRows().get(0).getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)).getValue());

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
}
