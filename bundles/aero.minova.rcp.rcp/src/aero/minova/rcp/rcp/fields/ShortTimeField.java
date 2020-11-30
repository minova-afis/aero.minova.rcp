package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_VALUE;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.SHORT_TIME_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.time.Instant;
import java.time.LocalTime;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.ValueAccessor;
import aero.minova.rcp.rcp.parts.ShortTimeValueAccessor;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.DateTimeUtil;
import aero.minova.rcp.rcp.util.TimeUtil;

public class ShortTimeField {

	public static Control create(Composite composite, MField field, int row, int column, FormToolkit formToolkit, Locale locale, String timezone) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		TextAssist text = new TextAssist(composite, SWT.BORDER, new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				return null;
			}
		});
		TextAssistContentProvider contentProvider = new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				ArrayList<String> result = new ArrayList<>();
				Instant time = TimeUtil.getTime(entry, timezone);
				if (time == null && !entry.isEmpty()) {
					result.add("!Error converting");
					((ValueAccessor) text.getData(Constants.VALUE_ACCESSOR)).setValue(null, true);
				} else {
					LocalTime localTime = LocalTime.ofInstant(time, ZoneId.of(timezone));
					result.add(localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)));
					((ValueAccessor) text.getData(Constants.VALUE_ACCESSOR)).setValue(new Value(time), true);
				}
				return result;
			}

		};
		text.setContentProvider(contentProvider);
		FieldUtil.addDataToText(text, field, DataType.INSTANT);
		text.setData(Constants.VALUE_ACCESSOR, new ShortTimeValueAccessor(field, text));

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
		text.setNumberOfLines(1);
		text.setLayoutData(textFormData);
		text.setData(TRANSLATE_LOCALE, locale);
		text.setData(Constants.CONTROL_CONSUMER, (Consumer<Table>) t -> {
			Instant date = t.getRows().get(0).getValue(t.getColumnIndex(field.getName())).getInstantValue();
			updateValue(text, date, timezone);
		});
		text.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				String input = text.getText();
				updateValue(text, DateTimeUtil.getDate(input, locale), timezone);
			}

			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}
		});

		return text;
	}

	public static void updateValue(TextAssist text, Instant time, String timezone) {
		if (time == null) return;
		text.setData(FIELD_VALUE, time);
		// Hier auch die Preferences beachten
		LocalTime localTime = LocalTime.ofInstant(time, ZoneId.of(timezone));
		Locale locale = (Locale) text.getData(TRANSLATE_LOCALE);
		// Bei der Formatierung geschehen fehler, wir erhalten das Milienium zurück
//		text.setText(localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(locale)));
		// text.setMessage(localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)));
	}

}
