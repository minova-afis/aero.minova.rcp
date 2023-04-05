package aero.minova.rcp.rcp.util;

import java.util.Comparator;

public class CustomComparator implements Comparator<Object> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			} else {
				return -1;
			}
		} else if (o2 == null) {
			return 1;

		} else if (o1 instanceof Comparable c1 && o2 instanceof Comparable c2 && //
				c1.getClass().equals(c2.getClass())) { // Auch überprüfen, ob die Objekte die gleiche Klasse haben
			return c1.compareTo(c2);
		} else {
			return o1.toString().compareTo(o2.toString());
		}
	}
}