package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.rcp.parts.WFCDetailPart;

public class OptimizeDetailHandler {

	@Execute
	public void execute(EModelService emservice, MWindow mwindow) {
		MPart detail = emservice.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.part.details", MPart.class).get(0);
		Integer detailWidth = (Integer) detail.getContext().get(Constants.DETAIL_WIDTH);

		// Standardbreite min 2x Detailbereich + 10Pixel
		if (mwindow.getWidth() < (detailWidth * 2) + 25 && detailWidth == WFCDetailPart.SECTION_WIDTH) {
			mwindow.setWidth((detailWidth * 2) + 25);
		}

		int size = 10000;
		float detailSize = (float) ((detailWidth + 25.0) / mwindow.getWidth());
		float leftSize = 1.0f - detailSize;

		MPartSashContainer element = emservice
				.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.partsashcontainer.main", MPartSashContainer.class).get(0);
		for (MPartSashContainerElement e : element.getChildren()) {
			if (e.getElementId().equalsIgnoreCase("aero.minova.rcp.rcp.partstack.details")) {
				e.setContainerData(Integer.toString((int) (detailSize * size)));
			} else {
				e.setContainerData(Integer.toString((int) (leftSize * size)));
			}
		}

		// Standardhöhe
		if (mwindow.getHeight() < 700) {
			mwindow.setHeight(700);

			MPartStack searchPartStack = emservice
					.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.partstack.search", MPartStack.class).get(0);
			searchPartStack.setContainerData("35");
			MPartStack indexPartStack = emservice.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.partstack.index", MPartStack.class)
					.get(0);
			indexPartStack.setContainerData("65");
		}
	}
}
