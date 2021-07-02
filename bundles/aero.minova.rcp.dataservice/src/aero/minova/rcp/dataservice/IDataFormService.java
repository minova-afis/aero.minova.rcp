package aero.minova.rcp.dataservice;

import java.util.List;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.model.Table;

public interface IDataFormService {

	Form getForm(String name);

	Table getTableFromFormIndex(Form form);

	Table getTableFromFormDetail(Form form, String prefix);

	Table getTableFromGrid(Grid grid);

	List<Field> getFieldsFromForm(Form form);

	List<Field> getAllPrimaryFieldsFromForm(Form form);

}
