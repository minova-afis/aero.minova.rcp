package aero.minova.rcp.dataservice;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.TransactionEntry;
import aero.minova.rcp.model.TransactionResultEntry;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MLookupField;

public interface IDataService {

	void setCredentials(String username, String password, String server, URI uri);

	/**
	 * Anfrage an den Server ein Table object zu bekommen, mit den Suchkriterium definiert über den searchTab
	 *
	 * @param seachTable
	 *            Suchkriterien
	 * @return
	 */
	CompletableFuture<Table> getTableAsync(Table searchTable);

	/**
	 * Anfrage an den Server um eine Prozedur aufzurufen. Die Parameter sind über die Tabelle definiert, als Prozedur-Name wird der Name der Tabelle verwendet
	 * 
	 * @param detailTable
	 * @return
	 */
	CompletableFuture<SqlProcedureResult> callProcedureAsync(Table detailTable);

	/**
	 * Anfrage an den Server über eine Transaktion. Wenn in einer der Prozeduren ein Fehler auftritt wird die gesamte Transaktion nicht ausgeführt. Evtl werden
	 * auf CAS-Seite weitere Prozeduren ausgeführt.
	 * 
	 * @param procedureList
	 * @return
	 */
	CompletableFuture<List<TransactionResultEntry>> callTransactionAsync(List<TransactionEntry> procedureList);

	/**
	 * Diese Methode löst einen Wert auf. Für den gegebenen keyLong und/oder keyText wird das entsprechende LookupValue angefragt
	 *
	 * @param field
	 *            Über dieser Feld werden alle erforderlichen Konfigurationswerte geselesen.
	 * @param useCache
	 *            Wenn dieser Wert true ist, werden die Daten zuerst im Cache gesucht. Werden sie dort gefunden, wird die Suche beendet. Wenn der Wert nicht im
	 *            Cache gefunden wird, oder dieser Wert false ist, wird die Datenbank / der CAS angefragt.
	 * @param keyLong
	 *            Wenn dieser Wert nicht leer (null) ist, wird nach genau diesem Wert gesucht
	 * @param keyText
	 *            Der Keytext muss vollständig angegeben sein (Groß-/Kleinschreibung wir ignoriert)
	 * @return
	 */
	CompletableFuture<List<LookupValue>> resolveLookup(MLookupField field, boolean useCache, Integer keyLong, String keyText);

	CompletableFuture<List<LookupValue>> resolveGridLookup(String tableName, boolean useCache);

	/**
	 * Diese Methode liefert alle möglichen Werte für das gegebene Lookup.
	 *
	 * @param field
	 *            Über dieser Feld werden alle erforderlichen Konfigurationswerte geselesen.
	 * @param useCache
	 *            Wenn dieser Wert true ist, werden die Daten zuerst im Cache gesucht. Werden sie dort gefunden, wird die Suche beendet. Wenn der Wert nicht im
	 *            Cache gefunden wird, oder dieser Wert false ist, wird die Datenbank / der CAS angefragt.
	 * @return
	 */
	CompletableFuture<List<LookupValue>> listLookup(MLookupField field, boolean useCache);

	/**
	 * Laden einer XML Datei. Diese wird im reports Ordner des Workspaces abgelegt
	 * 
	 * @param table
	 *            Eine Spalte und eine Zeile, der KeyLong für die die xml Datei angefragt werden soll
	 * @param rootElement
	 *            Sollte aus .xbs ausgelesen werden
	 * @return
	 */
	CompletableFuture<Path> getXMLAsync(Table table, String rootElement);

	/**
	 * Fragt eine PDF vom CAS an und speichert sie in den Workspace Ordner
	 * 
	 * @param table
	 *            Die Tabelle über die die Prozedur angefragt wird
	 * @param fileName
	 *            gewünschter Name des Files, kann auch mit weiterem Ordner angegeben werden. z.B. "reports/Report.pdf", "pdf/File.pdf", "Rechnung1.pdf";
	 * @return
	 */
	CompletableFuture<Path> getPDFAsync(Table table, String fileName);

	/**
	 * Ersatz für die alte JavaScript Methode getSQLValue() aus Masken.
	 * 
	 * @param tablename
	 * @param requestColumn
	 * @param requestValue
	 * @param resultColumn
	 * @param resultType
	 * @return
	 */
	CompletableFuture<Value> getSQLValue(String tablename, String requestColumn, Value requestValue, String resultColumn, DataType resultType);

	/**
	 * Fragt die Bezeichnung des verbundenen CAS zurück. Ist kein Label definiert wird die URL zurückgegeben
	 * 
	 * @return
	 */
	CompletableFuture<String> getCASLabel();

	CompletableFuture<String> getHashedFile(String filename);

	boolean checkIfUpdateIsRequired(String fileName) throws IOException, InterruptedException;

	/**
	 * asynchrones laden einer Datei vom Server.
	 *
	 * @param serverFileName
	 *            relativer Pfad und Dateiname auf dem Server
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	CompletableFuture<Path> downloadFile(String serverFileName) throws IOException, InterruptedException;

	CompletableFuture<String> getCachedFileContent(String filename);

	/**
	 * returns true if zip file could be downloaded
	 */
	boolean getHashedZip(String zipname);

	String getUserName();

	void sendLogs();

	Path getStoragePath();

	/**
	 * Liefert den Wert aus der Tabelle tSiteParameter wenn der Key existiert, ansonsten den Default-Wert
	 * 
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	String getSiteParameter(String key, String defaultVal);

	HttpClient.Builder getHttpClientBuilder();

	URI getServer();
}
