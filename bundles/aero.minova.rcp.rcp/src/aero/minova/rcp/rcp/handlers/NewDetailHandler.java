package aero.minova.rcp.rcp.handlers;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.rcp.parts.XMLDetailPart;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class NewDetailHandler {

	@Inject
	EModelService model;

	@Execute
	public void execute(MPart mpart, MPerspective mPerspective) {
		List<MPart> findElements = model.findElements(mPerspective, PartsID.DETAIL_PART, MPart.class);
		XMLDetailPart xmlPart = (XMLDetailPart)findElements.get(0).getObject();
		Map<String, Control> controls = xmlPart.getControls();
		for (Control c : controls.values()) {
			if (c instanceof Text) {
				Text t = (Text) c;
				t.setText("");
			}
			if (c instanceof LookupControl) {
				LookupControl l = (LookupControl) c;
				l.setText("");
			}
		}
		xmlPart.setEntryKey(0);
	}
}
