package aero.minova.rcp.plugin1.model.builder;

import aero.minova.rcp.plugin1.model.Row;
import aero.minova.rcp.plugin1.model.Value;

public class RowBuilder {

	private Row row;

	private RowBuilder() {
		row = new Row();
	}

	public static RowBuilder newRow() {
		return new RowBuilder();
	}

	public RowBuilder withValue(Object value) {
		if (value != null) {
			row.addValue(new Value(value));
		} else {
			row.addValue(null);
		}
		return this;
	}

	public Row create() {
		return row;
	}
}
