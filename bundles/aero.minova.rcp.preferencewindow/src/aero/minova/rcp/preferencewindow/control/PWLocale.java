package aero.minova.rcp.preferencewindow.control;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.nls.ILocaleChangeService;
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

	private final List<String> dataL = CustomLocale.getLanguages();
	private Combo comboCountries;
	private Combo comboLanguage;

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

		comboLanguage = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		addControl(comboLanguage);

		// Setzt die Text auf den in den Preferences gespeicherten Wert
		for (int i = 0; i < dataL.size(); i++) {
			final Object language = dataL.get(i);
			comboLanguage.add(language.toString());
			if (language.equals(PreferenceWindow.getInstance().getValueFor("language"))) {
				comboLanguage.select(i);
			}

		}

		comboLanguage.addListener(SWT.Modify, event -> {
			// erneuert Liste mit Ländern
			updateLocale();
			if (!PreferenceWindow.getInstance().getValueFor("language")
					.equals(InstancePreferenceAccessor.getValue(preferences, "language", DisplayType.COMBO)))
				comboCountries.removeAll();
			for (String country : CustomLocale.getCountries()) {
				comboCountries.add(country);
			}
			comboCountries.select(0);
		});

		// Label für Landauswahl erstellen
		final Label countryLabel = new Label(parent, SWT.NONE);
		countryLabel.setText("Land");
		final GridData labelCGridData = new GridData(GridData.END, getAlignment(), false, false);
		labelCGridData.horizontalIndent = getIndent();
		countryLabel.setLayoutData(labelCGridData);
		addControl(countryLabel);

		// Combo Box für Landauswahl erstellen
		comboCountries = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		addControl(comboCountries);

		// Setzt die Text auf den in den Preferences gespeicherten Wert
		for (int i = 0; i < dataC.size(); i++) {
			final Object country = dataC.get(i);
			comboCountries.add(country.toString());
			if (country.equals(PreferenceWindow.getInstance().getValueFor("land"))) {
				comboCountries.select(i);
			} else {
				comboCountries.select(0);
			}
		}

		// Erneuert den gespeicherten Wert in der Data des Preference Windows und fügt
		// den geänderten Wert den Preferences hinzu
//		comboCountries.addListener(SWT.Modify, event -> {
//			updateLocale();
//		});

		return comboLanguage;
	}

	private void updateLocale() {
		PreferenceWindow.getInstance().setValue("language", PWLocale.this.dataL.get(comboLanguage.getSelectionIndex()));
		PreferenceWindow.getInstance().setValue("land", PWLocale.this.dataC.get(comboCountries.getSelectionIndex()));

	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		// Alle Prüfungen auf Null oder einen leeren String werden in anderen Methode
		// direkt beim erstellen der Listen gemacht
	}
}