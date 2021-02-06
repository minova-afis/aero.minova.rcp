package aero.minova.rcp.dataservice;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.e4.ui.di.UISynchronize;

import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MLookupField;

public interface IDataService {

	void setCredentials(String username, String password, String server);

	CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable);

	CompletableFuture<SqlProcedureResult> getDetailDataAsync(String tableName, Table detailTable);

	CompletableFuture<Integer> getReturnCodeAsync(String tableName, Table detailTable);

	CompletableFuture<List<LookupValue>> resolveLookupAsync(Integer keyLong, String keyText, MLookupField field, boolean useCache);

	CompletableFuture<List<LookupValue>> listLookupAsync(String filterText, MLookupField field, boolean useCache);

	CompletableFuture<String> getFile(String path);

	/**
	 * Eine Datei vom CAS laden. Die Datei wird in den Workspace geladen. Dabei wird
	 * die gleiche Struktur, wie auf dem Server verwendet.
	 * </p>
	 * Der Ladeprozess wird asynchron durchgeführt.
	 * </p>
	 * Die Datei wird nur geladen, wenn sie noch nicht im Workspace ist.
	 * 
	 * @param filename Name inklusive Verzeichnis auf dem CAS.
	 */
	void loadFile(UISynchronize sync, String filename);

	File getFileSynch(String path, String mdiFileName);

	/**
	 * synchrones laden einer Datei vom Cache.
	 *
	 * @param filename relativer Pfad und Dateiname auf dem Server
	 * @return Die Datei, wenn sie geladen werden konnte; ansonsten null
	 */
	File getFileSynch(String filename);

	Path getStoragePath();

}
