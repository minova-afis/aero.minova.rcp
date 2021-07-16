package aero.minova.rcp.rcp.handlers;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class OptimizeDetailHandler {

	@Execute
	public void execute(EModelService emservice, MWindow mwindow) {
		MPartSashContainer element = (MPartSashContainer) emservice.find("aero.minova.rcp.rcp.partsashcontainer.main",
				mwindow);
		element.getChildren();
		
		List<MPart> partStack = emservice.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.part.details", MPart.class);
		MPart detail = partStack.get(0);

		Integer detailWidth = (Integer) detail.getContext().get("Detail_Width");

		int size = 10000;

		if (mwindow.getWidth() < (detailWidth * 2) + 25) {
			// Standardbreite min 2x Detailbereich + 10Pixel
			mwindow.setWidth((detailWidth * 2) + 25);
		}

		float dummy = (float) ((detailWidth + 25.0) / mwindow.getWidth());
		float dummy2 = 1.0f - dummy;


		for (MPartSashContainerElement e : element.getChildren()) {
			if (e.getElementId().equalsIgnoreCase("aero.minova.rcp.rcp.partstack.details")) {
				e.setContainerData(Integer.toString((int) (dummy * size)));
			} else {
				e.setContainerData(Integer.toString((int)(dummy2 * size)));
			}
		}
	}

}
