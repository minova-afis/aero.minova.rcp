
package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;

public class LoadFromMatchcode {

	@Inject
	private EModelService model;

	@Inject
	private EPartService partService;

	@Inject
	private IDataFormService dataFormService;

	@Execute
	public void execute(MPerspective mPerspective) {
		List<MPart> findElements = model.findElements(mPerspective, Constants.DETAIL_PART, MPart.class);
		MPart part = findElements.get(0);
		partService.activate(part);
		WFCDetailPart detailPart = (WFCDetailPart) part.getObject();
		MField field = detailPart.getDetail().getField(Constants.TABLE_KEYTEXT);
		((AbstractValueAccessor) field.getValueAccessor()).getControl().setFocus();

		Table t = dataFormService.getTableFromFormIndex(detailPart.getForm());
		Row r = new Row();
		for (Column c : t.getColumns()) {
			if(c.getName().equals(Constants.TABLE_KEYTEXT)) {
				r.addValue(field.getValue());
			} else {
				r.addValue(new Value(null, c.getType()));
			}
		}
		t.addRow(r);
		

		WFCDetailCASRequestsUtil wDCR = new WFCDetailCASRequestsUtil();
		wDCR.readData(t);
	}

}