package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.NUMBER_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.SHORT_TIME_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.TEXT_WIDTH;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.rcp.util.Constants;

public class WFCDetailFieldUtil {

	public static Control createBooleanField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();
		FormData formData = new FormData();
		Button button = formToolkit.createButton(composite, field.getTextAttribute(), SWT.CHECK);

		formData.width = COLUMN_WIDTH;
		formData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		formData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);

		button.setData(TRANSLATE_PROPERTY, labelText);
		button.setLayoutData(formData);
		button.setData(Constants.CONTROL_FIELD, field);
		button.setData(Constants.CONTROL_DATATYPE, DataType.BOOLEAN);
		FieldUtil.addConsumer(button, field);

		return button;
	}

	public static Control createDateTimeField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();

		FormData formData = new FormData();
		formData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		formData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		formData.width = COLUMN_WIDTH;

		Button button = formToolkit.createButton(composite, field.getTextAttribute(), SWT.CHECK);
		button.setData(TRANSLATE_PROPERTY, labelText);
		button.setLayoutData(formData);
		button.setData(Constants.CONTROL_FIELD, field);
		button.setData(Constants.CONTROL_DATATYPE, DataType.INSTANT);
		FieldUtil.addConsumer(button, field);

		return button;
	}

	public static Control createShortTimeField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		Text text = formToolkit.createText(composite, "", SWT.BORDER);
		FieldUtil.addDataToText(text, field, DataType.INSTANT);
		FieldUtil.addConsumer(text, field);
		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();

		labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(text, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		textFormData.width = SHORT_TIME_WIDTH;

		label.setData(TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		text.setMessage("23:59");
		text.setLayoutData(textFormData);

		return text;
	}

	public static Control createTextField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		Text text = formToolkit.createText(composite, "",
				SWT.BORDER | (getExtraHeight(field) > 0 ? SWT.MULTI : SWT.NONE));
		FieldUtil.addDataToText(text, field, DataType.STRING);
		FieldUtil.addConsumer(text, field);
		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();

		labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(text, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		if (field.getNumberColumnsSpanned() != null && field.getNumberColumnsSpanned().intValue() > 2
				&& "toright".equals(field.getFill())) {
			textFormData.width = COLUMN_WIDTH * 3 + MARGIN_LEFT * 2 + MARGIN_BORDER;
		} else {
			textFormData.width = TEXT_WIDTH;
		}
		if (field.getNumberRowsSpanned() != null && field.getNumberRowsSpanned().length() > 0) {
			textFormData.height = COLUMN_HEIGHT * Integer.parseInt(field.getNumberRowsSpanned()) - MARGIN_TOP;
		}

		label.setData(TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		text.setLayoutData(textFormData);

		return text;
	}

	private static int getExtraHeight(Field field) {
		if (field.getNumberRowsSpanned() != null && field.getNumberRowsSpanned().length() > 0) {
			return Integer.parseInt(field.getNumberRowsSpanned()) - 1;
		}
		return 0;
	}

}
