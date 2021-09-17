package aero.minova.rcp.rcp.util;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import aero.minova.rcp.rcp.parts.Preview;

public class PrintUtil {

	private PrintUtil() {}

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
	public static Preview checkPreview(MWindow window, EModelService modelService, EPartService partService, Preview preview) {
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
