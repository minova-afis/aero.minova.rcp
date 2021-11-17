package aero.minova.rcp.dataservice.internal;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.IDummyService;

@Component(immediate = true)
public class MinovaImageService {

	private IDataService dataService;

	@Reference
	void getDummyService(IDummyService dummyService) {
		// this method prevents the immediate component to be actived before the
		// workspace location in the dataservice has been set
		// maybe move that to a service property dependency?
	}

	@Reference
	void getDataService(IDataService dataService) {
		this.dataService = dataService;
	}

	@Activate
	public void activatePlugin() {
		dataService.getHashedZip("images.zip");
	}

}
