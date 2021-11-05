package aero.minova.rcp.dataservice.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

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

		int lastIndexOf = helperClass.lastIndexOf('.');
		String pluginName = helperClass.substring(0, lastIndexOf);
		Path storagePath = dataService.getStoragePath();
		Path pluginPath = Paths.get(storagePath.toString(), "plugins");
		List<Path> plugins = null;
		try {
			plugins = Files.list(pluginPath).filter(f -> f.toString().contains(pluginName)).filter(f -> f.toString().toLowerCase().endsWith("jar"))
					.collect(Collectors.toList());

			if (plugins.isEmpty()) {
				System.err.println("Plugin für Klasse " + helperClass + " konnte nicht geladen werden");
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		BundleContext bundleContext = FrameworkUtil.getBundle(MinovaPluginService.class).getBundleContext();
		try {
			Bundle[] bundles = bundleContext.getBundles();

			for (Bundle bundle : bundles) {
				Version bundleVersion = bundle.getVersion();
				if (bundle.getSymbolicName().equals(pluginName)) {
					PluginInformation pI = new PluginInformation(plugins.get(0).toFile());
					if (!pI.isDifferent(bundleVersion)) {
						return;
					}
					bundle.uninstall();
				}
			}

			Bundle installBundle = bundleContext.installBundle(plugins.get(0).toUri().toString());
			installBundle.start(Bundle.START_ACTIVATION_POLICY);
		} catch (BundleException e) {
			e.printStackTrace();
		}
	}

}
