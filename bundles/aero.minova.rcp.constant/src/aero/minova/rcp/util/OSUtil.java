package aero.minova.rcp.util;

public class OSUtil {
	private OSUtil() {}

	public static boolean isLinux() {
		return System.getProperty("os.name").startsWith("Linux");
	}

}
