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

	@Override
	public String toString() {
		return "Row [values=" + values + "]";
	}

	public List<Value> getValues() {
		return values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Row other = (Row) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (values.size() != other.values.size()) {
			return false;
		} else {
			for (int i = 0; i < values.size(); i++) {
				if (!values.get(i).equals(other.values.get(i))) {
					return false;
				}
			}
		}
		return true;
	}

}
