package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_VALUE;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.SHORT_TIME_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.TEXT_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.nebula.widgets.opal.textassist.TextAssistContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.TimeUtil;

public class WFCDetailFieldUtil {

	public static Control createBooleanField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		FormData formData = new FormData();
		Button button = formToolkit.createButton(composite, field.getLabel(), SWT.CHECK);

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
		String labelText = field.getLabel() == null ? "" : field.getLabel();

		FormData formData = new FormData();
		formData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		formData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		formData.width = COLUMN_WIDTH;

		Button button = formToolkit.createButton(composite, field.getLabel(), SWT.CHECK);
		button.setData(TRANSLATE_PROPERTY, labelText);
		button.setLayoutData(formData);
		button.setData(Constants.CONTROL_FIELD, field);
		button.setData(Constants.CONTROL_DATATYPE, DataType.INSTANT);
		FieldUtil.addConsumer(button, field);

		return button;
	}

	public static Control createShortTimeField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit, Locale locale) {

		TextAssistContentProvider contentProvider = new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				ArrayList<String> result = new ArrayList<>();
				Instant date = TimeUtil.getTime(entry, "UTC");
				if (date == null && !entry.isEmpty()) {
					result.add("!Error converting");
				} else {
					LocalDateTime localDate = LocalDateTime.ofInstant(date, ZoneId.of("UTC"));
					result.add(localDate.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)));
				}
				return result;
			}

		};
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		TextAssist text = new TextAssist(composite, SWT.BORDER, contentProvider);
		// Text text = formToolkit.createText(composite, "", SWT.BORDER);
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
		text.setData(TRANSLATE_LOCALE, locale);
		label.setLayoutData(labelFormData);

		text.setMessage("23:59");
		text.setLayoutData(textFormData);

		text.setData(Constants.CONTROL_CONSUMER, (Consumer<Table>) t -> {
			Instant date = t.getRows().get(0).getValue(t.getColumnIndex(field.getName())).getInstantValue();
			updateValue(text, date);
		});

		text.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				String input = text.getText();
				updateValue(text, TimeUtil.getTime(input, "UTC"));
			}

			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}
		});

		return text;
	}

	public static void updateValue(TextAssist text, Instant date) {
		if (date != null) {
			text.setData(FIELD_VALUE, date);
			// Hier auch die Preferences beachten
			LocalDateTime localDate = LocalDateTime.ofInstant(date, ZoneId.of("UTC"));
			Locale locale = (Locale) text.getData(TRANSLATE_LOCALE);
			// Bei der Formatierung geschehen fehler, wir erhalten das Milienium zurück
			text.setText(localDate.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)));
		} else {
			text.setText("");
		}
	}
	public static Control createTextField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		Text text = formToolkit.createText(composite, "",
				SWT.BORDER | (getExtraHeight(field) > 0 ? SWT.MULTI : SWT.NONE));
		FieldUtil.addDataToText(text, field, DataType.STRING);
		FieldUtil.addConsumer(text, field);
		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();

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
			labelFormData.top = new FormAttachment(text, 0, SWT.TOP);
		} else {
			labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
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
