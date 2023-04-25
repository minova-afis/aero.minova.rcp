package aero.minova.rcp.dataservice.internal;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Version;

/**
 * @author saak
 * @since 10.3.0
 */
public class PluginInformation {
	private String bundleSymbolicName;
	private int majorRelease;
	private int minorRelease;
	private int patchLevel;
	private String qualifier;

	Pattern pattern = Pattern.compile("^(.+)[_-](\\d+)\\.(\\d+)\\.(\\d+)[\\.-]?(.*)\\.jar$");

	public PluginInformation(File jarFile) {
		this(jarFile.getName());
	}

	public PluginInformation(String filename) {

		Matcher matcher = pattern.matcher(filename);
		if (matcher.find(0)) {
			bundleSymbolicName = matcher.group(1);
			String versionNumber = matcher.group(2);
			majorRelease = Integer.parseInt(versionNumber);
			versionNumber = matcher.group(3);
			minorRelease = Integer.parseInt(versionNumber);
			versionNumber = matcher.group(4);
			patchLevel = Integer.parseInt(versionNumber);
			qualifier = matcher.group(5);
		} else {
			// dann haben wir wohl keine Versions-Info im Dateiname
			bundleSymbolicName = filename.substring(0, filename.length() - 4); // .jar entfernen
			// evtl. könnte man das Datum der Datei abfragen?
		}
	}

	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}

	public int getMajorRelease() {
		return majorRelease;
	}

	public int getMinorRelease() {
		return minorRelease;
	}

	public int getPatchLevel() {
		return patchLevel;
	}

	public String getBuildnumber() {
		return qualifier;
	}

	/**
	 * Prüft, ob es sich um das gleiche Bundle handelt. Wenn dem so ist, überprüft es die Versionsnummern. Der Aualifier wird NICHT überprüft
	 *
	 * @param pluginInformation
	 * @return true, wenn diese PluginInformation das jüngere Plugin beschreibt
	 */
	public boolean isNewerAs(PluginInformation pluginInformation) {
		if (!bundleSymbolicName.equals(pluginInformation.getBundleSymbolicName())) {
			return false;
		}

		if (majorRelease < pluginInformation.getMajorRelease()) {
			return false;
		}
		if (majorRelease > pluginInformation.getMajorRelease()) {
			return true;
		}

		if (minorRelease < pluginInformation.getMinorRelease()) {
			return false;
		}
		if (minorRelease > pluginInformation.getMinorRelease()) {
			return true;
		}

		if (patchLevel < pluginInformation.getPatchLevel()) {
			return false;
		}
		return (patchLevel > pluginInformation.getPatchLevel());
	}

	/**
	 * Prüft, überprüft die Versionsnummern und den Qualifier
	 *
	 * @param Version
	 *            pluginInformation
	 * @return true, wenn diese PluginInformation das aktuellere Plugin beschreibt
	 */
	public boolean isDifferent(Version version) {
		if (majorRelease != version.getMajor()) {
			return true;
		}

		if (minorRelease != version.getMinor()) {
			return true;
		}

		if (patchLevel != version.getMicro()) {
			return true;
		}

		return !Objects.equals(qualifier, version.getQualifier());
	}

	@Override
	public String toString() {
		return "PluginInformation " + bundleSymbolicName + "-" + majorRelease + "." + minorRelease + "." + patchLevel
				+ (!qualifier.equals("") ? "-" + qualifier : "");
	}
}