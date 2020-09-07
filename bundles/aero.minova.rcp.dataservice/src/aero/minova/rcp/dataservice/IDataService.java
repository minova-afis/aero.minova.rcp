package aero.minova.rcp.dataservice;

import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.plugin1.model.Table;

public interface IDataService {

	CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable);

	CompletableFuture<Table> getDetailDataAsync(String tableName, Table detailTable);

	CompletableFuture<Integer> getReturnCodeAsync(String tableName, Table detailTable);

	CompletableFuture<Table> sendNewEntry(String tableName, Table newEntryTable);

	CompletableFuture<Table> updateEntry(String tableName, Table updateTable);

	CompletableFuture<Table> deleteEntry(String tableName, Table deleteTable);

}
