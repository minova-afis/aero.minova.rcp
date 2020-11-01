package aero.minova.rcp.rcp.fields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.DataType;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.SHORT_DATE_WIDTH;

import java.util.Locale;

public class ShortDateField {

	private ShortDateField() {
	}

	public static Control create(Composite composite, Field field, int row, int column, FormToolkit formToolkit,
			Locale locale) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
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
		textFormData.width = SHORT_DATE_WIDTH;

		label.setData(TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		final ToolTip tip = new ToolTip(composite.getShell(), SWT.NONE);
		tip.setMessage(
				"Here is a message for the user. When the message is too long it wraps. I should say something cool but nothing comes to my mind.");

		text.setMessage("01.01.2000");
		text.setLayoutData(textFormData);
		text.setData(TRANSLATE_LOCALE, locale);

		text.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent e) {
				System.out.println(e.text + " " + ((Text) e.widget).getText());
				if (",".equals(e.text)) {
					((Text) e.widget).setText("100,00");
					((Text) e.widget).setSelection(4);
					e.doit = false;
				}
				tip.setMessage("test" + ((Text) e.widget).getText());
			}
		});

		text.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				tip.setVisible(false);
			}

			@Override
			public void focusGained(FocusEvent e) {
				Text actionWidget = (Text) e.widget;
				Point loc = actionWidget.toDisplay(actionWidget.getLocation());
				Point size = actionWidget.getSize();
				System.out.println(loc);
				System.out.println(size);
//				Point loc = actionWidget.getLocation();
				tip.setLocation(loc.x, loc.y);
//				tip.setMessage(actionWidget.getText());
				tip.setText("TT");
				tip.setVisible(true);
				System.out.println("Integer: " + Integer.MAX_VALUE);
				System.out.println("Long: " + Long.MAX_VALUE);
			}
		});
		text.addListener(SWT.None, new Listener() {

			@Override
			public void handleEvent(Event event) {

			}
		});

		return text;
	}

}
