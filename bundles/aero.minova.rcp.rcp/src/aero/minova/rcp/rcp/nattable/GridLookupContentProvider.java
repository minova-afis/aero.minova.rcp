package aero.minova.rcp.rcp.nattable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.LookupValue;

public class GridLookupContentProvider {

	List<LookupValue> originalValues;
	List<LookupValue> values;
	IDataService dataService;
	String tablename;

	public GridLookupContentProvider(IDataService dataService, String tablename) {
		originalValues = new ArrayList<>();
		values = new ArrayList<>();
		this.dataService = dataService;
		this.tablename = tablename;
		CompletableFuture<List<LookupValue>> listLookup = dataService.resolveGridLookup(tablename, true);
		listLookup.thenAccept(l -> {
			originalValues.addAll(l);
			values.addAll(originalValues);
		});

	}

	public List<LookupValue> getValues() {
		return values;
	}

	public void update() {
		CompletableFuture<List<LookupValue>> listLookup = dataService.resolveGridLookup(tablename, true);
		listLookup.thenAccept(l -> {
			originalValues.clear();
			originalValues.addAll(l);
			values.clear();
			values.addAll(originalValues);
		});
	}
	
	public String[] getOriginalValueArray() {
		String[] stringValues = new String[originalValues.size()];
		for (int i = 0; i < originalValues.size(); i++) {
			stringValues[i] = originalValues.get(i).keyText;
		}
		return stringValues;
	}

	public String[] filterContent(String entry) {

		String regex = buildRegex(entry);
		values.clear();
		values = originalValues.stream().filter(
				lv -> lv.keyText.toUpperCase().matches(regex.toUpperCase()) || lv.description.replace("\r\n", "; ").toUpperCase().matches(regex.toUpperCase()))
				.collect(Collectors.toList());
		values.sort(new SortIgnoreCase());
		String[] stringValues = new String[values.size()];
		for (int i = 0; i < values.size(); i++) {
			stringValues[i] = values.get(i).keyText;
		}
		return stringValues;
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

	public class SortIgnoreCase implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			String s1 = ((LookupValue) o1).keyText;
			String s2 = ((LookupValue) o2).keyText;
			return s1.toLowerCase().compareTo(s2.toLowerCase());
		}
	}
}
