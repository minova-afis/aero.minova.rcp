
package aero.minova.rcp.rcp.parts;

import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.PostConstruct;

import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

public class WFCDetailPart {

	@Inject
	private IDataFormService dataFormService;

	private Form form;

	public WFCDetailPart() {

	}

	@Inject
	@Named(E4WorkbenchParameterConstants.FORM_NAME)
	String formName;

	@PostConstruct
	public void postConstruct(Composite parent) {
		form = dataFormService.getForm(formName);
		if (form == null) {
			LabelFactory.newLabel(SWT.CENTER).align(SWT.CENTER).text(formName).create(parent);
			return;
		}
		
		
	}

}