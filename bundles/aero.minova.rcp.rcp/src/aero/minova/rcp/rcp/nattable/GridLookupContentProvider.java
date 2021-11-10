package aero.minova.rcp.rcp.nattable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.LookupValue;

public class GridLookupContentProvider {

	List<LookupValue> values;
	IDataService dataService;
	String tablename;

	public GridLookupContentProvider(IDataService dataService, String tablename) {
		values = new ArrayList<>();
		this.dataService = dataService;
		this.tablename = tablename;
		CompletableFuture<List<LookupValue>> listLookup = dataService.resolveGridLookup(tablename, true);
		listLookup.thenAccept(l -> values.addAll(l));
	}

	public List<LookupValue> getValues() {
		return values;
	}

	public void update() {
		values.clear();
		CompletableFuture<List<LookupValue>> listLookup = dataService.resolveGridLookup(tablename, true);
		listLookup.thenAccept(l -> values.addAll(l));
	}
}
