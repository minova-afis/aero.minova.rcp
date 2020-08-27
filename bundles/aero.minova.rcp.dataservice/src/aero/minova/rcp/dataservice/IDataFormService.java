package aero.minova.rcp.dataservice;

import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.plugin1.model.Table;

public interface IDataFormService {
	
	Form getForm();

	Table getTableFromFormIndex(Form form);

}
