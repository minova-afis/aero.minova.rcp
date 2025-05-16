package aero.minova.rcp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Row {
	List<Value> values = new ArrayList<>();

	public void addValue(Value v) {
		values.add(v);
	}

	public Value getValue(int index) {
		return index >= 0 && index < values.size() ? values.get(index) : null;
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

	public boolean equals(Object obj, boolean exact) {
		if (exact) {
			return this.equals(obj);
		}

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		Row other = (Row) obj;
		if (values == null) {
			if (other.values != null) {
				return false;
			}
		} else if (values.size() != other.values.size()) {
			return false;
		} else {
			for (int i = 0; i < values.size(); i++) {
				if (!Objects.equals(values.get(i), other.values.get(i))) {
					return false;
				}
			}
		}
		return true;
	}
}
