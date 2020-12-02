package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TEXT_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.TextValueAccessor;

public class TextField {

	public static Control create(Composite composite, MField field, int row, int column, FormToolkit formToolkit) {

		String labelText = field.getLabel() == null ? "" : field.getLabel();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		label.setData(TRANSLATE_PROPERTY, labelText);

		int style = SWT.BORDER;
		if (field.getNumberRowsSpanned() > 1) {
			// Anwender hat mehrzeilige Eingabe definiert
			style |= SWT.MULTI;
		}
		Text text = formToolkit.createText(composite, "", style);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}
		});

		field.setValueAccessor(new TextValueAccessor(field, text));

		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();

		labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(text, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		if (field.getNumberColumnsSpanned() != null && field.getNumberColumnsSpanned().intValue() > 2
				&& field.isFillToRight()) {
			textFormData.width = COLUMN_WIDTH * 3 + MARGIN_LEFT * 2 + MARGIN_BORDER;
		} else {
			textFormData.width = TEXT_WIDTH;
		}
		if (field.getNumberRowsSpanned() > 1) {
			textFormData.height = COLUMN_HEIGHT * field.getNumberRowsSpanned() - MARGIN_TOP;
		}

		label.setLayoutData(labelFormData);

		text.setLayoutData(textFormData);

		return text;
	}
}
