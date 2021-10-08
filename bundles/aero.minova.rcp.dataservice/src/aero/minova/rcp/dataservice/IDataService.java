package aero.minova.rcp.dataservice;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.e4.core.services.log.Logger;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MLookupField;

public interface IDataService {

	void setCredentials(String username, String password, String server, URI uri);

	/**
	 * Anfrage an den Server ein Table object zu bekommen, mit den Suchkriterium definiert über den searchTab
	 *
	 * @param tableName
	 * @param seachTable
	 * @return
	 */
	CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable);

	CompletableFuture<SqlProcedureResult> getDetailDataAsync(String tableName, Table detailTable);

	CompletableFuture<SqlProcedureResult> getGridDataAsync(String tableName, Table detailTable);

	CompletableFuture<Path> getXMLAsync(String tableName, Table detailTable, String rootElement);

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
	CompletableFuture<List<LookupValue>> resolveLookup(MLookupField field, boolean useCache, Integer keyLong, String keyText);

	CompletableFuture<List<LookupValue>> resolveGridLookup(String tableName, boolean useCache);

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
	CompletableFuture<List<LookupValue>> listLookup(MLookupField field, boolean useCache, String filterText);

	Path getStoragePath();

	CompletableFuture<String> getHashedFile(String filename);

	boolean checkIfUpdateIsRequired(String fileName) throws IOException, InterruptedException;

	/**
	 * synchrones laden einer Datei vom Server.
	 *
	 * @param localPath
	 *            Lokaler Pfad für die Datei. Der Pfad vom #filename wird noch mit angehängt.
	 * @param filename
	 *            relativer Pfad und Dateiname auf dem Server
	 * @return Die Datei, wenn sie geladen werden konnte; ansonsten null
	 * @throws InterruptedException
	 * @throws IOException
	 */

	void downloadFile(String serverFileName) throws IOException, InterruptedException;

	String getUserName();

	void setLogger(Logger logger);

	CompletableFuture<String> getCachedFileContent(String filename);

	/*
	 * returns true if zip file could be downloaded
	 */
	boolean getHashedZip(String zipname);

	void setTimeout(int timeout);

	void setTimeoutOpenNotification(int timeoutOpenNotification);

	void sendLogs();

	CompletableFuture<Value> getSQLValue(String tablename, String requestColumn, Value requestValue, String resultColumn, DataType resultType);

	/**
	 * Liefert den Wert aus der Tabelle tSiteParameter wenn der Key existiert, ansonsten den Default-Wert
	 * 
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	String getSiteParameter(String key, String defaultVal);

}
