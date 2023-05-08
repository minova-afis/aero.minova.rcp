package aero.minova.rcp.widgets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.e4.core.services.translation.TranslationService;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.util.WildcardMatcher;

public class LookupContentProvider {
	private LookupComposite lookup;
	protected List<LookupValue> values = new ArrayList<>();

	private Predicate<LookupValue> filter;

	@Inject
	private TranslationService translationService;

	private String tableName;

	private Comparator<LookupValue> customComparator;

	public LookupContentProvider(String tableName) {
		this.tableName = tableName;
	}

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

		WildcardMatcher matcher = new WildcardMatcher(entry.toUpperCase());
		List<LookupValue> result = values.stream()
				.filter(lv -> matcher.matches(lv.keyText.toUpperCase()) || matcher.matches(lv.description.replace("\r\n", "; ").toUpperCase()))
				.collect(Collectors.toList());

		// Wenn gegeben, weiteren Filter anwenden
		if (getFilter() != null) {
			result = result.stream().filter(lv -> getFilter().test(lv)).collect(Collectors.toList());
		}

		// Wenn möglich Übersetzten
		if (tableName != null) {
			for (LookupValue lv : result) {
				translateLookup(lv);
			}
		}

		if (getCustomComparator() == null) {
			// Default wenn kein Comparator gesetzt: Groß- und Kleinschreibung ignorieren, bei entry == Matchcode dieses Element an erste Stelle, siehe #1086
			result.sort(new SortIgnoreCase(entry));
		} else {
			result.sort(getCustomComparator());
		}
		return result;
	}

	public void translateLookup(LookupValue lv) {
		String translateKey = tableName + ".KeyText." + lv.keyLong;
		String translated = translationService.translate("@" + tableName + ".KeyText." + lv.keyLong, null);
		lv.keyText = translateKey.equals(translated) ? lv.keyText : translated;

		translateKey = tableName + ".Description." + lv.keyLong;
		translated = translationService.translate("@" + tableName + ".Description." + lv.keyLong, null);
		lv.description = translateKey.equals(translated) ? lv.description : translated;
	}

	/**
	 * @param lookup
	 *            the textAssist to set
	 */
	public void setLookup(final LookupComposite lookup) {
		this.lookup = lookup;
	}

	public void setValuesOnly(List<LookupValue> values) {
		this.values.clear();
		this.values.addAll(values.stream().sorted((lv1, lv2) -> lv1.compareTo(lv2)).toList());
	}

	public void setValues(List<LookupValue> values) {
		this.values.clear();
		this.values.addAll(values.stream().sorted((lv1, lv2) -> lv1.compareTo(lv2)).toList());
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

	public Comparator<LookupValue> getCustomComparator() {
		return customComparator;
	}

	public void setCustomComparator(Comparator<LookupValue> customComparator) {
		this.customComparator = customComparator;
	}

	public class SortIgnoreCase implements Comparator<LookupValue> {
		private String entry;

		public SortIgnoreCase(String entry) {
			this.entry = entry;
		}

		@Override
		public int compare(LookupValue o1, LookupValue o2) {
			String s1 = o1.keyText;
			String s2 = o2.keyText;

			// Bei genauer Übereinstimmung des Matchcodes Element an erste Stelle setzten, siehe #1086
			if (s1.equalsIgnoreCase(entry)) {
				return -1;
			} else if (s2.equalsIgnoreCase(entry)) {
				return 1;
			}

			return s1.toLowerCase().compareTo(s2.toLowerCase());
		}
	}
}
