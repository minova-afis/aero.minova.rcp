package aero.minova.rcp.model.helper;

import aero.minova.rcp.model.form.MDetail;

public interface IHelper {

	void setControls(MDetail detail);

	void handleDetailAction(ActionCode code);

}
