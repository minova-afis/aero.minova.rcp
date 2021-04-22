package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.DATE_TIME_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
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
import aero.minova.rcp.rcp.accessor.DateTimeValueAccessor;
import aero.minova.rcp.util.DateTimeUtil;

public class DateTimeField {

	public static Control create(Composite composite, MField field, int row, int column, Locale locale, String timezone,
			MPerspective perspective) {

		String labelText = field.getLabel() == null ? "" : field.getLabel();

		Label label = LabelFactory.newLabel(SWT.RIGHT).text(labelText).create(composite);
		label.setData(TRANSLATE_PROPERTY, labelText);

		TextAssistContentProvider contentProvider = new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				ArrayList<String> result = new ArrayList<>();
				Instant date = DateTimeUtil.getDateTime(Instant.now(), entry, locale);
				if (date == null && !entry.isEmpty()) {
					result.add("!Error converting");
				} else {
					result.add(DateTimeUtil.getDateTimeString(date, locale));
					field.setValue(new Value(date), true);
				}
				return result;
			}

		};

		TextAssist text = new TextAssist(composite, SWT.BORDER, contentProvider);
		LocalDateTime of = LocalDateTime.of(LocalDate.of(2020, 12, 12), LocalTime.of(22, 55));
		Instant ofEpochSecond = Instant.ofEpochSecond(of.toEpochSecond(ZoneOffset.UTC), of.getNano());
		text.setMessage(DateTimeUtil.getDateTimeString(ofEpochSecond, locale));
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
		DateTimeValueAccessor valueAccessor = new DateTimeValueAccessor(field, text);
		ContextInjectionFactory.inject(valueAccessor, context);
		field.setValueAccessor(valueAccessor);

		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();

		labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(text, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		textFormData.width = DATE_TIME_WIDTH;

		label.setLayoutData(labelFormData);
		text.setLayoutData(textFormData);

		return text;
	}

}
