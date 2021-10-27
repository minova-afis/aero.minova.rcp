package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.rcp.parts.WFCDetailPart;

public class OptimizeDetailHandler {

	@Execute
	public void execute(EModelService emservice, MWindow mwindow) {
		MPart detail = emservice.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.part.details", MPart.class).get(0);

		// Höhe mindestens 900 Pixel
		if (mwindow.getHeight() < 900) {
			mwindow.setHeight(900);

			MPartStack searchPartStack = emservice
					.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.partstack.search", MPartStack.class).get(0);
			searchPartStack.setContainerData("35");
			MPartStack indexPartStack = emservice.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.partstack.index", MPartStack.class)
					.get(0);
			indexPartStack.setContainerData("65");
		}

		Integer defaultSectionWidth = WFCDetailPart.SECTION_WIDTH;
		WFCDetailPart wfcDetailPart = (WFCDetailPart) detail.getObject();
		Composite detailComposite = wfcDetailPart.getComposite();
		int prefDetailWidth = detailComposite.computeSize(SWT.DEFAULT, mwindow.getHeight()).x;

		// Standardbreite defaultSectionWidth (für Index/Suche) + Benötigte Breite für Detail (können mehrere Sections nebeneinander sein) + 25 Pixel
		if (mwindow.getWidth() < defaultSectionWidth + prefDetailWidth + 25) {
			mwindow.setWidth(defaultSectionWidth + prefDetailWidth + 25);
		}

		int size = 10000;
		float detailSize = (float) (prefDetailWidth + 15.0) / mwindow.getWidth();
		float leftSize = 1.0f - detailSize;

		MPartSashContainer element = emservice
				.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.partsashcontainer.main", MPartSashContainer.class).get(0);
		for (MPartSashContainerElement e : element.getChildren()) {
			if (e.getElementId() != null && e.getElementId().equalsIgnoreCase("aero.minova.rcp.rcp.partstack.details")) {
				e.setContainerData(Integer.toString((int) (detailSize * size)));
			} else {
				e.setContainerData(Integer.toString((int) (leftSize * size)));
			}
		}
	}
}