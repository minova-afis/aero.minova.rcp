package aero.minova.rcp.rcp.accessor;

import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.model.form.IDetailAccessor;
import aero.minova.rcp.model.form.MDetail;

public class DetailAccessor implements IDetailAccessor {

	private MDetail mDetail;
	private Control selectedControl;

	public DetailAccessor(MDetail mDetail) {
		this.mDetail = mDetail;
	}

	public Control getSelectedControl() {
		return selectedControl;
	}

	public void setSelectedControl(Control selectedControl) {
		this.selectedControl = selectedControl;
	}

}
