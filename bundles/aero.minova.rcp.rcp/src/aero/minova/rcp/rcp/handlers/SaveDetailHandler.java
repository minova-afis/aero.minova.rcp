package aero.minova.rcp.rcp.handlers;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dialogs.SucessDialog;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.rcp.parts.XMLDetailPart;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class SaveDetailHandler {

	@Inject
	private IEventBroker broker;

	@Inject
	private EModelService model;

	@Inject
	private IDataService dataService;

	@Inject
	private UISynchronize sync;

	private Shell shell;

	private Table searchTable;

	@CanExecute
	public boolean canExecute(MPart mpart) {
		// TODO
		System.out.println("TODO canExecute");
		return true;
	}

	// Sucht die aktiven Controls aus der XMLDetailPart und baut anhand deren Werte
	// eine Abfrage an den CAS zusammen. Anhand eines gegebenen oder nicht gegebenen
	// KeyLongs wird zwischen update und neuem Eintrag unterschieden
	@Execute
	// TODO: überprüfen, ob überschneidungen zwischen dem neuen eintrag und bereits
	// existierenden bestehen (Zeitenüberschneidungen)
	public void execute(MPart mpart, MPerspective mPerspective, Shell shell) {

		this.shell = shell;
		XMLDetailPart xmlPart = (XMLDetailPart) mpart;
		Map<String, Control> controls = xmlPart.getControls();
		TableBuilder tb;
		RowBuilder rb = RowBuilder.newRow();
		if (xmlPart.getKeys() != null) {
			tb = TableBuilder.newTable("spUpdateWorkingTime");
			for (ArrayList key : xmlPart.getKeys()) {
				tb.withColumn((String) key.get(0), (DataType) key.get(2));
				rb.withValue(key.get(1));
			}
		} else {
			tb = TableBuilder.newTable("spInsertWorkingTime");
		}
		int i = 0;
		for (Control c : controls.values()) {
			String s = (String) controls.keySet().toArray()[i];
			if (c instanceof Text) {
				tb.withColumn(s, (DataType) c.getData("dataType"));
				if (!(((Text) c).getText().isBlank())) {
					rb.withValue(((Text) c).getText());
				} else {
					rb.withValue(null);

				}
			}
			if (c instanceof LookupControl) {
				tb.withColumn(s, (DataType) c.getData("dataType"));
				if (c.getData("keyLong") != null) {
					rb.withValue(c.getData("keyLong"));
				} else {
					rb.withValue(null);
				}
			}
			i++;
		}
		Table t = tb.create();
		Row r = rb.create();
		for (i = 0; i < t.getColumnCount(); i++) {
			if (r.getValue(i) == null) {
				MessageDialog.openError(shell, "Error", "not all Fields were filled");
				return;
			}
		}
		t.addRow(r);
		checkWorkingTime((int) controls.get("EmployeeKey").getData("keyLong"),
				((Text) controls.get("BookingDate")).getText(), ((Text) controls.get("StartDate")).getText(),
				((Text) controls.get("EndDate")).getText(), ((Text) controls.get("RenderedQuantity")).getText(),
				((Text) controls.get("ChargedQuantity")).getText(), t);
	}

	// Eine Methode, welche eine Anfrage an den CAS versendet um zu überprüfen, ob
	// eine Überschneidung in den Arbeitszeiten vorliegt
	private void checkWorkingTime(int employee, String bookingDate, String startDate, String endDate,
			String renderedQuantity, String chargedQuantity, Table t) {
		boolean contradiction = false;

		// Prüfen, ob die bemessene Arbeitszeit der differenz der Stunden entspricht
		LocalTime timeEndDate = LocalTime.parse(endDate);
		LocalTime timeStartDate = LocalTime.parse(startDate);
		float timeDifference = ((timeEndDate.getHour() * 60) + timeEndDate.getMinute())
				- ((timeStartDate.getHour() * 60) + timeStartDate.getMinute());
		timeDifference = timeDifference / 60;

		float renderedQuantityFloat = Float.parseFloat(renderedQuantity);
		float chargedQuantityFloat = Float.parseFloat(chargedQuantity);
		if (timeDifference != renderedQuantityFloat) {
			contradiction = true;
		}
		if ((renderedQuantityFloat != chargedQuantityFloat) && (renderedQuantityFloat != chargedQuantityFloat + 0.25)
				&& (renderedQuantityFloat != chargedQuantityFloat - 0.25)) {
			contradiction = true;
		}
		// Anfrage an den CAS um zu überprüfen, ob für den Mitarbeiter im angegebenen
		// Zeitrahmen bereits einträge existieren
		if (contradiction != true) {

			Table table = TableBuilder.newTable("spReadWorkingTime").withColumn("EmployeeKey", DataType.INTEGER)
					.withColumn("BookingDate", DataType.INSTANT).withColumn("StartDate", DataType.INSTANT)
					.withColumn("EndDate", DataType.INSTANT).create();
			Row r = RowBuilder.newRow().withValue(employee).withValue(bookingDate).withValue(">=" + startDate)
					.withValue("=<" + endDate).create();
			table.addRow(r);

			CompletableFuture<SqlProcedureResult> checkTimes = dataService.getDetailDataAsync(table.getName(), table);
			checkTimes.thenAccept(ta -> sync.asyncExec(() -> {

				continueCheck(t, ta.getOutputParameters());
			}));
		}
	}

	private void continueCheck(Table t, Table ta) {
		if (ta.getRows() != null) {
			if (t.getRows() != null) {
				// TODO: umbau auf SqlProcedureResult
				CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(t.getName(), t);
				if (t.getColumnName(0) != "KeyLong") {
					tableFuture.thenAccept(tr -> sync.asyncExec(() -> {
						checkNewEntryInsert(tr.getReturnCode());
					}));
				} else {
					tableFuture.thenAccept(tr -> sync.asyncExec(() -> {
						checkEntryUpdate(tr.getReturnCode());
					}));
				}
			}
		} else {
			MessageDialog.openError(shell, "Error", "Entry not possible, check for wrong inputs in your messured Time");
		}
	}

	// Überprüft. ob das Update erfolgreich war
	private void checkEntryUpdate(int responce) {
		if (responce != 1) {
			MessageDialog.openError(shell, "Error", "Entry could not be updated");
			return;
		} else {
			SucessDialog sucess = new SucessDialog(shell, "Sucessfully updated the entry");
			// MessageDialog sucess = new MessageDialog(shell, "Sucess", null, "Sucessfully
			// updated the entry",
			// MessageDialog.NONE, new String[] {}, 0);
			// sucess.setBlockOnOpen(false);
			sucess.open();
			// sucess.close();
			// reload the indexTable
			CompletableFuture<Table> tableFuture = dataService.getIndexDataAsync(searchTable.getName(), searchTable);
			tableFuture.thenAccept(ta -> broker.post("PLAPLA", ta));
		}
	}

	// Überprüft, ob der neue Eintrag erstellt wurde
	private void checkNewEntryInsert(int responce) {
		if (responce != 1) {
			MessageDialog.openError(shell, "Error", "Entry could not be added");
		} else {
			SucessDialog sucess = new SucessDialog(shell, "Sucessfully added the entry");
			// sucess.setBlockOnOpen(false);
			sucess.open();
			// sucess.close();
			// reload the indexTable
			CompletableFuture<Table> tableFuture = dataService.getIndexDataAsync(searchTable.getName(), searchTable);
			tableFuture.thenAccept(ta -> broker.post("PLAPLA", ta));
		}
	}
}