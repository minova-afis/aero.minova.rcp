package aero.minova.rcp.model.helper;

import org.eclipse.jface.wizard.IWizard;

import aero.minova.rcp.model.form.MDetail;

public interface IMinovaWizard extends IWizard {

	void setOriginalMDetail(MDetail originalMDetail);

	MDetail getOriginalMDetail();
}
