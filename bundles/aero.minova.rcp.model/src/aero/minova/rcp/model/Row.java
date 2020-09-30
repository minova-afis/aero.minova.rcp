package aero.minova.rcp.model;

import java.util.ArrayList;
import java.util.List;

public class Row {
	List<Value> values = new ArrayList<>();

	public void addValue(Value v) {
		values.add(v);
	}

	public Value getValue(int index) {
		return values.get(index);
	}

	public void setValue(Value value, int index) {
		values.set(index, value);
	}

	public int size() {
		return values.size();
	}
}
