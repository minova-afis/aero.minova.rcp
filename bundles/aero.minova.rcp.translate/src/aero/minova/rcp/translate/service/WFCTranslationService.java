package aero.minova.rcp.translate.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.nls.ILocaleChangeService;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import aero.minova.rcp.dataservice.IDataService;

public class WFCTranslationService extends TranslationService {

	private TranslationService parent;

	@Inject
	IEventBroker eventBroker;

	@Inject
	private IDataService dataService;

	Logger logger;

	@Inject
	IEclipseContext context;

	@Inject
	@Named(TranslationService.LOCALE)
	Locale locale;

	private String customerId = "MIN";
	private String applicationId = "SIS";
	private Properties resources = new Properties();

	volatile boolean updateRequired = true;

	@Inject
	@Optional
	private void localChanged(@Named(TranslationService.LOCALE) Locale s) {
		if (logger != null) {
			logger.info("Locale changed to: " + s.getDisplayName(Locale.ENGLISH));
		}
		updateRequired = true;
	}

	@Inject
	@Optional
	private void downloadedFileChange(@UIEventTopic("i18ndownload") String string) {
		updateRequired = true;
		Locale realLocal = this.locale;
		Locale neededToTriggerChange = new Locale("de");
		ILocaleChangeService localeChangeService = context.get(ILocaleChangeService.class);
		localeChangeService.changeApplicationLocale(neededToTriggerChange);
		localeChangeService.changeApplicationLocale(realLocal);

	}

	@Override
	public String translate(String key, String contributorURI) {
		if (updateRequired) {
			synchronized (this) {
				loadResources();
				updateRequired = false;
			}
		}
		try {
			if (key.startsWith("%")) {
				if (parent != null) {
					return parent.translate(key, contributorURI);
				} else {
					return key;
				}
			}
		} catch (NullPointerException e) {
			// Weitermachen
		}

		return translate(key);
	}

	/**
	 * einen MINOVA String übersetzen
	 *
	 * @param key
	 * @return
	 */
	private String translate(String key) {
		if (key.startsWith("@") && !key.startsWith("@@")) {
			key = key.substring(1);
			String value = resources.getProperty(key + "." + applicationId + "." + customerId);
			if (value != null) {
				return translate(value);
			}
			value = resources.getProperty(key + "." + customerId);
			if (value != null) {
				return translate(value);
			}
			value = resources.getProperty(key + "." + applicationId);
			if (value != null) {
				return translate(value);
			}
			value = resources.getProperty(key);
			return value == null ? key : translate(value);
		}
		return key;
	}

	@Inject
	@Optional
	void setLoggerFactory(LoggerFactory factory) {
		if (factory != null) {
			this.logger = factory.getLogger(getClass());
		} else {
			this.logger = null;
		}
	}

	@Inject
	@Optional
	void setApplicationId(@Named("aero.minova.rcp.applicationid") String id) {
		this.applicationId = id;
	}

	@Inject
	@Optional
	void setCustomerId(@Named("aero.minova.rcp.customerid") String id) {
		this.customerId = id;
	}

	/**
	 * Wird verwendet, um den existierenden platform translation service zu cachen und für bestimmte String zu verwenden
	 *
	 * @param bundleTranslationProvider
	 */

	public void setTranslationService(TranslationService bundleTranslationProvider) {
		this.parent = bundleTranslationProvider;
	}

	/**
	 * Resource Bundle Search and Loading Strategy
	 * <p>
	 * getBundle uses the base name, the specified locale, and the default locale (obtained from Locale.getDefault) to generate a sequence of candidate bundle
	 * names. If the specified locale's language, script, country, and variant are all empty strings, then the base name is the only candidate bundle name.
	 * Otherwise, a list of candidate locales is generated from the attribute values of the specified locale (language, script, country and variant) and
	 * appended to the base name. Typically, this will look like the following:
	 * </p>
	 * baseName + "_" + language + "_" + script + "_" + country + "_" + variant <br>
	 * baseName + "_" + language + "_" + script + "_" + country <br>
	 * baseName + "_" + language + "_" + script <br>
	 * baseName + "_" + language + "_" + country + "_" + variant <br>
	 * baseName + "_" + language + "_" + country <br>
	 * baseName + "_" + language
	 *
	 * @throws URISyntaxException
	 */
	private void loadResources() {
		String filename;

		String i18nPath = "i18n";
		filename = i18nPath + "/messages";
		resources = new Properties();

		load(filename);

		// construct file name to load
		if (locale == null) {
			return;
		}
		if (!isEmpty(locale.getLanguage())) {
			filename += "_" + locale.getLanguage();
			load(filename);
			if (!isEmpty(locale.getCountry())) {
				filename += "_" + locale.getCountry();
				load(filename);
				if (!isEmpty(locale.getVariant())) {
					filename += "_" + locale.getVariant();
					load(filename);
				}
			}
			filename = "i18n/messages" + "+" + locale.getDisplayLanguage();
			if (!isEmpty(locale.getScript())) {
				filename += "_" + locale.getScript();
				load(filename);
				if (!isEmpty(locale.getCountry())) {
					filename += "_" + locale.getCountry();
					load(filename);
					if (!isEmpty(locale.getVariant())) {
						filename += "_" + locale.getVariant();
						load(filename);
					}
				}
			}
		}
	}

	private void load(String filename) {
		File file = dataService.getStoragePath().resolve(filename + ".properties").toFile();
		if (file.exists()) {
			try (BufferedInputStream targetStream = new BufferedInputStream(new FileInputStream(file))) {
				resources.load(targetStream);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

	private boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}
}
