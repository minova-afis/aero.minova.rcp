package aero.minova.rcp.dataservice;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MLookupField;

public interface IDataService {

	void setCredentials(String username, String password, String server, String workspacePath);

	/**
	 * Anfrage an den Server ein Table object zu bekommen, mit den Suchkriterium
	 * definiert über den searchTab
	 * 
	 * @param tableName
	 * @param seachTable
	 * @return
	 */
	CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable);

	CompletableFuture<SqlProcedureResult> getDetailDataAsync(String tableName, Table detailTable);

//	CompletableFuture<Integer> getReturnCodeAsync(String tableName, Table detailTable);
//
//	CompletableFuture<List<LookupValue>> resolveLookupAsync(Integer keyLong, String keyText, MLookupField field, boolean useCache);
//
//	CompletableFuture<List<LookupValue>> listLookupAsync(String filterText, MLookupField field, boolean useCache);

	/**
	 * Diese Methode löst einen Wert auf.
	 * 
	 * @param field
	 *            Über dieser Feld werden alle erforderlichen Konfigurationswerte geselesen.
	 * @param useCache
	 *            Wenn dieser Wert true ist, werden die Daten zuerst im Cache gesucht. Werden sie dort gefunden, wird die Suche beendet. Wenn der Wert nicht im
	 *            Cache gefunden wird, oder dieser Wert false ist, wird die Datenbank / der CAS angefragt.
	 * @param keyLong
	 *            Wenn dieser Wert nicht leer (null) ist, wird nach genau diesem Wert gesucht. Der Wert von KeyText wird ignoriert.
	 * @param keyText
	 *            Der Keytext muss vollständig angegeben sein (Groß-/Kleinschreibung wir ignoriert). Er wird nur verwendet, wenn der KeyLong null ist.
	 * @return
	 */
	public CompletableFuture<List<LookupValue>> resolveLookup(MLookupField field, boolean useCache, Integer keyLong, String keyText);

	/**
	 * Diese Methode liefert alle möglichen Werte für den angegebenen Filtertext.
	 * 
	 * @param field
	 *            Über dieser Feld werden alle erforderlichen Konfigurationswerte geselesen.
	 * @param useCache
	 *            Wenn dieser Wert true ist, werden die Daten zuerst im Cache gesucht. Werden sie dort gefunden, wird die Suche beendet. Wenn der Wert nicht im
	 *            Cache gefunden wird, oder dieser Wert false ist, wird die Datenbank / der CAS angefragt.
	 * @param filterText
	 *            Der Text, nach dem gefiltert werden soll. Wenn nichts angegeben wird, sollen alle möglichen Werte zurückgegeben werden. Als Wildcard sind "%"
	 *            und "_" erlaubt, wie es im SQL-Server Standard ist.
	 * @return
	 */
	public CompletableFuture<List<LookupValue>> listLookup(MLookupField field, boolean useCache, String filterText);

	CompletableFuture<String> getFile(String path);
	CompletableFuture<Path> getPath(String path);

	/**
	 * Eine Datei vom CAS laden. Die Datei wird in den Workspace geladen. Dabei wird
	 * die gleiche Struktur, wie auf dem Server verwendet.
	 * </p>
	 * </p>
	 * Die Datei wird nur geladen, wenn sie noch nicht im Workspace ist.
	 * 
	 * @param filename Name inklusive Verzeichnis auf dem CAS.
	 */

	<T> T convert(File f, Class<T> clazz);


	String getFileContent(String path);
	/**
	 * synchrones laden einer Datei vom Cache.
	 *
	 * @param filename relativer Pfad und Dateiname auf dem Server
	 * @return Die Datei, wenn sie geladen werden konnte; ansonsten null
	 */
	File getFileSynch(String filename);

	Path getStoragePath();

}
