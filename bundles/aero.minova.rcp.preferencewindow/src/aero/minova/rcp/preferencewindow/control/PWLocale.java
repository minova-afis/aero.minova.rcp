package aero.minova.rcp.preferencewindow.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.ComboHeightAdjust;
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
	private CCombo comboCountries;
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
	public PWLocale(final String label, @Optional String tooltip, final String propertyKey, IEclipseContext context, TranslationService translationService,
			IDataService dataService) {
		super(label, tooltip, propertyKey, label == null ? 1 : 2, false);
		this.context = context;
		this.translationService = translationService;
		Locale l = context.get(Locale.class);
		if (l == null) {
			l = Locale.getDefault();
		}
		dataL = CustomLocale.getLanguages(l, CustomLocale.getLanguageTagListOfi18n(dataService));
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
			if (language.equals(l.getDisplayLanguage(l)) && !l.getDisplayCountry(l).equals("") && !countries.contains(l.getDisplayCountry(l))) {
				countries.add(l.getDisplayCountry(l));
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
		languageLabel.setText(translationService.translate("@Preferences.LocalLanguage", null));
		final GridData labelLGridData = new GridData(GridData.END, getAlignment(), false, false);
		labelLGridData.horizontalIndent = getIndent();
		languageLabel.setLayoutData(labelLGridData);
		addControl(languageLabel);

		Composite cmpL = new Composite(parent, SWT.NONE);
		cmpL.setLayout(new GridLayout(2, false));
		final GridData cmpGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		cmpL.setLayoutData(cmpGridData);
		addControl(cmpL);

		CCombo comboLanguage = new CCombo(cmpL, SWT.BORDER | SWT.READ_ONLY);
		GridData languageData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		languageData.heightHint = ComboHeightAdjust.getComboHeight();
		comboLanguage.setLayoutData(languageData);
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
			PreferenceWindow.getInstance().setValue(ApplicationPreferences.LOCALE_LANGUAGE,
					PWLocale.this.dataL.get(comboLanguage.indexOf(comboLanguage.getText())));
			// erneuert Liste mit Ländern
			comboCountries.removeAll();
			for (String country : getCountries()) {
				comboCountries.add(country);
			}
			comboCountries.select(0);

		});

		new Label(cmpL, SWT.NONE);

		// Label für Landauswahl erstellen
		final Label countryLabel = new Label(parent, SWT.NONE);
		countryLabel.setText(translationService.translate("@Preferences.Country", null));
		final GridData labelCGridData = new GridData(GridData.END, getAlignment(), false, false);
		labelCGridData.horizontalIndent = getIndent();
		countryLabel.setLayoutData(labelCGridData);
		addControl(countryLabel);

		Composite cmpC = new Composite(parent, SWT.NONE);
		cmpC.setLayout(new GridLayout(2, false));
		final GridData cmpCGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		cmpC.setLayoutData(cmpCGridData);
		addControl(cmpC);

		// Combo Box für Landauswahl erstellen
		comboCountries = new CCombo(cmpC, SWT.BORDER | SWT.READ_ONLY);
		GridData countryData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		countryData.heightHint = ComboHeightAdjust.getComboHeight();
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
			if (comboCountries.getText() != null && !comboCountries.getText().isBlank()) {
				PreferenceWindow.getInstance().setValue(ApplicationPreferences.COUNTRY,
						comboCountries.getItem(comboCountries.indexOf(comboCountries.getText())));
			}
		});

		new Label(cmpC, SWT.NONE);

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