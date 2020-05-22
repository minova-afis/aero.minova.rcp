package aero.minova.rcp.plugin1.model;

import java.util.List;
import java.util.Vector;

public class Row {
	List<Object> values = new Vector<>();

	public void addValue(Object o) {
		values.add(o);
	}
}
