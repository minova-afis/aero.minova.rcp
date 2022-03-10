package aero.minova.rcp.rcp.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.opal.textassist.TextAssistContentProvider;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.util.DateTimeUtil;
import aero.minova.rcp.util.DateUtil;
import aero.minova.rcp.util.TimeUtil;

public class TextAssistUtil {

	private TextAssistUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static TextAssistContentProvider getTimeTextAssistProvider(MField field, TranslationService translationService, Locale locale, String timePattern) {
		return getTextAssistProvider(field, translationService, locale, null, timePattern);
	}

	public static TextAssistContentProvider getDateTextAssistProvider(MField field, TranslationService translationService, Locale locale, String datePattern) {
		return getTextAssistProvider(field, translationService, locale, datePattern, null);
	}

	public static TextAssistContentProvider getTextAssistProvider(MField field, TranslationService translationService, Locale locale, String datePattern,
			String timePattern) {

		TextAssistContentProvider contentProvider = new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				ArrayList<String> result = new ArrayList<>();
				Instant date = getInstantForEntry(Instant.now(), entry, locale, datePattern, timePattern);
				if (date == null && !entry.isEmpty()) {
					result.add(translationService.translate("@msg.ErrorConverting", null));
				} else {
					result.add(getStringForInstant(date, locale, datePattern, timePattern));
					field.setValue(new Value(date), true);
				}
				return result;
			}

		};
		return contentProvider;
	}

	private static String getStringForInstant(Instant date, Locale locale, String datePattern, String timePattern) {
		if (timePattern == null) {
			return DateUtil.getDateString(date, locale, datePattern);
		} else if (datePattern == null) {
			return TimeUtil.getTimeString(date, locale, timePattern);
		} else {
			return DateTimeUtil.getDateTimeString(date, locale, datePattern, timePattern);
		}
	}

	private static Instant getInstantForEntry(Instant now, String entry, Locale locale, String datePattern, String timePattern) {
		if (timePattern == null) {
			return DateUtil.getDate(now, entry);
		} else if (datePattern == null) {
			return TimeUtil.getTime(now, entry);
		} else {
			return DateTimeUtil.getDateTime(now, entry, locale);
		}
	}
}
