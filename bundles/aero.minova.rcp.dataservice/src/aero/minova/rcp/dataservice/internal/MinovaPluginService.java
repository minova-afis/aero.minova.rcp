package aero.minova.rcp.dataservice.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.IDummyService;
import aero.minova.rcp.dataservice.IMinovaPluginService;

@Component(immediate = true)
public class MinovaPluginService implements IMinovaPluginService {

	private static final boolean USE_LOCAL_HELPER = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/uselocalhelper"));

	private IDataService dataService;
	private boolean downloadPlugins = true;
	ILog logger = Platform.getLog(this.getClass());

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

	@Override
	public void activatePlugin(String helperClass) {
		if (USE_LOCAL_HELPER) {
			return;
		}
		if (downloadPlugins) {
			dataService.getHashedZip("plugins.zip");
			downloadPlugins = false;
		}

		// Passende Plugins finden
		int lastIndexOf = helperClass.lastIndexOf('.');
		String pluginName = helperClass.substring(0, lastIndexOf);
		Path storagePath = dataService.getStoragePath();
		Path pluginPath = Paths.get(storagePath.toString(), "plugins");
		List<Path> plugins = null;
		try (Stream<Path> list = Files.list(pluginPath)) {
			plugins = list.filter(f -> f.toString().contains(pluginName)).filter(f -> f.toString().toLowerCase().endsWith("jar")).toList();
			if (plugins.isEmpty()) {
				logger.error("Plugin für Klasse " + helperClass + " konnte nicht geladen werden");
				return;
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return;
		}

		// Aktuellstes Plugin finden
		Path newestPlugin = plugins.get(0);
		PluginInformation newestPluginInfo = new PluginInformation(plugins.get(0).toFile());
		for (Path p : plugins) {
			PluginInformation pI = new PluginInformation(p.toFile());
			if (pI.isNewerAs(newestPluginInfo)) {
				newestPlugin = p;
				newestPluginInfo = pI;
			}
		}

		BundleContext bundleContext = FrameworkUtil.getBundle(MinovaPluginService.class).getBundleContext();
		try {
			Bundle[] bundles = bundleContext.getBundles();

			// Wenn vorhanden alte Version deinstallieren
			for (Bundle bundle : bundles) {
				Version bundleVersion = bundle.getVersion();
				if (bundle.getSymbolicName().equals(pluginName)) {
					if (!newestPluginInfo.isDifferent(bundleVersion)) {
						return;
					}
					bundle.uninstall();
				}
			}

			// Plugin installieren
			Bundle installBundle = bundleContext.installBundle(newestPlugin.toUri().toString());
			installBundle.start(Bundle.START_ACTIVATION_POLICY);
		} catch (BundleException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
