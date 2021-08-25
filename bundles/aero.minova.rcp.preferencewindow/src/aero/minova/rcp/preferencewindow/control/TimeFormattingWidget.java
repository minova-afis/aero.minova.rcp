package aero.minova.rcp.preferencewindow.control;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

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
		text.setText(PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey()).toString());
		final GridData textGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		textGridData.widthHint = 185;
		text.setLayoutData(textGridData);
		
		Label example = new Label(cmp, SWT.NONE);
		addControl(example);
		final GridData exampleGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		example.setLayoutData(exampleGridData);
		example.setText(getTimeStringFromPattern(text.getText()));;
		
		text.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), text.getText());
			example.setText(getTimeStringFromPattern(text.getText()));
		});

		return text;
	}
	
	private String getTimeStringFromPattern(String pattern) {
		try {
			LocalTime lt = LocalTime.of(12, 35, 54);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern, locale);
			String formatted = lt.format(dtf);
			return formatted;
		} catch (Exception e) {
			return "Invalid format!";
		}
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
				throw new UnsupportedOperationException(
						"The property '" + getCustomPropertyKey() + "' has to be a String because it is associated to a Text");
			}
		}
	}

}
