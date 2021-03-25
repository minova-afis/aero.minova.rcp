package aero.minova.rcp.translate.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.nls.ILocaleChangeService;
import org.eclipse.e4.core.services.translation.TranslationService;
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

	private String customerId = "MIN";
	private String applicationId = "WFC";
	private Properties resources = new Properties();

	@Inject
	@Optional
	private void getNotified1(@Named(TranslationService.LOCALE) Locale s) {
		if (logger != null) {
			logger.info("Locale changed to: " + s.getDisplayName(Locale.ENGLISH));
		}
		CompletableFuture.runAsync(this::loadResources);
	}

	@Override
	public String translate(String key, String contributorURI) {
		if (key.startsWith("%")) {
			if (parent != null) {
				return parent.translate(key, contributorURI);
			} else {
				return key;
			}
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
			if (value != null)
				return translate(value);
			value = resources.getProperty(key + "." + customerId);
			if (value != null)
				return translate(value);
			value = resources.getProperty(key + "." + applicationId);
			if (value != null)
				return translate(value);
			value = resources.getProperty(key);
			return value == null ? key : translate(value);
		} else {
			return key;
		}
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
	 * Wird verwendet, um den existierenden platform translation service zu cachen
	 * und für bestimmte String zu verwenden
	 * 
	 * @param bundleTranslationProvider
	 */

	public void setTranslationService(TranslationService bundleTranslationProvider) {
		this.parent = bundleTranslationProvider;
	}

	/**
	 * Resource Bundle Search and Loading Strategy
	 *
	 * <p>
	 * getBundle uses the base name, the specified locale, and the default locale
	 * (obtained from Locale.getDefault) to generate a sequence of candidate bundle
	 * names. If the specified locale's language, script, country, and variant are
	 * all empty strings, then the base name is the only candidate bundle name.
	 * Otherwise, a list of candidate locales is generated from the attribute values
	 * of the specified locale (language, script, country and variant) and appended
	 * to the base name. Typically, this will look like the following:
	 * </p>
	 *
	 * baseName + "_" + language + "_" + script + "_" + country + "_" + variant <br>
	 * baseName + "_" + language + "_" + script + "_" + country <br>
	 * baseName + "_" + language + "_" + script <br>
	 * baseName + "_" + language + "_" + country + "_" + variant <br>
	 * baseName + "_" + language + "_" + country <br>
	 * baseName + "_" + language
	 * 
	 * @throws URISyntaxException
	 *
	 */
	private void loadResources() {
		String filename;

		String i18nPath = "i18n";
		filename = i18nPath + "/messages";
		resources = new Properties();
		List<CompletableFuture<String>> list = new ArrayList<>();

		loadAndStore(filename, list);

		// construct file name to load
		if (locale == null) {
			return;
		}
		if (!isEmpty(locale.getLanguage())) {
			filename += "_" + locale.getLanguage();
			loadAndStore(filename, list);
			if (!isEmpty(locale.getCountry())) {
				filename += "_" + locale.getCountry();
				loadAndStore(filename, list);
				if (!isEmpty(locale.getVariant())) {
					filename += "_" + locale.getVariant();
					loadAndStore(filename, list);
				}
			}
			filename = "i18n/messages" + "+" + locale.getDisplayLanguage();
			if (!isEmpty(locale.getScript())) {
				filename += "_" + locale.getScript();
				loadAndStore(filename, list);
				if (!isEmpty(locale.getCountry())) {
					filename += "_" + locale.getCountry();
					loadAndStore(filename, list);
					if (!isEmpty(locale.getVariant())) {
						filename += "_" + locale.getVariant();
						loadAndStore(filename, list);
					}
				}
			}
		}
		// Sobald die Files heruntergeladen werden, triggered wir ein LocalChangeEvent
		// damit die neuen Übersetzungsfiles
		// verwendet werden, damit ein "CHANGE" propagiert wird, wird zunächst ein
		// localEvent verschickt mit "de" als local und dannach das
		// echte local
		CompletableFuture<Void> allFutures = CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
		allFutures.thenAccept((Void e) -> {
			Locale neededToTriggerChange = new Locale("de");

			// required to propagate a change in the next statement
			eventBroker.post(ILocaleChangeService.LOCALE_CHANGE, neededToTriggerChange); 
			eventBroker.post(ILocaleChangeService.LOCALE_CHANGE, locale);

		});
	}

	private void loadAndStore(String filename, List<CompletableFuture<String>> list) {
		CompletableFuture<String> hashedFile = dataService.getHashedFile(filename + ".properties");
		hashedFile.thenAccept(e -> loadResources(e));
		list.add(hashedFile);
	}

	private void loadResources(String content) {
		 InputStream targetStream = new ByteArrayInputStream(content.getBytes());
			try {
				resources.load(targetStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}
}
