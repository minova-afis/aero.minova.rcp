package aero.minova.rcp.rcp.handlers;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.internal.DataService;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.parts.Preview;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.util.Tools;

public class PrintDetailHandler {

	@Inject
	DataService dataService;

	@Inject
	protected UISynchronize sync;

	@Execute
	public void execute(MPart mpart, MWindow window, EModelService modelService, EPartService partService, @Optional Preview preview) {
		try {
			// String fileName = "/Users/erlanger/Documents/Urlaubsantrag_250221.pdf";

			Object wfcDetailpart = mpart.getObject();
			WFCDetailPart wfcDetail = null;
			if (wfcDetailpart instanceof WFCDetailPart) {
				wfcDetail = (WFCDetailPart) wfcDetailpart;
			}
			MField field = wfcDetail.getDetail().getField(Constants.CONTROL_KEYLONG);
			Integer integerValue = field.getValue().getIntegerValue();

			Table table = TableBuilder.newTable("xpctsPrintTestergebnis").withColumn("KeyLong", DataType.INTEGER).create();
			Row row = RowBuilder.newRow().withValue(integerValue).create();
			table.addRow(row);

			// Table Bauen, Tabellenname + KeyLong
			CompletableFuture<Path> tableFuture = dataService.getPDFAsync("xpctsPrintTestergebnis", table);
			tableFuture.thenAccept(tr -> sync.asyncExec(() -> {

				URL url;
				try {
					url = tr.toFile().toURI().toURL();
					showFile(url.toString(), checkPreview(window, modelService, partService, preview));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

			}));
		} catch (Exception ex) {
			System.out.println("Drucken geht nicht!");
			ex.printStackTrace();
		}
	}

	private void showFile(String urlString, Preview preview) {
		if (urlString != null) {
			System.out.println(MessageFormat.format("versuche {0} anzuzeigen", urlString));
			try {
				if (preview == null) {
					System.out.println(MessageFormat.format("öffne {0} auf dem Desktop", urlString));
					Tools.openURL(urlString);
				} else {
					System.out.println(MessageFormat.format("öffne {0} im Preview-Fenster", urlString));
					preview.openURL(urlString);
				}
			} catch (final Exception e) {
				e.printStackTrace();
				System.out.println("Error occured during the file open");
			}
		} else {
			System.out.println("kann Datei NULL nicht anzeigen");
		}

	}

	/**
	 * Bereitet die Druckvorschau vor
	 *
	 * @param window
	 *            das aktuelle Fenster (benötigt um den Druckvorschau-Part zu finden)
	 * @param modelService
	 *            der ModelService (benötigt um den Druckvorschau-Part zu finden)
	 * @param partService
	 *            der PartService (benötigt um den Druckvorschau-Part anzuzeigen)
	 * @param preview
	 *            der Druckvorschau-Part (falls nicht vorhanden, wird versucht, einen zu erzeugen)
	 * @return der Druckvorschau-Part (falls schon vorhanden, wird dasselbe Objekt zurückgegeben)
	 * @author wild
	 * @since 11.0.0
	 */
	protected static Preview checkPreview(MWindow window, EModelService modelService, EPartService partService, Preview preview) {
		if (preview == null) {
			// Wir suchen mal nach dem Druck-Part und aktivieren ihn
			MPart previewPart = (MPart) modelService.find(Preview.PART_ID, window);
			if (previewPart.getObject() == null) {
				partService.showPart(previewPart, PartState.CREATE);
			}
			previewPart.setVisible(true);
			previewPart.getParent().setSelectedElement(previewPart);
			preview = (Preview) previewPart.getObject();
		} else {
			preview.clear();
		}

		return preview;
	}

}
