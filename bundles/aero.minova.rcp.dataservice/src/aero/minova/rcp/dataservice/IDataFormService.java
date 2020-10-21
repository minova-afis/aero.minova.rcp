package aero.minova.rcp.dataservice;

import java.util.List;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Table;

public interface IDataFormService {

	Form getForm();
	
	Form getForm(String name);

	Table getTableFromFormIndex(Form form);

	Table getTableFromFormDetail(Form form, String prefix);

	List<Field> getFieldsFromForm(Form form, Boolean insert);

}
