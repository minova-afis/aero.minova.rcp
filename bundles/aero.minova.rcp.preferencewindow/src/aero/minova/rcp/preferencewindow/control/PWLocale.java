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

public class PWLocale extends CustomPWWidget {
	Preferences preferences = InstanceScope.INSTANCE.getNode("aero.minova.rcp.preferencewindow");

	@Inject
	ILocaleChangeService lcs;

	private final List<String> dataL = CustomLocale.getLanguages();
	private Combo comboCountries;

	/**
	 * Constructor
	 *
	 * @param label       associated label
	 * @param propertyKey associated key
	 */
	public PWLocale(final String label, final String propertyKey) {
		super(label, propertyKey, label == null ? 1 : 2, false);
	}

	/**
	 * Erstellt eine Liste mit allen Ländern, die die ausgewählte Sprache, die in
	 * der Data des PreferenceWindows gespeichert ist, sprechen
	 * 
	 * @return
	 */
	public List<String> getCountriesByData() {
		Locale[] locales = CustomLocale.getLocales();
		Map<String, Object> data = PreferenceWindow.getInstance().getValues();
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
	 * Erstellt zwei Combo Boxen. Die erste liefert alle mögliche Sprachen wieder.
	 * Die zweite liefert eine Liste von Ländern wieder, die die vorher ausgewählte
	 * Sprache sprechen.
	 * 
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		// Label für Sprachauswahl erstellen
		final Label languageLabel = new Label(parent, SWT.NONE);
		languageLabel.setText("Sprache");
		final GridData labelLGridData = new GridData(GridData.END, getAlignment(), false, false);
		labelLGridData.horizontalIndent = getIndent();
		languageLabel.setLayoutData(labelLGridData);
		addControl(languageLabel);

		// Combo Box für Sprachauswahl erstellen
		final Combo comboLanguage = new Combo(parent, SWT.BORDER);
		addControl(comboLanguage);

		// Setzt die Text auf den in den Preferences gespeicherten Wert
		for (int i = 0; i < dataL.size(); i++) {
			final Object datum = dataL.get(i);
			comboLanguage.add(datum.toString());
			if (datum.equals(PreferenceWindow.getInstance().getValueFor("language"))) {
				comboLanguage.select(i);
			}
		}

		// ModifyListener zur Sprachauswahl hinzufügen, der die Liste mit allen Ländern
		// für die ausgewählte Sprache neu erstellt, sobald die Sprache gewechselt wird
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

		});

		// Label für Landauswahl erstellen
		final Label countryLabel = new Label(parent, SWT.NONE);
		countryLabel.setText("Land");
		final GridData labelCGridData = new GridData(GridData.END, getAlignment(), false, false);
		labelCGridData.horizontalIndent = getIndent();
		countryLabel.setLayoutData(labelCGridData);
		addControl(countryLabel);

		// Combo Box für Landauswahl erstellen
		comboCountries = new Combo(parent, SWT.BORDER);
		addControl(comboCountries);

		// Setzt die Text auf den in den Preferences gespeicherten Wert
		for (int i = 0; i < getCountriesByData().size(); i++) {
			final Object country = getCountriesByData().get(i);
			comboCountries.add(country.toString());
			if (country.equals(PreferenceWindow.getInstance().getValueFor("land"))) {
				comboCountries.select(i);
			}
		}

		// Erneuert den gespeicherten Wert in der Data des Preference Windows
		comboCountries.addListener(SWT.Modify, event -> 
			PreferenceWindow.getInstance().setValue("land",
					PWLocale.this.getCountriesByData().get(comboCountries.getSelectionIndex()))
		);

		return comboLanguage;
	}

	/**
	 * Prüft ob der Wert für den Key kein leerer String ist und oder den richtigen
	 * Datentyp hat
	 * 
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor("language");
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), null);
		} else {
			if (!getCountriesByData().isEmpty() && !value.getClass().equals(getCountriesByData().get(0).getClass())) {
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey() + "' has to be a "
						+ dataL.get(0).getClass() + " because it is associated to a combo");
			}

		}
	}
}