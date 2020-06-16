package aero.minova.rcp.plugin1.model;

import java.util.List;
import java.util.Vector;

public class Row {
	List<Value> values = new Vector<>();

	public void addValue(Value v) {
		values.add(v);
	}
}
