package aero.minova.rcp.model.helper;

import aero.minova.rcp.model.form.MDetail;
import org.eclipse.jface.wizard.IWizard;

public interface IMinovaWizard extends IWizard {

	void setOriginalMDetail(MDetail originalMDetail);
}
