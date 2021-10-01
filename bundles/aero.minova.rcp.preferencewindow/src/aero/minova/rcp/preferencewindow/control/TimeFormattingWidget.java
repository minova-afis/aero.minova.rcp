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

import aero.minova.rcp.util.TimeUtil;

public class TimeFormattingWidget extends CustomPWWidget {

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
	public TimeFormattingWidget(final String label, final String propertyKey, final TranslationService translationService, Locale locale) {
		super(label, propertyKey, 2, false);
		this.translationService = translationService;
		this.locale = locale;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);

		if (getLabel() == null) {
			throw new UnsupportedOperationException("Test");
		} else {
			label.setText(getLabel());
		}
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
		text.setMessage(DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, FormatStyle.SHORT, Chronology.ofLocale(locale), locale));
		text.setText(PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey()).toString());
		text.setToolTipText("H: " + translationService.translate("@Preferences.TimeUtilPattern.24Hour", null) + "\nh: "
				+ translationService.translate("@Preferences.TimeUtilPattern.12Hour", null) + "\nm: "
				+ translationService.translate("@Preferences.TimeUtilPattern.Minute", null) + "\na: AM/PM");
		final GridData textGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		textGridData.widthHint = 185;
		text.setLayoutData(textGridData);

		Label example = new Label(cmp, SWT.NONE);
		addControl(example);
		final GridData exampleGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		example.setLayoutData(exampleGridData);
		example.setText(getTimeStringFromPattern(text.getText()));

		text.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), text.getText());
			example.setText(getTimeStringFromPattern(text.getText()));
		});

		return text;
	}

	private String getTimeStringFromPattern(String pattern) {
		if (validatePattern(pattern) || pattern.isBlank()) {
			try {
				LocalDateTime time = LocalDateTime.of(2000, 01, 01, 23, 45);
				String formatted = TimeUtil.getTimeString(time.toInstant(ZoneOffset.UTC), locale, pattern);
				return formatted;
			} catch (Exception e) {
				return "Invalid format!";
			}
		}
		return "Invalid format!";
	}

	private boolean validatePattern(String input) {
		Pattern pattern = Pattern.compile("([hH]{0,2})([\\:/\\s]{0,1})([m]{0,2})([\\s]?)([a]?)");
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
		final Object value = PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), Boolean.valueOf(false));
		} else {
			if (!(value instanceof String)) {
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey() + "' has to be a String because it is associated to a Text");
			}
		}
	}

}
