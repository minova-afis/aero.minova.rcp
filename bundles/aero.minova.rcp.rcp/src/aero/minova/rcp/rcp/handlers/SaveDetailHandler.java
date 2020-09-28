package aero.minova.rcp.rcp.handlers;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

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

import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dialogs.SucessDialog;
import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.plugin1.model.Row;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.builder.RowBuilder;
import aero.minova.rcp.plugin1.model.builder.TableBuilder;
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

	// Sucht die aktiven Controls aus der XMLDetailPart und baut anhand deren Werte
	// eine Abfrage an den CAS zusammen. Anhand eines gegebenen oder nicht gegebenen
	// KeyLongs wird zwischen update und neuem Eintrag unterschieden
	@Execute
	// TODO: überprüfen, ob überschneidungen zwischen dem neuen eintrag und bereits
	// existierenden bestehen (Zeitenüberschneidungen)
	public void execute(MPart mpart, MPerspective mPerspective, Shell shell) {

		this.shell = shell;

		List<MPart> findElements = model.findElements(mPerspective, PartsID.DETAIL_PART, MPart.class);
		List<MPart> findSearchTable = model.findElements(mPerspective, PartsID.SEARCH_PART, MPart.class);
		searchTable = (Table) findSearchTable.get(0).getContext().get("NatTableDataSearchArea");
		XMLDetailPart xmlPart = (XMLDetailPart) findElements.get(0).getObject();
		Map<String, Control> controls = xmlPart.getControls();
		TableBuilder tb;
		RowBuilder rb = RowBuilder.newRow();
		if (xmlPart.getEntryKey() != 0) {
			tb = TableBuilder.newTable("spUpdateWorkingTime");
			tb.withColumn("KeyLong", DataType.INTEGER);
			rb.withValue(xmlPart.getEntryKey());
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
				}
				else {
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
		boolean contradiction = checkWorkingTime((int) controls.get("EmployeeKey").getData("keyLong"),
				((Text) controls.get("BookingDate")).getText(),
				((Text) controls.get("StartDate")).getText(), ((Text) controls.get("EndDate")).getText(),
				((Text) controls.get("RenderedQuantity")).getText(),
				((Text) controls.get("ChargedQuantity")).getText());
		// FOR TEST PURPOSE, DELETE
		contradiction = false;

		if (contradiction == false) {
			if (t.getRows() != null) {
				CompletableFuture<Table> tableFuture = dataService.getDetailDataAsync(t.getName(), t);
				if (t.getColumnName(0) != "KeyLong") {
					tableFuture.thenAccept(tr -> sync.asyncExec(() -> {
						checkNewEntryInsert(tr);
					}));
				} else {
					tableFuture.thenAccept(tr -> sync.asyncExec(() -> {
						checkEntryUpdate(tr);
					}));
				}
			}
		} else {
			MessageDialog.openError(shell, "Error", "Entry not possible, check for wrong inputs in your messured Time");
		}
	}

	// Eine Methode, welche eine Anfrage an den CAS versendet um zu überprüfen, ob
	// eine Überschneidung in den Arbeitszeiten vorliegt
	private boolean checkWorkingTime(int employee, String bookingDate, String startDate, String endDate,
			String renderedQuantity,
			String chargedQuantity) {
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
			return contradiction;
		}
		if ((renderedQuantityFloat != chargedQuantityFloat) && (renderedQuantityFloat != chargedQuantityFloat + 0.25)
				&& (renderedQuantityFloat != chargedQuantityFloat - 0.25)) {
			contradiction = true;
			return contradiction;
		}
		// Anfrage an den CAS um zu überprüfen, ob für den Mitarbeiter im angegebenen
		// Zeitrahmen bereits einträge existieren
		Table table = TableBuilder.newTable("spReadWorkingTime").withColumn("EmployeeKey", DataType.INTEGER)
				.withColumn("BookingDate", DataType.INSTANT).withColumn("StartDate", DataType.INSTANT)
				.withColumn("EndDate", DataType.INSTANT).create();
		Row r = RowBuilder.newRow().withValue(employee).withValue(bookingDate).withValue(">=" + startDate)
				.withValue("=<" + endDate).create();
		table.addRow(r);
		Table checkTimes = null;
		try {
			checkTimes = dataService.getDetailDataAsync(table.getName(), table).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (checkTimes.getRows() != null) {
			contradiction = true;
		}
		return contradiction;
	}

	// Überprüft. ob das Update erfolgreich war
	private void checkEntryUpdate(Table responce) {
		if (responce.getRows() != null) {
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
	private void checkNewEntryInsert(Table responce) {
		if (responce.getRows() != null) {
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