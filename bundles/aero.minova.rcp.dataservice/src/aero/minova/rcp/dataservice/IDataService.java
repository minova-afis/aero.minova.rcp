package aero.minova.rcp.dataservice;

import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.plugin1.model.Table;

public interface IDataService {

	@Deprecated
	Table getData(String tableName, Table seachTable);

	@Deprecated
	Table getData(String tableName);
	
	
	CompletableFuture<Table> getDataAsync(String tableName, Table seachTable);
	//TODO 
//	CompletableFuture<Table> getDataAsync(String tableName);
}
