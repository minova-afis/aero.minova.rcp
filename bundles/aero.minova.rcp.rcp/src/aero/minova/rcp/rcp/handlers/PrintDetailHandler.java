package aero.minova.rcp.rcp.handlers;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import aero.minova.rcp.rcp.parts.Preview;
import aero.minova.rcp.rcp.util.Tools;

public class PrintDetailHandler {
	@Execute
	public void execute(MPart mpart, MWindow window, EModelService modelService, EPartService partService, @Optional Preview preview) {
		try {
			preview = checkPreview(window, modelService, partService, preview);

			String fileName = "/Users/erlanger/Documents/Urlaubsantrag_250221.pdf";
			// Report erzeugen
//		ReportCreator reportCreator = new IndexReportCreator(form, getColumnInfo(part), getGroupInfo(part), getSortInfo(part), guiFont, indexFont,
//				optimizeFieldWidth, hideEmptyCols, hideGroupCols, hideSearchCriterias);
//		String fileName = reportCreator.create();
//
//		Log.debug(this, MessageFormat.format("Report {0} wurde erzeugt", fileName));
			File f = new File(fileName);
			URL url2 = f.toURI().toURL();
			showFile(url2.toString(), preview);
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
