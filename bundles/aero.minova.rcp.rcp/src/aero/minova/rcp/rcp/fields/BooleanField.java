package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.BooleanValueAccessor;
import aero.minova.rcp.rcp.util.Constants;

public class BooleanField {

	public static Control create(Composite composite, MField field, int row, int column, FormToolkit formToolkit) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		FormData formData = new FormData();
		Button button = formToolkit.createButton(composite, field.getLabel(), SWT.CHECK);

		field.setValueAccessor(new BooleanValueAccessor(field, button));

		formData.width = COLUMN_WIDTH;
		formData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		formData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);

		button.setData(TRANSLATE_PROPERTY, labelText);
		button.setLayoutData(formData);
		button.setData(Constants.CONTROL_FIELD, field);
		button.setData(Constants.CONTROL_DATATYPE, DataType.BOOLEAN);

		return button;
	}

}
