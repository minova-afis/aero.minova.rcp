package aero.minova.rcp.rcp.util;

import java.util.HashMap;
import java.util.Map;

import aero.minova.rcp.form.setup.xbs.Map.Entry;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;

public class XBSUtil {

	/**
	 * Liefert die ERSTE Node mit dem angegebenen Namen aus den Preferences
	 * 
	 * @param preferences
	 *            aus der .xbs ausgelesenen Preferences
	 * @param name
	 *            der Name der gesuchten Node
	 */
	public static Node getNodeWithName(Preferences preferences, String name) {

		for (Node node : preferences.getRoot().getNode()) {
			Node nodeWithName = walkNode(node, name);
			if (nodeWithName != null) {
				return nodeWithName;
			}
		}

		return null;
	}

	private static Node walkNode(Node node, String name) {
		if (node.getName().equals(name)) {
			return node;
		}

		for (Node childNode : node.getNode()) {
			Node nodeWithName = walkNode(childNode, name);
			if (nodeWithName != null) {
				return nodeWithName;
			}
		}

		return null;
	}

	/**
	 * Liefert eine Map von Feldnamen in der OP zu "Index" des primary Keys in der Hauptmaske. "Index" 0 bedeutet erstes primary-Feld, es können nicht-primary
	 * Felder davorstehen.
	 */
	public static Map<String, Integer> getKeynamesToIndex(Node node) {
		Map<String, Integer> namesToIndex = new HashMap<>();

		for (Entry entry : node.getMap().getEntry()) {
			String uppercaseString = entry.getKey().toUpperCase();
			if (uppercaseString.startsWith("KEY")) {
				int index = Integer.parseInt(uppercaseString.replace("KEY", ""));
				namesToIndex.put(entry.getValue(), index);
			}
		}

		return namesToIndex;
	}
}
