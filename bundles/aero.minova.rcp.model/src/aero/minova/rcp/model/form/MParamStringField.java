package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.util.ParamStringUtil;

public class MParamStringField extends MField implements ValueChangeListener {

	protected MParamStringField(Locale locale) {
		super(DataType.STRING);
		this.subMFields = new ArrayList<>();
		this.subFields = new ArrayList<>();
		this.locale = locale;
	}

	private List<MField> subMFields;
	private List<Field> subFields;
	private Locale locale;

	public List<MField> getSubMFields() {
		return subMFields;
	}

	public void setSubMFields(List<MField> subMFields) {
		this.subMFields = subMFields;

		for (MField f : subMFields) {
			f.addValueChangeListener(this);
		}
	}

	public void addSubMField(MField f) {
		subMFields.add(f);
		f.addValueChangeListener(this);
	}

	public void clearSubMFields() {
		subMFields.clear();
	}

	public List<Field> getSubFields() {
		return subFields;
	}

	public void setSubFields(List<Field> subFields) {
		this.subFields = subFields;
	}

	@Override
	public void setValue(Value value, boolean user) {
		super.setValue(value, user);

		// Alle Unterfelder leeren
		if (value == null || value.getStringValue() == null) {
			for (MField subMField : subMFields) {
				subMField.setValue(null, user);
			}
			return;
		}

		// String parsen
		List<Value> values = ParamStringUtil.convertStringParameterToValues(value.getStringValue(), locale);

		// Unterfelder füllen
		for (int i = 0; i < subMFields.size(); i++) {
			System.out.println("Setting " + subMFields.get(i).getLabel() + " " + values.get(i));
			subMFields.get(i).setValue(values.get(i), false);
		}
	}

	@Override
	public Value getValue() {

		List<Value> values = new ArrayList<>();

		for (MField subMField : subMFields) {
			values.add(subMField.getValue());
		}

		String paramString = ParamStringUtil.convertValuesToStringParameter(values, locale);

		return new Value(paramString);
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		if (evt.isUser()) {
			this.setValue(getValue(), false);
		}

	}

}
