package aero.minova.rcp.preferencewindow.control;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.regex.Pattern;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.util.DateUtil;

public class DateFormattingWidget extends CustomPWWidget {

	TranslationService translationService;
	Locale locale;

	/**
	 * Constructor
	 *
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated key
	 */
	public DateFormattingWidget(final String label, final String tooltip, final String propertyKey, final TranslationService translationService,
			Locale locale) {
		super(label, tooltip, propertyKey, 2, false);
		this.translationService = translationService;
		this.locale = locale;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(getLabel());
		label.setToolTipText("d: " + translationService.translate("@Preferences.DateUtilPattern.Day", null) + "\nM: "
				+ translationService.translate("@Preferences.DateUtilPattern.Month", null) + "\ny/u: "
				+ translationService.translate("@Preferences.DateUtilPattern.Year", null));
		addControl(label);
		final GridData labelGridData = new GridData(SWT.END, SWT.CENTER, false, false);
		labelGridData.horizontalIndent = getIndent();
		label.setLayoutData(labelGridData);

		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new GridLayout(2, false));
		final GridData cmpGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		cmp.setLayoutData(cmpGridData);
		addControl(cmp);

		final Text text = new Text(cmp, SWT.BORDER);
		addControl(text);
		text.setMessage(DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.MEDIUM, null, Chronology.ofLocale(locale), locale));
		text.setText(PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey()).toString());
		text.setToolTipText("d: " + translationService.translate("@Preferences.DateUtilPattern.Day", null) + "\nM: "
				+ translationService.translate("@Preferences.DateUtilPattern.Month", null) + "\ny/u: "
				+ translationService.translate("@Preferences.DateUtilPattern.Year", null));
		final GridData textGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		textGridData.widthHint = 185;
		text.setLayoutData(textGridData);

		Label example = new Label(cmp, SWT.NONE);
		addControl(example);
		final GridData exampleGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		example.setLayoutData(exampleGridData);
		example.setText(getDateStringFromPattern(text.getText()));

		text.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), text.getText());
			example.setText(getDateStringFromPattern(text.getText()));
		});

		return text;
	}

	private String getDateStringFromPattern(String pattern) {
		if (validatePattern(pattern) || pattern.isBlank()) {
			try {
				LocalDateTime date = LocalDateTime.of(2015, 12, 24, 23, 45);
				String formatted = DateUtil.getDateString(date.toInstant(ZoneOffset.UTC), locale, pattern);
				return formatted;
			} catch (Exception e) {
				return "Invalid format!";
			}
		}
		return "Invalid format!";
	}

	private boolean validatePattern(String input) {
		Pattern pattern = Pattern.compile("([dMyu]{0,4})([\\.,/\\s]{0,1})([dM]{0,3})([\\.,/\\s]{0,1})([dMyu]{0,4})");
		if (pattern.matcher(input).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		// not needed
	}

}
