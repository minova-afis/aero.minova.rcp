package aero.minova.rcp.dataservice;

import aero.minova.rcp.plugin1.model.Table;

public interface IDataService {

	Table getData(String tableName, Table seachTable);

	Table getData(String tableName);
}
