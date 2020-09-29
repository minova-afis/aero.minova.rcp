package aero.minova.rcp.dataservice;

import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;

public interface IDataService {

	CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable);

	CompletableFuture<SqlProcedureResult> getDetailDataAsync(String tableName, Table detailTable);

}
