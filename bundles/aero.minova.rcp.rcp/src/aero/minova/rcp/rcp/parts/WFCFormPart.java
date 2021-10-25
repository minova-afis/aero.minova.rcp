package aero.minova.rcp.rcp.parts;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.IMinovaPluginService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

public abstract class WFCFormPart {

	@Inject
	protected MPerspective mPerspective;
	@Inject
	protected IDataFormService dataFormService;
	@Inject
	protected IDataService dataService;

	@Inject
	protected IMinovaPluginService pluginService;
	protected Form form;

	public Form getForm() {
		IEclipseContext ctx = mPerspective.getContext();
//		form = ctx.get(Form.class);
		if (form == null) {
			// TODO herausfinden wo das gesetzt wird und dokumentieren
			String formName = mPerspective.getPersistedState().get(E4WorkbenchParameterConstants.FORM_NAME);

			form = dataFormService.getForm(formName);
			// Form in den Context injected, damit überall darauf zugegriffen werden kann
			ctx.set(Form.class, form);
		}

		return form;
	}

}