
package aero.minova.rcp.rcp.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.widgets.AbstractWizard;

public class DynamicButtonHandler {

	@Execute
	public void execute(IEclipseContext context, Shell shell, @Optional @Named(Constants.CONTROL_WIZARD) String className, MPart part) {
		try {

			MDetail detail = ((WFCDetailPart) part.getObject()).getDetail();

			Class<?> wizardClass = Class.forName(className);
			Object wizardObject = ContextInjectionFactory.make(wizardClass, context);
			((AbstractWizard) wizardObject).setOriginalMDetail(detail);

			WizardDialog wizardDialog = new WizardDialog(shell, (Wizard) wizardObject);
			wizardDialog.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}