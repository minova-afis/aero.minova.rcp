package aero.minova.rcp.rcp.handlers;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dialogs.ErrorDialog;
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

	@Execute
	public void execute(MPart mpart, MPerspective mPerspective, Shell shell) {
		this.shell = shell;
		List<MPart> findElements = model.findElements(mPerspective, PartsID.DETAIL_PART, MPart.class);
		XMLDetailPart xmlPart = (XMLDetailPart) findElements.get(0).getObject();
		Map<String, Control> controls = xmlPart.getControls();
		TableBuilder tb = TableBuilder.newTable("");
		RowBuilder rb = RowBuilder.newRow();
		if (xmlPart.getEntryKey() != 0) {
			tb.withColumn("KeyLong", DataType.INTEGER);
			rb.withValue(xmlPart.getEntryKey());
		}
		int i = 0;
		for (Control c : controls.values()) {
			String s = (String) controls.keySet().toArray()[i];
			if (c instanceof Text) {
				Text t = (Text) c;
				tb.withColumn(s, (DataType) t.getData("dataType"));
				if (!(t.getText().isBlank())) {
					rb.withValue(t.getText());
				} else {
					rb.withValue(null);

				}
			}
			if (c instanceof LookupControl) {
				LookupControl l = (LookupControl) c;
				tb.withColumn(s, (DataType) l.getData("dataType"));
				// TODO: Tablecalles to get the correct value, NOT the string (dates,
				// doubles,...)
				if (l.getData("keylong") != null) {
					rb.withValue(l.getData("keyLong"));
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
				ErrorDialog errorDialog = new ErrorDialog(shell, "Not all Fields were filled");
				errorDialog.open();
				return;
			}
		}
		t.addRow(r);

//		if (t.getColumnName(0) != "Keylong") {
//			CompletableFuture<Table> tableFuture = dataService.sendNewEntry(t.getName(), t);
//			tableFuture.thenAccept(tr -> sync.asyncExec(() -> {
//				checkNewEntryInsert(tr);
//			}));
//		} else {
//			CompletableFuture<Table> tableFuture = dataService.updateEntry(t.getName(), t);
//			tableFuture.thenAccept(tr -> sync.asyncExec(() -> {
//				checkEntryUpdate(tr);
//			}));
//		}

	}

	public void checkEntryUpdate(Object responce) {
		if (!(responce instanceof Table)) {
			System.out.println("Error: Entry could not be updated");
			ErrorDialog errorDialog = new ErrorDialog(shell, "Error: Entry could not be updated");
			errorDialog.open();
		}
		else {
			// TODO: implement success popup that will show up for 3 seconds in the corner
		}
	}

	public void checkNewEntryInsert(Object responce) {
		if (!(responce instanceof Table)) {
			System.out.println("Error: Entry could not be pushed");
			ErrorDialog errorDialog = new ErrorDialog(shell, "Error: Entry could not be pushed");
			errorDialog.open();
		}
		else {
			// TODO: implement success popup that will show up for 3 seconds in the corner
		}
	}
}
