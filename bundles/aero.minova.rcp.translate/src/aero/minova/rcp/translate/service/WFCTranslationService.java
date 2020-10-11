package aero.minova.rcp.translate.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

public class WFCTranslationService extends TranslationService {

	private TranslationService parent;

	Logger logger;

	private String customerId = "MIN";
	private String applicationId = "WFC";
	private Properties resources = new Properties();

	public WFCTranslationService() {
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
			return value == null ? key : value;
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

	@Inject
	@Optional
	void setLocale(Locale locale) {
		if (logger != null) {
			logger.info("Locale changed to: " + locale.getDisplayName(Locale.ENGLISH));
			logger.info(Platform.getInstallLocation().toString());
		}
	}

	public void setTranslationService(TranslationService o) {
		this.parent = o;

		if (logger != null) {
			logger.info("Locale changed to: " + locale.getDisplayName(Locale.ENGLISH));
			try {
				logger.info(Platform.getInstanceLocation().getURL().toURI().toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		loadResources();
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
		String baseFilename;
		try {
			baseFilename = Platform.getInstanceLocation().getURL().toURI().toString();
			baseFilename += "/i18n/messages";
			resources = new Properties();
			load(baseFilename + ".properties");
			if (locale.getCountry().equals(new Locale("DE").getCountry())) {
				// Deutschland
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void load(String string) {
		FileInputStream is = null;
		File f;
		try {
			f = new File(new URI(string));
			is = new FileInputStream(f);
			resources.load(is);
			logger.error("test");
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					if (logger != null) {
						logger.error(e.toString());
					} else {
						e.printStackTrace();
					}
				}
		}
	}
}
