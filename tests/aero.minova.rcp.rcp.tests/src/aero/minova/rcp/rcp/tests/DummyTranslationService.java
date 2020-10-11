package aero.minova.rcp.rcp.tests;

import org.eclipse.e4.core.services.translation.TranslationService;

public class DummyTranslationService extends TranslationService {

	@Override
	public String translate(String key, String contributorURI) {
		return key;
	}
}
