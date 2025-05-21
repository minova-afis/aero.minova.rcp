package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.util.ParamJsonUtil;
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
	private Value cacheValue;
	private boolean isJson = false;

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

		cacheValue = value;

		// Alle Unterfelder leeren
		if (value == null || value.getStringValue() == null || value.getStringValue().isBlank()) {
			for (MField subMField : subMFields) {
				subMField.setValue(null, user);
			}
			return;
		}

		if (!isJson) {
			// String parsen
			List<Value> values = ParamStringUtil.convertStringParameterToValues(value.getStringValue(), locale);

			// Unterfelder füllen
			try {
				for (int i = 0; i < subMFields.size(); i++) {
					subMFields.get(i).setValue(values.get(i), false);
				}
			} catch (IllegalArgumentException | IndexOutOfBoundsException e) {
				// Wenn gerade ein neuer Datensatz geladen wurde passen die Werte und Felder evtl nicht zusammen
			}
		} else {
			ParamJsonUtil.convertJsonParameterToValues(value.getStringValue(), subMFields);
		}
	}

	@Override
	public Value getValue() {

		if (!isJson) {
			List<Value> values = new ArrayList<>();

			for (MField subMField : subMFields) {
				values.add(subMField.getValue());
			}

			String paramString = ParamStringUtil.convertValuesToStringParameter(values, locale);

			return new Value(paramString);
		} else {
			return new Value(ParamJsonUtil.convertValuesToJson(subMFields));
		}
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		if (evt.isUser()) {
			this.setValue(getValue(), true);
		}
	}

	/**
	 * Liefert den zuletzt durch setValue() gesetzten Wert zurück (ohne die Unterfelder zu beachten)
	 * 
	 * @return
	 */
	public Value getCacheValue() {
		return cacheValue != null ? cacheValue : new Value("");
	}

	/**
	 * Liefert true, wenn alle Kinderfelder null Values haben -> Dieses Feld hat effektiv null als Value <br>
	 * Erklärung: MParamStringFelder geben als Value immer den Parameter String ({0-0-0}{1-0-0}{2-7-14}20220629020000...) zurück. Deshalb gibt es diese Methode,
	 * die überprüft ob alle Unterfelder null sind.
	 * 
	 * @return
	 */
	public boolean isNullValue() {
		for (MField subMField : subMFields) {
			if (subMField instanceof MBooleanField) { // Boolean Felder haben nie null Wert -> Prüfung auf false
				if (Boolean.TRUE.equals(subMField.getValue().getBooleanValue())) {
					return false;
				}
			} else if (subMField.getValue() != null) {
				return false;
			}
		}
		return true;
	}

	public boolean isJson() {
		return isJson;
	}

	public void setJson(boolean isJson) {
		this.isJson = isJson;
	}

}
