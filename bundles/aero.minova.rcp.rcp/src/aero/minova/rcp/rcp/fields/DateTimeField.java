package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
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
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.css.ICssStyler;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.rcp.accessor.DateTimeValueAccessor;
import aero.minova.rcp.util.DateTimeUtil;

public class DateTimeField {

	public static Control create(Composite composite, MField field, int row, int column, Locale locale, String timezone, MPerspective perspective,
			TranslationService translationService) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		String dateUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.DATE_UTIL, DisplayType.DATE_UTIL, "", locale);
		String timeUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.TIME_UTIL, DisplayType.TIME_UTIL, "", locale);
		Label label = FieldLabel.create(composite, field);

		TextAssistContentProvider contentProvider = new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				ArrayList<String> result = new ArrayList<>();
				Instant date = DateTimeUtil.getDateTime(Instant.now(), entry, locale);
				if (date == null && !entry.isEmpty()) {
					result.add(translationService.translate("@msg.ErrorConverting", null));
				} else {
					result.add(DateTimeUtil.getDateTimeString(date, locale, dateUtil, timeUtil));
					field.setValue(new Value(date), true);
				}
				return result;
			}

		};

		TextAssist text = new TextAssist(composite, SWT.BORDER, contentProvider);
		LocalDateTime of = LocalDateTime.of(LocalDate.of(2020, 12, 12), LocalTime.of(22, 55));
		text.setMessage(DateTimeUtil.getDateTimeString(of.toInstant(ZoneOffset.UTC), locale, dateUtil, timeUtil));
		text.setNumberOfLines(1);
		text.setData(TRANSLATE_LOCALE, locale);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (text.getText().isBlank()) {
					field.setValue(null, true);
				}
			}
		});
		text.setData(Constants.CONTROL_FIELD, field);

		// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
		IEclipseContext context = perspective.getContext();
		DateTimeValueAccessor valueAccessor = new DateTimeValueAccessor(field, text);
		ContextInjectionFactory.inject(valueAccessor, context);
		field.setValueAccessor(valueAccessor);

		FormData fd = new FormData();
		fd.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		fd.left = new FormAttachment(column == 0 ? 25 : 75);
		fd.right = new FormAttachment(column == 0 ? 50 : 100, column == 0 ? -ICssStyler.CSS_SECTION_SPACING : -MARGIN_BORDER);
		text.setLayoutData(fd);
		text.setData(CssData.CSSDATA_KEY,
				new CssData(CssType.DATE_TIME_FIELD, column + 1, row, field.getNumberColumnsSpanned(), field.getNumberRowsSpanned(), false));

		FieldLabel.layout(label, text, row, column, field.getNumberRowsSpanned());

		return text;
	}

}
