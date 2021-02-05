package aero.minova.rcp.rcp.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Table;

public class LookupContentProvider {
	private Lookup textAssist;
	protected List<LookupValue> values = new ArrayList<>();

	/**
	 * Provides the content
	 *
	 * @param entry text typed by the user
	 * @return an array list of String that contains propositions for the entry
	 *         typed by the user
	 */
	public List<LookupValue> getContent(final String entry) {
		if ("%".equals(entry)) {
			return values;
		} else {
			return values.stream()
					.filter(lv -> lv.keyText.toUpperCase().startsWith(entry.toUpperCase()) || lv.description.toUpperCase().startsWith(entry.toUpperCase()))
					.collect(Collectors.toList());
		}
	}

	/**
	 * @param textAssist the textAssist to set
	 */
	protected void setTextAssist(final Lookup textAssist) {
		this.textAssist = textAssist;
	}

	/**
	 * @return the max number of propositions.
	 */
	protected int getMaxNumberOfLines() {
		return this.textAssist.getNumberOfLines();
	}

	public void setTable(Table table) {
		values.clear();
		values.addAll(table.getRows().stream().map(r -> {
			int keyLong = r.getValue(0).getIntegerValue();
			String keyText = r.getValue(1) == null ? "" : r.getValue(1).getStringValue();
			String description = r.getValue(2) == null ? "" : r.getValue(2).getStringValue();
			return new LookupValue(keyLong, keyText, description);
		}).collect(Collectors.toList()));
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

}
