package aero.minova.rcp.dataservice;

import java.util.concurrent.CompletableFuture;

import org.eclipse.e4.ui.di.UISynchronize;

import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;

public interface IDataService {

	CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable);

	CompletableFuture<SqlProcedureResult> getDetailDataAsync(String tableName, Table detailTable);

	CompletableFuture<Integer> getReturnCodeAsync(String tableName, Table detailTable);
	CompletableFuture<String> getFile(String path);
	/**
	 * Eine Datei vom CAS laden. 
	 * Die Datei wird in den Workspace geladen.
	 * Dabei wird die gleiche Struktur, wie auf dem Server verwendet.
	 * </p>
	 * Der Ladeprozess wird asynchron durchgeführt. 
	 * </p>
	 * Die Datei wird nur geladen, wenn sie noch nicht im Workspace ist.
	 *  
	 * @param filename Name inklusive Verzeichnis auf dem CAS.
	 */
	void loadFile(UISynchronize sync, String filename);

	/**
	/**
	 * Eine Datei vom CAS laden. 
	 * Die Datei wird in den Workspace geladen.
	 * Dabei wird die gleiche Struktur, wie auf dem Server verwendet.
	 * </p>
	 * Der Ladeprozess wird asynchron durchgeführt. 
	 *  
	 * @param filename Name inklusive Verzeichnis auf dem CAS.
	 * @param reload true, wenn die Datei auch geladen werden soll, obwohl es schon eine mit dem Namen im Workspace gibt.
	 */
	public void loadFile(UISynchronize sync, String filename, boolean reload);
}
