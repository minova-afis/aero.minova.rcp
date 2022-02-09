package aero.minova.rcp.widgets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.translation.TranslationService;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.util.WildcardMatcher;

public class LookupContentProvider {
	private LookupComposite lookup;
	protected List<LookupValue> values = new ArrayList<>();

	private Predicate<LookupValue> filter;

	@Inject
	private TranslationService translationService;

	private static final boolean LOG = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.rcp/debug/lookupcontentprovider"));
	private String tableName;

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
		if (LOG) {
			System.out.println("Entry:[" + entry + "]");
		}

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

		// Groß- und Kleinschreibung ignorieren
		result.sort(new SortIgnoreCase());
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
