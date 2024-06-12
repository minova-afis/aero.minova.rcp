package aero.minova.rcp.rcp.addons;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;

import aero.minova.rcp.dataservice.IDataService;

public class WindowLabelAddon {

	ILog logger = Platform.getLog(this.getClass());

	@Inject
	@Optional
	private void adjustWindowLabel(@UIEventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) MApplication application, IDataService dataService) {
		MWindow mainWindow = application.getChildren().get(0);

		String label = dataService.getServer().toString();

		try {
			label = dataService.getCASLabel().get();
		} catch (ExecutionException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}

		mainWindow.setLabel("Free Tables (" + label + ")");
	}
}
