package aero.minova.rcp.model.form;

import aero.minova.rcp.form.model.xsd.Color;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.TypeParam;

public class ModelToViewModel {

	public static MField convert(Field field) {
		MField f;
		if (field.getBoolean() != null) {
			f = new MBooleanField();
		} else if (field.getDateTime() != null) {
			f = new MDateTimeField();
		} else if (field.getLookup() != null) {
			f = new MLookupField();
			f.setLookupTable(field.getLookup().getTable());
			f.setLookupProcedurePrefix(field.getLookup().getProcedurePrefix());
			for (TypeParam typeParam : field.getLookup().getParam()) {
				f.addLookupParameter(typeParam.getFieldName());
			}
		} else if (field.getNumber() != null) {
			f = new MNumberField(field.getNumber().getDecimals());
			if (field.getNumber().getMaxValue() != null) {
				f.setMaximumValue(field.getNumber().getMaxValue().doubleValue());
			}
			if (field.getNumber().getMinValue() != null) {
				f.setMinimumValue(field.getNumber().getMinValue().doubleValue());
			}
		} else if (field.getShortDate() != null) {
			f = new MShortDateField();
		} else if (field.getShortTime() != null) {
			f = new MShortTimeField();
		} else if (field.getText() != null) {
			f = new MTextField();
			f.setFillToRight("toright".equals(field.getFill()));
		} else {
			return null;
		}

		f.setName(field.getName());
		f.setLabel(field.getLabel());
		f.setUnitText(field.getUnitText());
		f.setSqlIndex(field.getSqlIndex().intValue());
		f.setRequired(field.isRequired());
		f.setReadOnly(field.isReadOnly());
		if (field.getNumberColumnsSpanned() != null) {
			f.setNumberColumnsSpanned(field.getNumberColumnsSpanned().intValue());
		}
		if (field.getNumberRowsSpanned() != null) {
			f.setNumberRowsSpanned(Integer.parseInt(field.getNumberRowsSpanned()));
		}
		if (field.getTabIndex() != null) {
			f.setTabIndex(field.getTabIndex().intValue());
		}

		return f;
	}

}
