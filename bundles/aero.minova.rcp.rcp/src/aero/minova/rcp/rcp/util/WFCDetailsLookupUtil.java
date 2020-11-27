package aero.minova.rcp.rcp.util;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.ILocalDatabaseService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class WFCDetailsLookupUtil {

	protected UISynchronize sync;

	private ILocalDatabaseService localDatabaseService;

	private IDataService dataService;

	private MPerspective perspective = null;

	private Map<String, Control> controls = null;

	public WFCDetailsLookupUtil(Map<String, Control> controls, MPerspective perspective, IDataService dataService,
			UISynchronize sync, ILocalDatabaseService localDatabaseService) {
		this.controls = controls;
		this.perspective = perspective;
		this.dataService = dataService;
		this.sync = sync;
		this.localDatabaseService = localDatabaseService;
	}

	public void requestOptionsFromCAS(Control c) {
		Field field = (Field) c.getData(Constants.CONTROL_FIELD);
		CompletableFuture<?> tableFuture;
		tableFuture = LookupCASRequestUtil.getRequestedTable(0, ((LookupControl) c).getText(), field, controls,
				dataService, sync, "List");
		tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
			if (ta instanceof SqlProcedureResult) {
				SqlProcedureResult sql = (SqlProcedureResult) ta;
				localDatabaseService.replaceResultsForLookupField(field.getName(), sql.getResultSet());
				changeOptionsForLookupField(sql.getResultSet(), c, false);
			} else if (ta instanceof Table) {
				Table t = (Table) ta;
				// Nur für den Fall verwenden, das sämtliche Optionen gespeichert werden, um die
				// Latenz zwischen Index und Detail zu vermindern
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
						updateSelectedLookupEntry(t, c);
						lc.setData(Constants.CONTROL_KEYLONG,
								t.getRows().get(0).getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)));
					}
					// Setzen der Proposals/Optionen
					changeProposals((LookupControl) c, t);

				} else {
					updateSelectedLookupEntry(t, c);
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
							|| (filteredTable.getRows().size() != 0
									&& filteredTable.getRows().get(0)
											.getValue(filteredTable.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null
									&& filteredTable.getRows().get(0)
											.getValue(filteredTable.getColumnIndex(Constants.TABLE_DESCRIPTION))
											.getStringValue().toLowerCase().equals(lc.getText().toLowerCase()))) {
						c.setData(Constants.CONTROL_KEYLONG, filteredTable.getRows().get(0)
								.getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)).getValue());
						sync.asyncExec(() -> DetailUtil.updateSelectedLookupEntry(filteredTable, c));
						changeProposals(lc, filteredTable);
						// Setzen der Proposals/Optionen
					} else if (filteredTable.getRows().size() != 0) {
						changeProposals((LookupControl) lc, filteredTable);
						lc.setData(Constants.CONTROL_KEYLONG, null);
					} else {
						changeProposals((LookupControl) lc, t);
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

	public static void updateSelectedLookupEntry(Table ta, Control c) {
		System.out.println("Ich habe die Werte gesetzt");
		Row r = ta.getRows().get(0);
		LookupControl lc = (LookupControl) c;
		int index = ta.getColumnIndex(Constants.TABLE_KEYTEXT);
		Value v = r.getValue(index);

		lc.setText((String) ValueBuilder.value(v).create());
		lc.getTextControl().setMessage("");
		if (lc.getDescription() != null && ta.getColumnIndex(Constants.TABLE_DESCRIPTION) > -1) {
			if (r.getValue(ta.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
				lc.getDescription().setText((String) ValueBuilder
						.value(r.getValue(ta.getColumnIndex(Constants.TABLE_DESCRIPTION))).create());
			} else {
				lc.getDescription().setText("");
			}
		}

	}
}
