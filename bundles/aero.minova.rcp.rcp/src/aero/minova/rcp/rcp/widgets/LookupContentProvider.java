package aero.minova.rcp.rcp.widgets;

import java.util.List;

import aero.minova.rcp.model.Table;

public abstract class LookupContentProvider {
	private Lookup textAssist;
	protected Table table;

	/**
	 * Provides the content
	 *
	 * @param entry text typed by the user
	 * @return an array list of String that contains propositions for the entry
	 *         typed by the user
	 */
	public abstract List<String> getContent(final String entry);

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
		this.table = table;
	}

	public Table getTable() {
		return table;
	}

	public boolean tableIsEmpty() {
		if (table == null || table.getRows().isEmpty()) {
			return true;
		}
		return false;
	}

}
