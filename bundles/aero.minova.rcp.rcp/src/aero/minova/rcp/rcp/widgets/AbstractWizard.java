package aero.minova.rcp.rcp.widgets;

import org.eclipse.jface.wizard.Wizard;

import aero.minova.rcp.model.form.MDetail;

public abstract class AbstractWizard extends Wizard {

	protected MDetail originalMDetail;

	public void setOriginalMDetail(MDetail originalMDetail) {
		this.originalMDetail = originalMDetail;
	}
}
