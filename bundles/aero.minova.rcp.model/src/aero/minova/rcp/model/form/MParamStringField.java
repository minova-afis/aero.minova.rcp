package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.List;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.DataType;

public class MParamStringField extends MField {

	protected MParamStringField() {
		super(DataType.STRING);
		this.subMFields = new ArrayList<>();
		this.setSubFields(new ArrayList<>());
	}

	private List<MField> subMFields;
	private List<Field> subFields;

	public List<MField> getSubMFields() {
		return subMFields;
	}

	public void setSubfields(List<MField> subMFields) {
		this.subMFields = subMFields;
	}

	public void addSubMField(MField f) {
		subMFields.add(f);
	}

	public List<Field> getSubFields() {
		return subFields;
	}

	public void setSubFields(List<Field> subFields) {
		this.subFields = subFields;
	}

	public void clearSubMFields() {
		subMFields.clear();
	}

}
