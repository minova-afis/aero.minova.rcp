
package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.helper.IMinovaWizard;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.widgets.MinovaWizardDialog;

public class DynamicButtonHandler {

	@Inject
	protected TranslationService translationService;

	@Inject
	@Optional
	IMinovaWizard wizard;

	@Execute
	public void execute(IEclipseContext context, Shell shell, @Optional @Named(Constants.CONTROL_WIZARD) String className, MPart part) {
		try {

			MDetail detail = ((WFCDetailPart) part.getObject()).getDetail();

			ContextInjectionFactory.inject(wizard, context);
			wizard.setOriginalMDetail(detail);

			MinovaWizardDialog wizardDialog = new MinovaWizardDialog(shell, wizard);
			wizardDialog.setTranslationService(translationService);
			wizardDialog.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}