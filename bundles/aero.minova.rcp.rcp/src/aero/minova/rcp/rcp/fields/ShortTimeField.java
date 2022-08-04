package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TEXT_WIDTH;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.rcp.accessor.ShortTimeValueAccessor;
import aero.minova.rcp.util.OSUtil;
import aero.minova.rcp.util.TimeUtil;

public class ShortTimeField {

	private ShortTimeField() {
		throw new IllegalStateException("Utility class");
	}

	public static Control create(Composite composite, MField field, int row, int column, Locale locale, String timezone, MPerspective perspective,
			TranslationService translationService) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		String timeUtil = (String) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.TIME_UTIL, DisplayType.TIME_UTIL, "", locale);

		Label label = FieldLabel.create(composite, field);

		TextAssistContentProvider contentProvider = new TextAssistContentProvider() {
			@Override
			public List<String> getContent(String entry) {
				ArrayList<String> result = new ArrayList<>();
				Instant time = TimeUtil.getTime(entry, timeUtil, locale);
				if (time == null && !entry.isEmpty()) {
					result.add(translationService.translate("@msg.ErrorConverting", null));
				} else {
					result.add(TimeUtil.getTimeString(time, locale, timeUtil));
					field.setValue(new Value(time), true);
				}
				return result;
			}

		};
		Control text;
		LocalDateTime date = LocalDateTime.of(LocalDate.of(2000, 01, 01), LocalTime.of(11, 59));
		if (OSUtil.isLinux()) {
			Text text2 = new Text(composite, SWT.BORDER);
			text = text2;
			ToolTip tooltip = new ToolTip(text2.getShell(), SWT.ICON_INFORMATION);
			text2.setMessage(TimeUtil.getTimeString(date.toInstant(ZoneOffset.UTC), locale, timeUtil));
			text2.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					text2.selectAll();
					tooltip.setAutoHide(false);
				}

				@Override
				public void focusLost(FocusEvent e) {
					tooltip.setAutoHide(true);
				}
			});
			text2.addModifyListener(e -> {
				try {
					if (!tooltip.getAutoHide()) {
						List<String> values = contentProvider.getContent(((Text) e.widget).getText());
						if (!values.isEmpty()) {
							tooltip.setText(values.get(0));
							tooltip.setVisible(true);
						}
					} else {
						tooltip.setText("");
					}
				} catch (NullPointerException ex) {}
			});
		} else {
			TextAssist text2 = new TextAssist(composite, SWT.BORDER, contentProvider);
			text = text2;
			text2.setMessage(TimeUtil.getTimeString(date.toInstant(ZoneOffset.UTC), locale, timeUtil));
			text2.setNumberOfLines(1);
			text2.setData(TRANSLATE_LOCALE, locale);
			text2.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					text2.selectAll();
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (text2.getText().isBlank()) {
						field.setValue(null, true);
					}
				}
			});
		}
		text.setData(Constants.CONTROL_FIELD, field);
		text.setData(TRANSLATE_LOCALE, locale);

		// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
		IEclipseContext context = perspective.getContext();
		ShortTimeValueAccessor valueAccessor = new ShortTimeValueAccessor(field, text);
		ContextInjectionFactory.inject(valueAccessor, context);
		field.setValueAccessor(valueAccessor);

		FieldLabel.layout(label, text, row, column, field.getNumberRowsSpanned());

		FormData fd = new FormData();
		fd.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		fd.left = new FormAttachment((column == 0) ? 25 : 75);
		fd.width = TEXT_WIDTH;
		text.setLayoutData(fd);
		text.setData(CssData.CSSDATA_KEY, new CssData(CssType.TIME_FIELD, column + 1, row, 1, 1, false));
		return text;
	}
}
