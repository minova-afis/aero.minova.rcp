package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.Locale;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Radio;
import aero.minova.rcp.form.model.xsd.TypeParam;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.KeyType;

public class ModelToViewModel {

	private ModelToViewModel() {}

	public static MField convert(Field field, Locale locale) {
		MField f = initializeModelField(field, locale);

		f.setName(field.getName());
		f.setLabel(field.getLabel());
		f.setUnitText(field.getUnitText());
		f.setSqlIndex(field.getSqlIndex().intValue());
		f.setOriginalRequired(field.isRequired());
		f.setOriginalReadOnly(field.isReadOnly());
		f.setOriginalVisible(field.isVisible());
		if (field.getNumberColumnsSpanned() != null) {
			f.setNumberColumnsSpanned(field.getNumberColumnsSpanned().intValue());
		}
		if (field.getNumberRowsSpanned() != null) {
			f.setNumberRowsSpanned(Integer.parseInt(field.getNumberRowsSpanned()));
		}
		if (f instanceof MRadioField) {
			// Anzahl Knöpfe/3
			f.setNumberRowsSpanned((int) Math.ceil((double) ((MRadioField) f).getRadiobuttons().size() / 3));
			f.setNumberColumnsSpanned(4);
		}
		if (f instanceof MParamStringField) {
			// Param-String Felder werden nicht angezeigt
			f.setNumberRowsSpanned(0);
		}
		if (f instanceof MPeriodField) {
			// Felder mit Periode benötigen immer 4 Spalten
			f.setNumberColumnsSpanned(4);
		}
		if (field.getTabIndex() != null) {
			f.setTabIndex(field.getTabIndex().intValue());
		}
		if (field.getLabelText() != null) {
			f.setLabelText(true);
		}

		f.setPrimary(KeyType.PRIMARY.toString().equalsIgnoreCase(field.getKeyType()));
		f.setKeyTypeUser(KeyType.USER.toString().equalsIgnoreCase(field.getKeyType()));
		if (f.isKeyTypeUser()) { // Felder mit keyType="user" sind immer Pflichtfelder
			f.setOriginalRequired(true);
		}

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
			f.setUseResolveParms(field.getLookup().isUseResolveParams());
			f.setLookupDescription(field.getLookup().getDescriptionName());
			for (TypeParam typeParam : field.getLookup().getParam()) {
				f.addLookupParameter(typeParam.getFieldName());
			}
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
			MField f = new MNumberField(field.getMoney().getDecimals(), DataType.BIGDECIMAL);
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
			f.setFillHorizontal("horizontal".equals(field.getFill()));
			f.setMaxTextLength(field.getText().getLength());
			return f;
		}

		if (field.getLabelText() != null) {
			MField f = new MLabelText();
			f.setFillToRight("toright".equals(field.getFill()));
			f.setFillHorizontal("horizontal".equals(field.getFill()));
			return f;
		}

		if (field.getRadiobox() != null) {
			MRadioField mRadioField = new MRadioField();
			ArrayList<MBooleanField> radiobuttons = new ArrayList<>();
			for (Radio r : field.getRadiobox().getRadio()) {
				MBooleanField b = new MBooleanField();
				b.setLabel(r.getLabel());
				b.setName(r.getName());
				radiobuttons.add(b);
			}
			mRadioField.setRadiobuttons(radiobuttons);
			return mRadioField;
		}

		if (field.getParamString() != null) {
			MParamStringField f = new MParamStringField(locale);
			f.setSubFields(field.getParamString().getField());
			return f;
		}

		if (field.getPeriod() != null) {
			return new MPeriodField();
		}

		if (field.getEditor() != null) {
			throw new RuntimeException("Editor (Field " + field.getName() + ") no longer supported. Please convert to Lookup!");
		}

		throw new RuntimeException("Typ of field " + field.getName() + " cannot  be determined");
	}

}
