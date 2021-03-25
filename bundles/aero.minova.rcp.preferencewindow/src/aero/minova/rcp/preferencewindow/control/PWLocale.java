package aero.minova.rcp.preferencewindow.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;

/**
 * Erstellt zwei ComboBoxen und eine Liste mit allen Ländern einer ausgewählten Sprache. Die Länderauswahl reagiert auf die ausgewählte Sprache. Die Sprache
 * wird zuerst ausgewählt, da es intuitiver ist und der Nutzer es von anderen Anwendungen/ Geräten gewöhnt ist.
 * 
 * @author bauer
 */
public class PWLocale extends CustomPWWidget {
	Preferences preferences = InstanceScope.INSTANCE.getNode("aero.minova.rcp.preferencewindow");

	private final List<String> dataL;
	private Combo comboCountries;
	private Combo comboLanguage;
	private IEclipseContext context;

	TranslationService translationService;

	/**
	 * Constructor
	 *
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated key
	 */
	public PWLocale(final String label, final String propertyKey, IEclipseContext context, TranslationService translationService) {
		super(label, propertyKey, label == null ? 1 : 2, false);
		this.context = context;
		this.translationService = translationService;
		Locale l = context.get(Locale.class);
		if (l == null) l = Locale.getDefault();
		dataL = CustomLocale.getLanguages(l);
	}

	/**
	 * Liefert eine Liste alle Länder für die ausgewählte Sprache zurück.
	 * 
	 * @return
	 */
	public List<String> getCountries() {
		List<String> countries = new ArrayList<>();
		String language = PreferenceWindow.getInstance().getValueFor(ApplicationPreferences.LOCALE_LANGUAGE).toString();
		Locale[] locales = CustomLocale.getLocales();
		for (Locale l : locales) {
			if (language.equals(l.getDisplayLanguage(l))) {
				if (!l.getDisplayCountry(l).equals("") && !countries.contains(l.getDisplayCountry(l))) {
					countries.add(l.getDisplayCountry(l));
				}
			}
		}
		Collections.sort(countries);
		return countries;
	}

	/**
	 * Erstellt zwei Combo Boxen. Die erste liefert alle mögliche Sprachen wieder. Die zweite liefert eine Liste von Ländern wieder, die die vorher ausgewählte
	 * Sprache sprechen.
	 * 
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		// Label für Sprachauswahl erstellen
		final Label languageLabel = new Label(parent, SWT.NONE);
		languageLabel.setText(translationService.translate("@Preferences.General.LocalLanguage", null));
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
			if (language.equals(InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.LOCALE_LANGUAGE, DisplayType.LOCALE,
					Locale.getDefault().getDisplayLanguage(Locale.getDefault()), context.get(Locale.class)))) {
				comboLanguage.select(i);
			}

		}

		comboLanguage.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(ApplicationPreferences.LOCALE_LANGUAGE, PWLocale.this.dataL.get(comboLanguage.getSelectionIndex()));
			// erneuert Liste mit Ländern
			comboCountries.removeAll();
			for (String country : getCountries()) {
				comboCountries.add(country);
			}
			comboCountries.select(0);

		});

		// Label für Landauswahl erstellen
		final Label countryLabel = new Label(parent, SWT.NONE);
		countryLabel.setText(translationService.translate("@tContact.Country", null));
		final GridData labelCGridData = new GridData(GridData.END, getAlignment(), false, false);
		labelCGridData.horizontalIndent = getIndent();
		countryLabel.setLayoutData(labelCGridData);
		addControl(countryLabel);

		// Combo Box für Landauswahl erstellen
		comboCountries = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		GridData countryData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		comboCountries.setLayoutData(countryData);
		addControl(comboCountries);

		// Setzt die Text auf den in den Preferences gespeicherten Wert
		for (int i = 0; i < getCountries().size(); i++) {
			final Object country = getCountries().get(i);
			comboCountries.add(country.toString());
			if (country.equals(InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.COUNTRY, DisplayType.LOCALE,
					Locale.getDefault().getDisplayCountry(Locale.getDefault()), context.get(Locale.class)))) {
				comboCountries.select(i);
			}
		}

		comboCountries.addListener(SWT.Modify, event -> {
			if (comboCountries.getItems().length == 0) {
				return;
			}

			if (!comboCountries.getItem(comboCountries.getSelectionIndex()).isEmpty())
				PreferenceWindow.getInstance().setValue(ApplicationPreferences.COUNTRY, comboCountries.getItem(comboCountries.getSelectionIndex()));
		});

		return comboLanguage;
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