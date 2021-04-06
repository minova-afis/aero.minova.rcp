package aero.minova.rcp.rcp.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MLookupField;

public class LookupContentProvider {
	private Lookup lookup;
	protected List<LookupValue> values = new ArrayList<>();

	private static final boolean LOG = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.rcp/debug/lookupcontentprovider"));

	/**
	 * Provides the content
	 *
	 * @param entry
	 *            text typed by the user
	 * @return an array list of String that contains propositions for the entry typed by the user
	 */
	public List<LookupValue> getContent(final String entry) {
		MLookupField mField = (MLookupField) lookup.getData(Constants.CONTROL_FIELD);
		if (entry != null && entry.startsWith("#") && entry.length() > 1) {
			// Setzen des Textes, welcher manuell eingetragen wurde
			mField.setWrittenText(entry);
		} else {
			mField.setWrittenText(null);
		}
		if (LOG) {
			System.out.println("Entry:[" + entry + "]");
		}

		String regex = buildRegex(entry);

		return values.stream().filter(lv -> lv.keyText.toUpperCase().matches(regex.toUpperCase()) || lv.description.toUpperCase().matches(regex.toUpperCase()))
				.collect(Collectors.toList());
	}

	private String buildRegex(String entry) {
		String regex = entry;

		regex = regex.replaceAll("%", ".*");
		regex = regex.replaceAll("_", ".");
		regex += ".*";

		return regex;
	}

	/**
	 * @param lookup
	 *            the textAssist to set
	 */
	protected void setLookup(final Lookup lookup) {
		this.lookup = lookup;
	}

	/**
	 * @return the max number of propositions.
	 */
	protected int getMaxNumberOfLines() {
		return this.lookup.getNumberOfLines();
	}

	public void setTable(Table table) {
		values.clear();
		values.addAll(table.getRows().stream().map(r -> {
			int keyLong = r.getValue(0).getIntegerValue();
			String keyText = r.getValue(1) == null ? "" : r.getValue(1).getStringValue();
			String description = r.getValue(2) == null ? "" : r.getValue(2).getStringValue();
			return new LookupValue(keyLong, keyText, description);
		}).sorted((lv1, lv2) -> lv1.compareTo(lv2)).collect(Collectors.toList()));
		lookup.valuesUpdated();
	}

	public void setValuesOnly(List<LookupValue> values) {
		this.values.clear();
		this.values.addAll(values.stream().sorted((lv1, lv2) -> lv1.compareTo(lv2)).collect(Collectors.toList()));
	}

	public void setValues(List<LookupValue> values) {
		this.values.clear();
		this.values.addAll(values.stream().sorted((lv1, lv2) -> lv1.compareTo(lv2)).collect(Collectors.toList()));
		lookup.valuesUpdated();
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public int getValuesSize() {
		return values.size();
	}

}
