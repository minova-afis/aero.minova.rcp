package aero.minova.rcp.preferencewindow.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;

public class PWLocale extends CustomPWWidget {
	Preferences preferences = InstanceScope.INSTANCE.getNode("aero.minova.rcp.preferencewindow");

	private Map<String, Object> data;
	private final List<String> dataL = CustomLocale.getLanguages();
	private final boolean editable;

	private Combo comboCountries;

	/**
	 * Constructor
	 *
	 * @param languageLabel associated label
	 * @param propertyKey   associated key
	 */
	public PWLocale(final String languageLabel, String countryLabel, final String propertyKey, Map<String, Object> data,
			final Object... values) {
		this(languageLabel, propertyKey, false, data, values);
	}

	/**
	 * Constructor
	 *
	 * @param label       associated label
	 * @param propertyKey associated key
	 */
	public PWLocale(final String label, final String propertyKey, final boolean editable, Map<String, Object> data,
			final Object... values) {
		super(label, propertyKey, label == null ? 1 : 2, false);
		this.editable = editable;
	}

	public List<String> getCountriesByData() {
		Locale locales[] = CustomLocale.getLocales();
		data = PreferenceWindow.getInstance().getValues();
		List<String> countries = new ArrayList<>();
		for (Locale locale : locales) {
			if (!locale.getDisplayCountry().equals("") && !countries.contains(locale.getDisplayCountry())
					&& data.get(getCustomPropertyKey()).toString().equals(locale.getDisplayLanguage()))
				countries.add(locale.getDisplayCountry());
		}
		Collections.sort(countries);
		return countries;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		final Label languageLabel = new Label(parent, SWT.NONE);
		languageLabel.setText("Sprache");
		final GridData labelLGridData = new GridData(GridData.END, getAlignment(), false, false);
		labelLGridData.horizontalIndent = getIndent();
		languageLabel.setLayoutData(labelLGridData);
		addControl(languageLabel);

		final Combo comboLanguage = new Combo(parent, SWT.BORDER | (editable ? SWT.NONE : SWT.READ_ONLY));
		addControl(comboLanguage);

		for (int i = 0; i < dataL.size(); i++) {
			final Object datum = dataL.get(i);
			comboLanguage.add(datum.toString());
			if (datum.equals(PreferenceWindow.getInstance().getValueFor("language"))) {
				comboLanguage.select(i);
			}
		}

		comboLanguage.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(),
					PWLocale.this.dataL.get(comboLanguage.getSelectionIndex()));
			comboCountries.removeAll();
			for (String string : getCountriesByData()) {
				comboCountries.add(string);
				if (string.equals(PreferenceWindow.getInstance().getValueFor("land"))) {
					comboCountries.select(0);
				}
			}
			InstancePreferenceAccessor.putValue(preferences, "language", DisplayType.LOCALE,
					PWLocale.this.getCountriesByData().get(comboLanguage.getSelectionIndex()));

		});

		final Label countryLabel = new Label(parent, SWT.NONE);
		countryLabel.setText("Land");
		final GridData labelCGridData = new GridData(GridData.END, getAlignment(), false, false);
		labelCGridData.horizontalIndent = getIndent();
		countryLabel.setLayoutData(labelCGridData);
		addControl(countryLabel);

		comboCountries = new Combo(parent, SWT.BORDER | (editable ? SWT.NONE : SWT.READ_ONLY));
		addControl(comboCountries);

		for (int i = 0; i < getCountriesByData().size(); i++) {
			final Object datum = getCountriesByData().get(i);
			comboCountries.add(datum.toString());
			if (datum.equals(PreferenceWindow.getInstance().getValueFor("land"))) {
				comboCountries.select(i);
			}
		}

		comboCountries.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue("land",
					PWLocale.this.getCountriesByData().get(comboCountries.getSelectionIndex()));
			InstancePreferenceAccessor.putValue(preferences, "land", DisplayType.LOCALE,
					PWLocale.this.getCountriesByData().get(comboCountries.getSelectionIndex()));
		});

		return comboLanguage;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), null);
		} else {
			if (editable && !(value instanceof String)) {
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey()
						+ "' has to be a String because it is associated to an editable combo");
			}

			if (!getCountriesByData().isEmpty()) {
				if (!value.getClass().equals(getCountriesByData().get(0).getClass())) {
					throw new UnsupportedOperationException("The property '" + getCustomPropertyKey() + "' has to be a "
							+ getCountriesByData().get(0).getClass() + " because it is associated to a combo");
				}
			}

		}
	}
}