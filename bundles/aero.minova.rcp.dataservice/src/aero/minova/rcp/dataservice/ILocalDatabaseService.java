package aero.minova.rcp.dataservice;

import aero.minova.rcp.model.Table;

public interface ILocalDatabaseService {

	Table getResultsForLookupField(String name);

	void replaceResultsForLookupField(String name, Table table);
}
