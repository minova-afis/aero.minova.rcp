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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.LabelFactory;
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

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.ShortDateValueAccessor;
import aero.minova.rcp.util.DateUtil;

public class ShortDateField {

	private ShortDateField() {
		throw new IllegalStateException("Utility class");
	}


public static Control create(Composite composite, MField field, int row, int column, Locale locale, String timezone,
			MPerspective perspective) {

		String labelText = field.getLabel() == null ? "" : field.getLabel();
		Label label = LabelFactory.newLabel(SWT.RIGHT).text(labelText).create(composite);
		label.setData(TRANSLATE_PROPERTY, labelText);

		TextAssistContentProvider contentProvider = new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				ArrayList<String> result = new ArrayList<>();
				Instant date = DateUtil.getDate(entry, locale);
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

		// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
		IEclipseContext context = perspective.getContext();
		ShortDateValueAccessor valueAccessor = new ShortDateValueAccessor(field, text);
		ContextInjectionFactory.inject(valueAccessor, context);
		field.setValueAccessor(valueAccessor);

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
