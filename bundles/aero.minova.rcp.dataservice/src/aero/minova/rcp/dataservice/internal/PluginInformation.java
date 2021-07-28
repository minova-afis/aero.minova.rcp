package aero.minova.rcp.dataservice.internal;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author saak
 * @since 10.3.0
 */
public class PluginInformation {
	File jarFile;
	private String bundleSymbolicName;
	private int majorRelease;
	private int minorRelease;
	private int patchLevel;
	private String buildnumber;

	Pattern pattern = Pattern.compile("^(.+)[_-](\\d+)\\.(\\d+)\\.(\\d+)[\\.-]?(.*)\\.jar$");

	public int getMajorRelease() {
		return majorRelease;
	}

	public PluginInformation(File jarFile) {
		String versionNumber;

		this.jarFile = jarFile;
		String filename = jarFile.getName();


		Matcher matcher = pattern.matcher(filename);
		if (matcher.find(0)) {
			bundleSymbolicName = matcher.group(1);
			versionNumber = matcher.group(2);
			majorRelease = Integer.parseInt(versionNumber);
			versionNumber = matcher.group(3);
			minorRelease = Integer.parseInt(versionNumber);
			versionNumber = matcher.group(4);
			patchLevel = Integer.parseInt(versionNumber);
			buildnumber = matcher.group(5);
			if (buildnumber.equalsIgnoreCase("SNAPSHOT")) {
				// uralt im Vergleich zu einem in eclipse erstelltem Plugin
				buildnumber = "000000000000";
			} else if ("".equals(buildnumber)) {
				buildnumber = null;
			}
		} else {
			// dann haben wir wohl keine Versions-Info im Dateiname
			bundleSymbolicName = filename.substring(0, filename.length() - 4); // .jar entfernen
			// evtl. könnte man das Datum der Datei abfragen?
		}
	}

	public String getBuildnumber() {
		return buildnumber;
	}

	public int getMinorRelease() {
		return minorRelease;
	}

	public int getPatchLevel() {
		return patchLevel;
	}

	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}

	/**
	 * Prüft, ob es sich um das gleiche Bundle handelt. Wenn dem so ist, überprüft es die Versionsnummern und die Buildnumber
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
		if (minorRelease < pluginInformation.getMinorRelease()) {
			return false;
		}
		if (patchLevel < pluginInformation.getPatchLevel()) {
			return false;
		}
		if (buildnumber != null && pluginInformation.getBuildnumber() != null && buildnumber.compareTo(pluginInformation.getBuildnumber()) < 0) {
			return false;
		}
		return true;
	}
}