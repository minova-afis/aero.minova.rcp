package aero.minova.rcp.rcp.parts;

import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

public abstract class WFCFormPart {

	@Inject
	protected MPerspective perspective;
	@Inject
	protected IDataFormService dataFormService;
	@Inject
	protected IDataService dataService;
	protected Form form;
	
	@Inject
	Logger logger;

	public WFCFormPart() {
		super();
	}

	public Form getForm(Composite parent) {
		form = perspective.getContext().get(Form.class);
		if (form == null) {
			String formName = perspective.getPersistedState().get(E4WorkbenchParameterConstants.FORM_NAME);
			dataService.getFileSynch(formName); // Datei ggf. vom Server holen
			form = dataFormService.getForm(formName);
			if (form == null) {
				LabelFactory.newLabel(SWT.CENTER).align(SWT.CENTER).text(formName).create(parent);
				logger.error("Server konnte " + formName + " nicht laden!");
				return null;
			}
		}
		perspective.getContext().set(Form.class, form); // Wir merken es uns im Context; so können andere es nutzen
		return form;
	}

}