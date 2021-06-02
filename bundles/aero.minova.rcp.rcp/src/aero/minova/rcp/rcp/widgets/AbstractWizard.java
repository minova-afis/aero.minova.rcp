package aero.minova.rcp.rcp.widgets;

import javax.inject.Inject;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.wizard.Wizard;

import aero.minova.rcp.dataservice.IDataService;

public abstract class AbstractWizard extends Wizard {

	@Inject
	protected MPerspective mPerspective;

	@Inject
	protected TranslationService translationService;

	@Inject
	protected MPart mPart;

	@Inject
	protected IDataService dataService;

	public void setMPerspective(MPerspective mPerspective) {
		this.mPerspective = mPerspective;
	}

	public void setTranslationService(TranslationService translationService) {
		this.translationService = translationService;

	}
}
