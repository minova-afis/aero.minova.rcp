
package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IMinovaPluginService;
import aero.minova.rcp.form.model.xsd.Procedure;
import aero.minova.rcp.model.form.MButton;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.helper.IMinovaWizard;
import aero.minova.rcp.model.util.ErrorObject;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.widgets.MinovaWizardDialog;

public class DynamicButtonHandler {

	@Inject
	protected TranslationService translationService;

	@Inject
	protected IMinovaPluginService pluginService;

	@Inject
	IEventBroker broker;

	ILog logger = Platform.getLog(this.getClass());

	@Execute
	public void execute(IEclipseContext context, Shell shell, @Optional @Named(Constants.CLAZZ) String className,
			@Optional @Named(Constants.PARAMETER) String parameter, MPart mPart) {

		if (Constants.WIZARD.equals(className)) {
			try {
				IMinovaWizard wizard = findWizard(parameter);
				if (wizard != null) {
					MDetail detail = ((WFCDetailPart) mPart.getObject()).getDetail();

					ContextInjectionFactory.inject(wizard, context);
					wizard.setOriginalMDetail(detail);

					MinovaWizardDialog wizardDialog = new MinovaWizardDialog(shell, wizard);
					wizardDialog.setTranslationService(translationService);
					wizardDialog.open();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				broker.post(Constants.BROKER_SHOWERROR, new ErrorObject("@msg.CantOpenWizard", e));
			}
		} else if (Constants.PROCEDURE.equals(className)) {
			Procedure p = (Procedure) context.get(parameter);
			((WFCDetailPart) mPart.getObject()).getRequestUtil().callProcedure(p);
		} else {
			// Helper Logik....

			MDetail detail = ((WFCDetailPart) mPart.getObject()).getDetail();

			MButton button = detail.getButton(parameter);
			for (SelectionListener listener : button.getButtonAccessor().getSelectionListener()) {
				listener.widgetSelected(null);
			}
		}
	}

	@CanExecute
	public boolean canExecute(MHandledItem item, MPart mPart) {
		String parameter = item.getPersistedState().get(Constants.CONTROL_ID);
		MDetail detail = ((WFCDetailPart) mPart.getObject()).getDetail();
		MButton button = detail.getButton(parameter);
		return button.getButtonAccessor().isEnabled();
	}

	private IMinovaWizard findWizard(String wizardName) {
		IMinovaWizard iWizard = null;

		pluginService.activatePlugin(wizardName);
		BundleContext bundleContext = FrameworkUtil.getBundle(WFCDetailPart.class).getBundleContext();
		try {
			ServiceReference<?>[] allServiceReferences = bundleContext.getAllServiceReferences(IMinovaWizard.class.getName(), null);
			for (ServiceReference<?> serviceReference : allServiceReferences) {
				String property = (String) serviceReference.getProperty("component.name");
				if (property.equals(wizardName)) {
					iWizard = (IMinovaWizard) bundleContext.getService(serviceReference);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		if (iWizard == null) {
			broker.post(Constants.BROKER_SHOWERRORMESSAGE, "@msg.WizardNotFound");
		}

		return iWizard;
	}

}