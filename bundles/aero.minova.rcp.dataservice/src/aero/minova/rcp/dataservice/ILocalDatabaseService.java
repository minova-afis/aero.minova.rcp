package aero.minova.rcp.dataservice;

import java.util.Map;
import java.util.ResourceBundle.Control;

import aero.minova.rcp.model.Table;

public interface ILocalDatabaseService {

	Table getResultsForLookupField(String name);

	void replaceResultsForLookupField(String name, Map<String, Control> map);
}
