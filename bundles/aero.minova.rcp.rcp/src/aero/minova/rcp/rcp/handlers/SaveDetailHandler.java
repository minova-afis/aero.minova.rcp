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

	private MPerspective mPerspective;

	private MPart mpart;

	// Sucht die aktiven Controls aus der XMLDetailPart und baut anhand deren Werte
	// eine Abfrage an den CAS zusammen. Anhand eines gegebenen oder nicht gegebenen
	// KeyLongs wird zwischen update und neuem Eintrag unterschieden
	@Execute
	public void execute(MPart mpart, MPerspective mPerspective, Shell shell) {
		this.shell = shell;
		this.mPerspective = mPerspective;
		this.mpart = mpart;

		List<MPart> findElements = model.findElements(mPerspective, PartsID.DETAIL_PART, MPart.class);
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

		if (t.getRows() != null) {
			CompletableFuture<Integer> tableFuture = dataService.getReturnCodeAsync(t.getName(), t);
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
	}

	// Überprüft. ob das Update erfolgreich war
	public void checkEntryUpdate(Integer responce) {

		if (responce != 0) {
			MessageDialog.openError(shell, "Error", "Entry could not be updated");
			return;
		}
		else {
			SucessDialog sucess = new SucessDialog(shell, "Sucessfully updated the entry");
			// sucess.setBlockOnOpen(false);
			sucess.open();
			sucess.close();
			// LoadIndexHandler li = new LoadIndexHandler();
			// li.execute(mpart, shell, mPerspective);
		}
	}

	// Überprüft, ob der neue Eintrag erstellt wurde
	public void checkNewEntryInsert(Integer responce) {
		if (responce != 0) {
			MessageDialog.openError(shell, "Error", "Entry could not be added");
		}
		else {
			SucessDialog sucess = new SucessDialog(shell, "Sucessfully added the entry");
			// sucess.setBlockOnOpen(false);
			sucess.open();
			sucess.close();
			// LoadIndexHandler li = new LoadIndexHandler();
			// li.execute(mpart, shell, mPerspective);
		}
	}
}