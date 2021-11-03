package aero.minova.rcp.model.form;

import java.util.Locale;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.TypeParam;
import aero.minova.rcp.model.KeyType;

public class ModelToViewModel {

	public static MField convert(Field field, Locale locale) {
		MField f = initializeModelField(field, locale);

		f.setName(field.getName());
		f.setLabel(field.getLabel());
		f.setUnitText(field.getUnitText());
		f.setSqlIndex(field.getSqlIndex().intValue());
		f.setOriginalRequired(field.isRequired());
		f.setOriginalReadOnly(field.isReadOnly());
		if (field.getNumberColumnsSpanned() != null) {
			f.setNumberColumnsSpanned(field.getNumberColumnsSpanned().intValue());
		}
		if (field.getEditor() != null) { // Contact Editor braucht immer 4 Spalten
			f.setNumberColumnsSpanned(4);
		}
		if (field.getNumberRowsSpanned() != null) {
			f.setNumberRowsSpanned(Integer.parseInt(field.getNumberRowsSpanned()));
		}
		if (field.getTabIndex() != null) {
			f.setTabIndex(field.getTabIndex().intValue());
		}

		f.setPrimary(KeyType.PRIMARY.toString().equalsIgnoreCase(field.getKeyType()));

		return f;
	}

	private static MField initializeModelField(Field field, Locale locale) {
		if (field.getBoolean() != null) {
			return new MBooleanField();
		}

		if (field.getDateTime() != null) {
			return new MDateTimeField();
		}

		if (field.getLookup() != null) {
			MField f = new MLookupField();
			f.setLookupTable(field.getLookup().getTable());
			f.setLookupProcedurePrefix(field.getLookup().getProcedurePrefix());
			f.setLookupDescription(field.getLookup().getDescriptionName());
			for (TypeParam typeParam : field.getLookup().getParam()) {
				f.addLookupParameter(typeParam.getFieldName());
			}
			return f;
		}

		if (field.getEditor() != null) {
			MField f = new MLookupField();
			f.setLookupTable("tContact");
			f.setLookupDescription("LastName");
			return f;
		}

		if (field.getNumber() != null) {
			MField f = new MNumberField(field.getNumber().getDecimals());
			if (field.getNumber().getMaxValue() != null) {
				f.setMaximumValue(field.getNumber().getMaxValue().doubleValue());
			}
			if (field.getNumber().getMinValue() != null) {
				f.setMinimumValue(field.getNumber().getMinValue().doubleValue());
			}
			return f;
		}

		if (field.getMoney() != null) {
			MField f = new MNumberField(field.getMoney().getDecimals());
			if (field.getMoney().getMaxValue() != null) {
				f.setMaximumValue(field.getMoney().getMaxValue().doubleValue());
			}
			if (field.getMoney().getMinValue() != null) {
				f.setMinimumValue(field.getMoney().getMinValue().doubleValue());
			}
			return f;
		}

		if (field.getPercentage() != null) {
			return new MNumberField(field.getPercentage().getDecimals());
		}

		if (field.getShortDate() != null) {
			return new MShortDateField();
		}

		if (field.getShortTime() != null) {
			return new MShortTimeField();
		}

		if (field.getText() != null) {
			MField f = new MTextField();
			f.setFillToRight("toright".equals(field.getFill()));
			f.setMaxTextLength(field.getText().getLength());
			return f;
		}

		if (field.getParamString() != null) {
			MParamStringField f = new MParamStringField(locale);
			f.setSubFields(field.getParamString().getField());
			return f;
		}

		throw new RuntimeException("Typ of field " + field.getName() + " cannot  be determined");
	}

}
