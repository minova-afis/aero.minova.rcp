package aero.minova.rcp.util;

public class OSUtil {
	private OSUtil() {}

	public static boolean isLinux() {
		return System.getProperty("os.name").toLowerCase().startsWith("linux");
	}

	public static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac");
	}

}
