package aero.minova.rcp.dataservice;

import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.plugin1.model.Table;

public interface IDataService {

	CompletableFuture<Table> getDataAsync(String tableName, Table seachTable);
}
