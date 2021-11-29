package aero.minova.rcp.widgets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MLookupField;

public class LookupContentProvider {
	private LookupComposite lookup;
	protected List<LookupValue> values = new ArrayList<>();

	private Predicate<LookupValue> filter;

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
		mField.setWrittenText(entry);
		if (LOG) {
			System.out.println("Entry:[" + entry + "]");
		}

		String regex = buildRegex(entry);
		List<LookupValue> result = values.stream().filter(
				lv -> lv.keyText.toUpperCase().matches(regex.toUpperCase()) || lv.description.replace("\r\n", "; ").toUpperCase().matches(regex.toUpperCase()))
				.collect(Collectors.toList());

		// Wenn gegeben, weiteren Filter anwenden
		if (getFilter() != null) {
			result = values.stream().filter(lv -> getFilter().test(lv)).collect(Collectors.toList());
		}

		// Groß- und Kleinschreibung ignorieren
		result.sort(new SortIgnoreCase());
		return result;
	}

	private String buildRegex(String entry) {
		String regex = "";

		for (String s : entry.split("%", -1)) {
			for (String s2 : s.split("_", -1)) {
				regex += Pattern.quote(s2) + ".";
			}
			if (regex.length() > 0) {
				regex = regex.substring(0, regex.length() - 1);
			}
			regex += ".*";
		}

		return regex;
	}

	/**
	 * @param lookup
	 *            the textAssist to set
	 */
	protected void setLookup(final LookupComposite lookup) {
		this.lookup = lookup;
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

	public int getValuesSize() {
		return values.size();
	}

	public Predicate<LookupValue> getFilter() {
		return filter;
	}

	public void setFilter(Predicate<LookupValue> filter) {
		this.filter = filter;
	}

	public class SortIgnoreCase implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			String s1 = ((LookupValue) o1).keyText;
			String s2 = ((LookupValue) o2).keyText;
			return s1.toLowerCase().compareTo(s2.toLowerCase());
		}
	}
}
