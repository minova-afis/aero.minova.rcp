package aero.minova.rcp.rcp.handlers;

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

public class DeleteDetailHandler {

	@Inject
	private IEventBroker broker;

	@Inject
	private EModelService model;

	@Inject
	private IDataService dataService;

	@Inject
	private UISynchronize sync;

	private Shell shell;

	private Table searchTable = null;

	@Execute

	// Sucht die aktiven Controls aus der XMLDetailPart und baut anhand deren Werte
	// eine Abfrage an den CAS zusammen
	public void execute(MPart mpart, MPerspective mPerspective, Shell shell) {
		this.shell = shell;

		List<MPart> findElements = model.findElements(mPerspective, PartsID.DETAIL_PART, MPart.class);
		List<MPart> findSearchTable = model.findElements(mPerspective, PartsID.SEARCH_PART, MPart.class);
		searchTable = (Table) findSearchTable.get(0).getContext().get("NatTableDataSearchArea");
		XMLDetailPart xmlPart = (XMLDetailPart) findElements.get(0).getObject();
		Map<String, Control> controls = xmlPart.getControls();
		TableBuilder tb = TableBuilder.newTable("spDeleteWorkingTime");
		RowBuilder rb = RowBuilder.newRow();
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
		if (t.getRows() != null) {
			Table responce = null;
			try {
				responce = dataService.getDetailDataAsync(t.getName(), t).get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Boolean sucess = false;
			sucess = deleteEntry(responce);

			if (sucess == true) {
				for (Control c : controls.values()) {
					if (c instanceof Text) {
						((Text) c).setText("");
					}
					if (c instanceof LookupControl) {
						((LookupControl) c).setText("");
					}
				}
				// reload the indexTable
				CompletableFuture<Table> tableFuture = dataService.getIndexDataAsync(searchTable.getName(),
						searchTable);
				tableFuture.thenAccept(ta -> broker.post("PLAPLA", ta));
			}
		}

	}

	// Überprüft, ob die Anfrage erfolgreich war, falls nicht bleiben die Textfelder
	// befüllt um die Anfrage anzupassen
	public boolean deleteEntry(Table responce) {
		if (responce.getRows() != null) {
			MessageDialog.openError(shell, "Error", "Entry could not be deleted");
			return false;
		} else {
			SucessDialog sucess = new SucessDialog(shell, "Sucessfully deleted the entry");
			// sucess.setBlockOnOpen(false);
			sucess.open();
			// sucess.close();
			return true;
		}
	}
}
