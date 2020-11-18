package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_DECIMALS;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_MAX_VALUE;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_MIN_VALUE;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.NUMBER_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.DataType;

public class NumberField {

	private NumberField() {
	}

	public static Control create(Composite composite, Field field, int row, int column, FormToolkit formToolkit,
			Locale locale) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		String unitText = field.getUnitText() == null ? "" : field.getUnitText();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		Text text = formToolkit.createText(composite, "", SWT.BORDER | SWT.RIGHT);
		FieldUtil.addDataToText(text, field, DataType.DOUBLE);
		FieldUtil.addConsumer(text, field);
		Label unit = formToolkit.createLabel(composite, unitText, SWT.LEFT);
		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();
		FormData unitFormData = new FormData();

		labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(text, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		textFormData.width = NUMBER_WIDTH;

		unitFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		unitFormData.left = new FormAttachment(text, 0, SWT.RIGHT);
		unitFormData.width = COLUMN_WIDTH - NUMBER_WIDTH;

		label.setData(TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		Integer decimals = field.getNumber().getDecimals();
		decimals = decimals == null ? 0 : decimals;
		Float maximum = field.getNumber().getMaxValue();
		maximum = maximum == null ? Float.MAX_VALUE : maximum;
		Float minimum = field.getNumber().getMinValue();
		minimum = minimum == null ? Float.MIN_VALUE : maximum;
		text.setData(TRANSLATE_LOCALE, locale);
		text.setData(FIELD_DECIMALS, decimals);
		text.setData(FIELD_MAX_VALUE, maximum);
		text.setData(FIELD_MIN_VALUE, minimum);
		text.setLayoutData(textFormData);
		NumberFieldUtil.setMessage(text);

		unit.setData(TRANSLATE_PROPERTY, unitText);
		unit.setLayoutData(unitFormData);

		return text;
	}

}
