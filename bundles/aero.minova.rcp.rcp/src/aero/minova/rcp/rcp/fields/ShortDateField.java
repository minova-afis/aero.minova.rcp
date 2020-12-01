package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.SHORT_DATE_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.nebula.widgets.opal.textassist.TextAssistContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.parts.ShortDateValueAccessor;
import aero.minova.rcp.rcp.util.DateTimeUtil;

public class ShortDateField {

	public static Control create(Composite composite, MField field, int row, int column, FormToolkit formToolkit, Locale locale, String timezone) {

		String labelText = field.getLabel() == null ? "" : field.getLabel();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		label.setData(TRANSLATE_PROPERTY, labelText);

		TextAssistContentProvider contentProvider = new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				ArrayList<String> result = new ArrayList<>();
				Instant date = DateTimeUtil.getDate(entry, locale);
				if (date == null && !entry.isEmpty()) {
					result.add("!Error converting");
				} else {
					LocalDate localDate = LocalDate.ofInstant(date, ZoneId.of("UTC"));
					result.add(localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)));
					field.setValue(new Value(date), true);
				}
				return result;
			}

		};
		TextAssist text = new TextAssist(composite, SWT.BORDER, contentProvider);
		text.setMessage(LocalDate.of(2000, 1, 1).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)));
		text.setNumberOfLines(1);
		text.setData(TRANSLATE_LOCALE, locale);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}
		});

		field.setValueAccessor(new ShortDateValueAccessor(field, text));

		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();

		labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(text, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		textFormData.width = SHORT_DATE_WIDTH;

		label.setLayoutData(labelFormData);
		text.setLayoutData(textFormData);

		return text;
	}
}
