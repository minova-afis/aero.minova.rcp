package aero.minova.rcp.model.helper;

import aero.minova.rcp.model.form.MDetail;

public interface IHelper {

	void setControls(MDetail mDetail);

	void handleDetailAction(ActionCode code);

}
