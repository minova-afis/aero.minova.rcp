package aero.minova.rcp.rcp.widgets;

import javax.inject.Inject;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.wizard.Wizard;

import aero.minova.rcp.model.form.MDetail;

public abstract class AbstractWizard extends Wizard {

	protected MDetail mdetail;

	@Inject
	protected MPerspective mPerspective;

	@Inject
	protected TranslationService translationService;


	public void setMDetail(MDetail mDetail) {
		this.mdetail = mDetail;
	}

	public void setMPerspective(MPerspective mPerspective) {
		this.mPerspective = mPerspective;
	}

	public void setTranslationService(TranslationService translationService) {
		this.translationService = translationService;

	}
}
