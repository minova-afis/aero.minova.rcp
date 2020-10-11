package aero.minova.rcp.translate.lifecycle;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;

import aero.minova.rcp.translate.service.WFCTranslationService;

public class Manager {

	@PostContextCreate
	public void postContextCreate(IEclipseContext context) {
		checkTranslationService(context);
	}

	private void checkTranslationService(IEclipseContext context) {
		Object o = context.get(TranslationService.class);
		if (o.getClass().getName().equals("org.eclipse.e4.core.internal.services.BundleTranslationProvider")) {
			WFCTranslationService translationService  = ContextInjectionFactory.make(WFCTranslationService.class, context);
			translationService.setTranslationService((TranslationService) o);
			context.set(TranslationService.class, translationService);
			context.set("aero.minova.rcp.applicationid", "WFC");
			context.set("aero.minova.rcp.customerid", "MIN");
		}
	}
}
