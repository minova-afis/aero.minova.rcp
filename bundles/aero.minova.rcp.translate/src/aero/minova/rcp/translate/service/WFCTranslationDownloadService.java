package aero.minova.rcp.translate.service;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.IDummyService;
import aero.minova.rcp.dataservice.ZipService;

@Component(immediate = true)
public class WFCTranslationDownloadService {

	private IDataService dataService;
	private TranslationService translationService;
	private EventAdmin admin;

	@Reference
	void getDummyService(IDummyService translationService) {
		// this method prevents the immediate component to be actived before the
		// workspace location in the dataservice has been set
		// maybe move that to a service property dependency?
	}

	@Reference
	void getDataService(IDataService dataService) {
		this.dataService = dataService;
	}

	@Reference
	void registerEventAdmin(EventAdmin admin) {
		this.admin = admin;
	}

	void unregisterEventAdmin(EventAdmin admin) {
		this.admin = null;
	}

	public void postEvent() {
		Dictionary<String, Object> data = new Hashtable<>(2);
		data.put(EventConstants.EVENT_TOPIC, "i18ndownload");
		data.put(IEventBroker.DATA, "i18ndownload");
		Event event = new Event("i18ndownload", data);
		admin.postEvent(event);
	}

	@Activate
	public void handleDataService() {
		Objects.requireNonNull(dataService);


		CompletableFuture.runAsync(() -> {
			try {
				boolean checkIfUpdateIsRequired = dataService.checkIfUpdateIsRequired("i18n.zip");
				if (checkIfUpdateIsRequired) {
					dataService.downloadFile("i18n.zip");
					ZipService.unzipFile(dataService.getStoragePath().resolve("i18n.zip").toFile(),
							dataService.getStoragePath().toString());
					postEvent();
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		});

	}

}
