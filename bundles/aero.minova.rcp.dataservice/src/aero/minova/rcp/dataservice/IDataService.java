package aero.minova.rcp.dataservice;

import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.model.Table;

public interface IDataService {

	CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable);

	CompletableFuture<Table> getDetailDataAsync(String tableName, Table detailTable);

}
