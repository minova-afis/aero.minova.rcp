package aero.minova.rcp.dataservice.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.IDummyService;
import aero.minova.rcp.dataservice.ZipService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.ColumnSerializer;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.FilterValue;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.ServerAnswer;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.TransactionEntry;
import aero.minova.rcp.model.TransactionResultEntry;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.util.ErrorObject;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.util.SSLContextUtil;

@Component
public class DataService implements IDataService {

	ILog logger = Platform.getLog(this.getClass());

	HashMap<String, String> serverHashes = new HashMap<>();

	/**
	 * Je Prozedur bzw. Tabellenneame gibt es einen Cache je KeyLong mit dem LookupValue
	 */
	private HashMap<String, HashMap<Integer, LookupValue>> cache = new HashMap<>();

	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String ERROR = "Error";
	private static final String USE_CACHE = "UseCache: ";
	private static final String MESSAGE = "Message";
	private static final String DATA_PROCEDURE = "data/procedure";

	private int minTimeBetweenError = 3;
	Map<String, Long> timeOfLastErrorMessage = new HashMap<>();

	private static final FilterValue fv = new FilterValue(">", "0", "");

	private static final boolean LOG_CACHE = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/cache"));
	private static final boolean LOG_SQL_STRING = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/logsqlstring"));
	private static final boolean DISABLE_FILE_UPDATE = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/disablefileupdate"));

	private HttpClient httpClient;
	private HttpClient.Builder httpClientBuilder;

	private Gson gson;

	private String username = null;// "admin";
	private String password = null;// "rqgzxTf71EAx8chvchMi";
	private URI server = null;// "http://publictest.minova.com:17280/cas";

	private URI workspacePath;

	private Map<String, String> siteParameters = new HashMap<>();

	EventAdmin eventAdmin;

	private IEclipsePreferences preferences;

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MANDATORY)
	void registerEventAdmin(EventAdmin admin) {
		this.eventAdmin = admin;
	}

	void unregisterEventAdmin(EventAdmin admin) {
		this.eventAdmin = null;
	}

	@Override
	public void setCredentials(String username, String password, String server, URI workspacePath) {
		this.username = username;
		this.password = password;
		this.server = URI.create(server.endsWith("/") ? server : server.concat("/"));
		this.workspacePath = workspacePath;
		init();

		// im Falle der Unit tests haben wir keinen bundle context
		if (FrameworkUtil.getBundle(this.getClass()) != null) {
			BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
			// allow to trigger components after the service has been initialized, see WFCTranslationDownloadService#getDummyService
			bundleContext.registerService(IDummyService.class.getName(), new IDummyService(), null);
		}
	}

	private void init() {
		try {
			// Damit Umlaute in Username und Passwort genutzt werden können muss mit ISO_8859_1 encoded werden
			String encodedUser = new String(username.getBytes(), StandardCharsets.ISO_8859_1.toString());
			String encodedPW = new String(password.getBytes(), StandardCharsets.ISO_8859_1.toString());
			Authenticator authentication = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(encodedUser, encodedPW.toCharArray());
				}
			};

			httpClientBuilder = HttpClient.newBuilder()//
					.sslContext(SSLContextUtil.getTrustAllSSLContext())//
					.authenticator(authentication);
			httpClient = httpClientBuilder.build();
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}

		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.registerTypeAdapter(Column.class, new ColumnSerializer()) //
				.setPrettyPrinting() //
				.create();

		preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
	}

	private void initSiteParameters() {
		Table requestTable = TableBuilder.newTable("tSiteParameter") //
				.withColumn(Constants.TABLE_KEYTEXT, DataType.STRING)//
				.withColumn("Value", DataType.STRING)//
				.withColumn(Constants.TABLE_LASTACTION, DataType.INTEGER).create();
		Row row = RowBuilder.newRow() //
				.withValue(null) //
				.withValue(null) //
				.withValue(fv) //
				.create();

		requestTable.addRow(row);
		CompletableFuture<Table> tableCF = getTableAsync(requestTable);

		try {
			Table paramTable = tableCF.get();
			if (paramTable != null) {
				for (Row r : paramTable.getRows()) {
					if (r.getValue(0) != null && r.getValue(1) != null) {
						siteParameters.put(r.getValue(0).getStringValue(), r.getValue(1).getStringValue());
					}
				}
			}
		} catch (ExecutionException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public CompletableFuture<Table> getTableAsync(Table seachTable) {
		return getTableAsync(seachTable, true);
	}

	public CompletableFuture<Table> getTableAsync(Table searchTable, boolean showErrorMessage) {
		String body = gson.toJson(searchTable);

		HttpRequest request = HttpRequest.newBuilder().uri(server.resolve("data/index")) //
				.header(CONTENT_TYPE, APPLICATION_JSON) //
				.method("POST", BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(preferences.getInt(ApplicationPreferences.TIMEOUT_CAS, 15))).build();

		log("CAS Request Table:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""), searchTable, false);

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "Table", showErrorMessage);
			return null;
		});

		return sendRequest.thenApply(t -> {
			log("CAS Answer Table:\n" + t.body());

			if (checkForError(t.body(), searchTable.getName())) {
				return null;
			}

			return gson.fromJson(t.body(), Table.class);
		});
	}

	@Override
	public CompletableFuture<List<TransactionResultEntry>> callTransactionAsync(List<TransactionEntry> procedureList) {
		String body = gson.toJson(procedureList);
		HttpRequest request = HttpRequest.newBuilder().uri(server.resolve("data/x-procedure")) //
				.header(CONTENT_TYPE, APPLICATION_JSON) //
				.POST(BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(preferences.getInt(ApplicationPreferences.TIMEOUT_CAS, 15))).build();

		log("CAS Call Transaction List:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""), procedureList);

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "Call Transaction List", true);
			return null;
		});

		return sendRequest.thenApply(t -> {
			log("CAS Answer Call Transaction List:\n" + t.body());

			if (checkForError(t.body(), procedureList.get(0).getTable().getName())) {
				return null;
			}

			// Auch einzelne Ergebnisse auf Fehler überprüfen
			Type listType = new TypeToken<ArrayList<TransactionResultEntry>>() {}.getType();
			List<TransactionResultEntry> transactionResults = gson.fromJson(t.body(), listType);
			for (TransactionResultEntry entry : transactionResults) {
				SqlProcedureResult entryResult = entry.getSQLProcedureResult();
				String procedureName = entry.getId();
				if (procedureList.size() > transactionResults.indexOf(entry)) {
					procedureName = procedureList.get(transactionResults.indexOf(entry)).getTable().getName();
				}
				ErrorObject e = checkForErrorInSQLResult(entryResult, procedureName);
				if (e != null) {
					postError(e);
					break;
				}
			}
			return transactionResults;
		});
	}

	@Override
	public CompletableFuture<SqlProcedureResult> callProcedureAsync(Table table) {
		return callProcedureAsync(table, true);
	}

	public CompletableFuture<SqlProcedureResult> callProcedureAsync(Table table, boolean showErrorMessage) {
		String body = gson.toJson(table);
		HttpRequest request = HttpRequest.newBuilder().uri(server.resolve(DATA_PROCEDURE)) //
				.header(CONTENT_TYPE, APPLICATION_JSON) //
				.POST(BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(preferences.getInt(ApplicationPreferences.TIMEOUT_CAS, 15))).build();

		log("CAS Call Procedure:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""), table, true);

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "Call Procedure", showErrorMessage);
			return null;
		});

		return sendRequest.thenApply(t -> {
			log("CAS Answer Call Procedure:\n" + t.body());

			if (checkForError(t.body(), table.getName())) {
				return null;
			}

			return gson.fromJson(t.body(), SqlProcedureResult.class);
		});
	}

	/**
	 * Liefert true, wenn ein Fehler enthalten ist. Folgende möglichen Fehlermeldungen werden geprüft: <br>
	 * - leerer/null body <br>
	 * - SqlProcedureResult mit negativen Returncode <br>
	 * - Table mit Namen Error <br>
	 * - Serverantwort mit HTTP-Code >= 300 <br>
	 *
	 * @param body
	 * @param sourceName
	 * @return
	 */
	private boolean checkForError(String body, String sourceName) {

		ErrorObject e = null;

		if (body == null || body.isBlank()) {
			e = new ErrorObject(getDefaultError().getResultSet(), username, sourceName);
		}

		try {
			SqlProcedureResult sqlResult = gson.fromJson(body, SqlProcedureResult.class);
			if (sqlResult != null && sqlResult.getReturnCode() != null) {
				e = checkForErrorInSQLResult(sqlResult, sourceName);
			}
		} catch (JsonSyntaxException ex) {
			// War kein SqlProcedureResult
		}

		try {
			Table fromJson = gson.fromJson(body, Table.class);
			if (fromJson != null && fromJson.getName() != null && fromJson.getName().equals(ERROR)) {
				e = new ErrorObject(fromJson, username, sourceName);
			}
		} catch (JsonSyntaxException ex) {
			// War keine Table
		}

		try {
			ServerAnswer serverAnswer = gson.fromJson(body, ServerAnswer.class);
			if (serverAnswer != null && serverAnswer.getStatus() != 0) {
				e = checkForErrorInServerAnswer(serverAnswer, sourceName);
			}
		} catch (JsonSyntaxException ex) {
			// War keine ServerAnswer
		}

		if (e != null) {
			postError(e);
			return true;
		}
		return false;
	}

	private ErrorObject checkForErrorInServerAnswer(ServerAnswer serverAnswer, String sourceName) {
		if (serverAnswer.getStatus() < 300) {
			return null;
		}

		// HTTP Status >= 300 -> Fehler
		Table error = new Table();
		error.setName(ERROR);
		error.addColumn(new Column(MESSAGE, DataType.STRING));
		String message = "Internal Server Error"; // Default Fehler
		if (serverAnswer.getMessage() != null) {
			message = serverAnswer.getMessage();
		} else if (serverAnswer.getError() != null) {
			message = serverAnswer.getError();
		}
		error.addRow(RowBuilder.newRow().withValue(message).create());

		return new ErrorObject(error, username, sourceName);
	}

	public ErrorObject checkForErrorInSQLResult(SqlProcedureResult fromJson, String procedureName) {
		// Returncode >= 0 -> kein Fehler -> nichts zu tun
		if (fromJson != null && fromJson.getReturnCode() >= 0) {
			return null;
		}

		// fromJson null ist oder kein Resultset: Default Fehlermeldung
		if (fromJson == null || fromJson.getResultSet() == null) {
			fromJson = getDefaultError();
		}

		if (fromJson.getReturnCode() < 0 && fromJson.getResultSet() != null && ERROR.equals(fromJson.getResultSet().getName())) {
			return new ErrorObject(fromJson.getResultSet(), username, procedureName);
		}
		return null;
	}

	private SqlProcedureResult getDefaultError() {
		SqlProcedureResult fromJson;
		Table error = new Table();
		error.setName(ERROR);
		error.addColumn(new Column(MESSAGE, DataType.STRING));
		error.addRow(RowBuilder.newRow().withValue("msg.NoErrorMessageAvailable").create());
		fromJson = new SqlProcedureResult();
		fromJson.setResultSet(error);
		fromJson.setReturnCode(-1);
		return fromJson;
	}

	@Override
	public CompletableFuture<Path> getXMLAsync(Table table, String rootElement) {
		String body = gson.toJson(table);
		HttpRequest request = HttpRequest.newBuilder().uri(server.resolve(DATA_PROCEDURE)) //
				.header(CONTENT_TYPE, APPLICATION_JSON) //
				.POST(BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(preferences.getInt(ApplicationPreferences.TIMEOUT_CAS, 15) * (long) 2)).build();

		Path path = getStoragePath().resolve("reports/" + table.getName() + table.getRows().get(0).getValue(0).getValue().toString() + ".xml");
		try {
			Files.createDirectories(path.getParent());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		Path finalPath = Path.of(FileUtil.createFile(path.toString()));

		log("CAS Request XML Detail:\nPath: " + finalPath + "\n" + request.toString() + "\n" + body.replaceAll("\\s", ""), table, true);

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "XML Detail", true);
			return null;
		});

		return sendRequest.thenApply(t -> {
			log("CAS Answer XML Detail:\nPath: " + finalPath + "\n" + t.body());
			SqlProcedureResult fromJson = gson.fromJson(t.body(), SqlProcedureResult.class);

			if (checkForError(t.body(), table.getName())) {
				return null;
			}

			try {
				FileWriter fw = new FileWriter(finalPath.toFile(), StandardCharsets.UTF_8);
				fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
				fw.write("<" + rootElement + ">");
				for (Row r : fromJson.getResultSet().getRows()) {
					if (r.getValue(0) != null) {
						fw.write(r.getValue(0).getStringValue() + "\n");
					}
				}
				fw.write("</" + rootElement + ">");
				fw.close();
			} catch (IOException e) {
				handleCASError(e, "XML", true, "msg.ErrorShowingFile");
			}
			return finalPath;
		});
	}

	@Override
	public CompletableFuture<String> getHashedFile(String filename) {
		logCache("Requested file: " + filename);
		try {
			if (checkIfUpdateIsRequired(filename)) {
				logCache(filename + " need to download / update the file ");

				// File löschen, damit es komplett aktualisiert wird
				if (getStoragePath().resolve(filename).toFile().exists()) {
					Files.delete(getStoragePath().resolve(filename));
				}

				downloadFile(filename).join();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
		return getCachedFileContent(filename);
	}

	@Override
	public boolean getHashedZip(String zipname) {
		logCache("Requested file: " + zipname);
		try {
			if (checkIfUpdateIsRequired(zipname)) {
				logCache(zipname + " need to download / update the file ");
				downloadFile(zipname).join();
				if (this.getStoragePath().resolve(zipname).toFile().exists()) {
					ZipService.unzipFile(this.getStoragePath().resolve(zipname).toFile(), this.getStoragePath().toString());
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return false;
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
			return false;
		}
		return true;
	}

	@Override
	public CompletableFuture<Path> getPDFAsync(Table table, String fileName) {
		String method = "PDF";
		String body = gson.toJson(table);
		HttpRequest request = HttpRequest.newBuilder().uri(server.resolve(DATA_PROCEDURE)) //
				.header(CONTENT_TYPE, APPLICATION_JSON) //
				.POST(BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(preferences.getInt(ApplicationPreferences.TIMEOUT_CAS, 15) * (long) 2)).build();

		Path path = getStoragePath().resolve(fileName);
		try {
			Files.createDirectories(path.getParent());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		Path finalPath = Path.of(FileUtil.createFile(path.toString()));

		log("CAS Request " + method + ":\n" + request.toString() + "\n" + body.replaceAll("\\s", ""), table, true);

		return downloadWithRequest(fileName, method, request, finalPath);
	}

	/**
	 * synchrones laden einer Datei vom Server.
	 *
	 * @param filename
	 *            relativer Pfad und Dateiname auf dem Server
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Override
	public CompletableFuture<Path> downloadFile(String fileName) throws IOException, InterruptedException {
		String method = "File Async";
		HttpRequest request = HttpRequest.newBuilder().uri(server.resolve("files/read?path=" + fileName))//
				.header(CONTENT_TYPE, APPLICATION_OCTET_STREAM) //
				.timeout(Duration.ofSeconds(preferences.getInt(ApplicationPreferences.TIMEOUT_CAS, 15))).build();
		Path localFile = getStoragePath().resolve(fileName);

		log("CAS Request " + method + ":\n" + request + "\n" + fileName);

		return downloadWithRequest(fileName, method, request, localFile);
	}

	private CompletableFuture<Path> downloadWithRequest(String fileName, String method, HttpRequest request, Path localFile) {
		CompletableFuture<HttpResponse<byte[]>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofByteArray());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, method, true);
			return null;
		});

		return sendRequest.thenApply(t -> {
			if (checkForErrorInByteStream(t, fileName, method)) {
				return null;
			}

			// Bei keinem Fehler Datei erstellen
			writeByteStreamToFile(t, localFile, method);
			return localFile;
		});
	}

	private boolean checkForErrorInByteStream(HttpResponse<byte[]> t, String fileName, String method) {
		// Überprüfen, ob ein Fehler geworfen wurde (dann wird SqlProcedureResult zurückgegeben)
		try {
			String asString = new String(t.body(), StandardCharsets.UTF_8);
			if (checkForError(asString, fileName)) {
				log("CAS Answer " + method + ":\n" + asString);
				return true;
			}
		} catch (Exception e) {
			// Wenn hier ein Exception geworfen wird wurde kein SqlProcedureResult/kein Fehler geliefert, die Daten können gespeichert werden
		}
		return false;
	}

	private void writeByteStreamToFile(HttpResponse<byte[]> t, Path localFile, String method) {
		// Ansonsten byteArray in File schreiben
		try (OutputStream out = new FileOutputStream(localFile.toString())) {
			log("CAS Answer " + method + ":\n" + localFile);
			out.write(t.body());
		} catch (IOException e) {
			handleCASError(e, "File Sync", true, "msg.ErrorShowingFile");
		}
	}

	@Override
	public boolean checkIfUpdateIsRequired(String fileName) throws IOException, InterruptedException {
		File file = getStoragePath().resolve(fileName).toFile();
		if (!file.exists()) { // Wenn es das file nicht gibt muss es immer geladen werden
			return true;
		}

		if (DISABLE_FILE_UPDATE) { // Wenn diese Option gesetzt ist sollen Files NIE geupdated werden
			return false;
		}

		if (preferences.getBoolean(ApplicationPreferences.DISABLE_FILE_CACHE, false)) { // Wenn diese Option gesetzt ist sollen Files IMMER geupdated werden
			return true;
		}

		String serverHash;
		if (serverHashes.containsKey(fileName)) {
			serverHash = serverHashes.get(fileName);
		} else {
			CompletableFuture<String> serverHashForFile = getServerHashForFile(fileName);
			serverHash = serverHashForFile.join();
			serverHashes.put(fileName, serverHash);
		}

		CompletableFuture<String> localHashForFile = FileUtil.getLocalHashForFile(file);
		String localHash = localHashForFile.join();
		return (!localHash.equals(serverHash));
	}

	/**
	 * Only public for the integration tests
	 *
	 * @param filename
	 * @return
	 */
	public CompletableFuture<String> getServerHashForFile(String filename) {
		HttpRequest request = HttpRequest.newBuilder().uri(server.resolve("files/hash?path=" + filename))//
				.header(CONTENT_TYPE, APPLICATION_OCTET_STREAM) //
				.timeout(Duration.ofSeconds(preferences.getInt(ApplicationPreferences.TIMEOUT_CAS, 15))).build();

		log("CAS Request Server Hash for File:\n" + request + "\n" + filename);

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "Server Hash for File", false);
			return null;
		});

		return sendRequest.thenApply(response -> {
			log("CAS Answer Server Hash for File:\n" + response.body());
			if (checkForError(response.body(), filename)) {
				return null;
			}

			return response.body();
		});
	}

	@Override
	public HttpClient.Builder getHttpClientBuilder() {
		return httpClientBuilder;
	}

	@Override
	public URI getServer() {
		return server;
	}

	@Override
	public CompletableFuture<String> getCachedFileContent(String filename) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				File cachedFile = new File(new URI(workspacePath + filename));
				if (cachedFile.exists()) {
					return Files.readString(cachedFile.toPath());
				}
			} catch (IOException | URISyntaxException e) {
				logger.error(e.getMessage(), e);
			}
			throw new CompletionException("File not found", null);
		});
	}

	@Override
	public Path getStoragePath() {
		return Path.of(workspacePath);
	}

	@Override
	public CompletableFuture<List<LookupValue>> resolveLookup(MLookupField field, boolean useCache, Integer keyLong, String keyText) {
		if (field.getLookupTable() != null) {
			return getLookupValuesFromTable(field.getLookupTable(), field.getLookupDescription(), keyLong, keyText, true, useCache);
		} else {
			String procedureName = field.getLookupProcedurePrefix() + "Resolve";
			return getLookupValuesFromProcedure(procedureName, field, keyLong, keyText, true, useCache);
		}
	}

	@Override
	public CompletableFuture<List<LookupValue>> resolveGridLookup(String tableName, boolean useCache) {
		return getLookupValuesFromTable(tableName, Constants.TABLE_DESCRIPTION, null, null, true, useCache);
	}

	@Override
	public CompletableFuture<List<LookupValue>> listLookup(MLookupField field, boolean useCache) {
		boolean useCacheLookup = false; // siehe #707
		if (field.getLookupTable() != null) {
			return getLookupValuesFromTable(field.getLookupTable(), field.getLookupDescription(), null, null, false, useCacheLookup);
		} else {
			String procedureName = field.getLookupProcedurePrefix() + "List";
			return getLookupValuesFromProcedure(procedureName, field, null, null, false, useCacheLookup);
		}
	}

	private CompletableFuture<List<LookupValue>> getLookupValuesFromTable(String tableName, String lookupDescriptionColumnName, Integer keyLong, String keyText,
			boolean resolve, boolean useCache) {
		List<LookupValue> list = new ArrayList<>();
		HashMap<Integer, LookupValue> map = cache.computeIfAbsent(tableName, k -> new HashMap<>());

		if (useCache && !resolve && !map.isEmpty()) {
			logCache(USE_CACHE + tableName);
			list.addAll(map.values());
			return CompletableFuture.supplyAsync(() -> list);
		} else if (useCache && map.containsKey(keyLong)) {
			logCache(USE_CACHE + tableName + " (" + keyLong + ")");
			list.add(map.get(keyLong));
			return CompletableFuture.supplyAsync(() -> list);
		}

		Table t = TableBuilder.newTable(tableName) //
				.withColumn(Constants.TABLE_KEYLONG, DataType.INTEGER)//
				.withColumn(Constants.TABLE_KEYTEXT, DataType.STRING)//
				.withColumn(lookupDescriptionColumnName, DataType.STRING)//
				.withColumn(Constants.TABLE_LASTACTION, DataType.INTEGER)//
				.create();
		Row row = RowBuilder.newRow() //
				.withValue(keyLong) //
				.withValue(keyText) //
				.withValue(null) //
				.withValue(fv) //
				.create();
		t.addRow(row);

		CompletableFuture<Table> tableFuture = getTableAsync(t, false);

		tableFuture.exceptionally(e -> {
			handleLookupError(tableName, list, map, e);
			return null;
		});

		return tableFuture.thenApplyAsync(ta -> {
			try {
				if (ta != null) {
					for (Row r : ta.getRows()) {
						LookupValue lv = new LookupValue(//
								r.getValue(0).getIntegerValue(), //
								r.getValue(1).getStringValue(), //
								r.getValue(2) == null ? null : r.getValue(2).getStringValue());

						map.put(lv.keyLong, lv);
						list.add(lv);
					}
				}
			} catch (Exception e) {
				handleLookupError(tableName, list, map, e);
			}
			return list;
		});
	}

	private void handleLookupError(String tableOrProcedureName, List<LookupValue> list, HashMap<Integer, LookupValue> map, Throwable e) {
		logCache("Error, using cache: " + tableOrProcedureName);
		postError(new ErrorObject("msg.WFCNoResponseServerUsingCache", username, e));
		list.addAll(map.values());
	}

	private CompletableFuture<List<LookupValue>> getLookupValuesFromProcedure(String procedureName, MField field, Integer keyLong, String keyText,
			boolean resolve, boolean useCache) {
		List<LookupValue> list = new ArrayList<>();
		String hashName = resolve ? procedureName : CacheUtil.getNameList(field);
		HashMap<Integer, LookupValue> map = cache.computeIfAbsent(hashName, k -> new HashMap<>());

		if (useCache && !resolve && !map.isEmpty()) {
			logCache(USE_CACHE + hashName);
			list.addAll(map.values());
			return CompletableFuture.supplyAsync(() -> list);
		} else if (useCache && map.containsKey(keyLong)) {
			logCache(USE_CACHE + hashName + " (" + keyLong + ")");
			list.add(map.get(keyLong));
			return CompletableFuture.supplyAsync(() -> list);
		}

		Table t = getLookupValuesFromProcedureTable(procedureName, field, keyLong, keyText, resolve);

		CompletableFuture<SqlProcedureResult> tableFuture = callProcedureAsync(t, false);

		tableFuture.exceptionally(e -> {
			handleLookupError(procedureName, list, map, e);
			return null;
		});

		return tableFuture.thenApplyAsync(res -> handleLookupFromProcedureResponse(procedureName, list, map, res));
	}

	private Table getLookupValuesFromProcedureTable(String procedureName, MField field, Integer keyLong, String keyText, boolean resolve) {
		Table t = TableBuilder.newTable(procedureName).create();
		Row row = RowBuilder.newRow().create();

		if (resolve) {
			t.addColumn(new Column(Constants.TABLE_KEYLONG, DataType.INTEGER));
			row.addValue(new Value(keyLong));

			t.addColumn(new Column(Constants.TABLE_KEYTEXT, DataType.STRING));
			row.addValue(new Value(keyText));

			t.addColumn(new Column(Constants.TABLE_FILTERLASTACTION, DataType.BOOLEAN));
			row.addValue(new Value(true));

			if (field.getLookupParameters() != null && field.isUseResolveParms()) {
				for (String paramName : field.getLookupParameters()) {
					MField paramField = field.getDetail().getField(paramName);
					t.addColumn(new Column(paramName, paramField.getDataType()));
					row.addValue(paramField.getValue());
				}
			}
		} else {
			t.addColumn(new Column(Constants.TABLE_COUNT, DataType.INTEGER));
			row.addValue(null);
			t.addColumn(new Column(Constants.TABLE_FILTERLASTACTION, DataType.BOOLEAN));
			row.addValue(new Value(true));
			if (field.getLookupParameters() != null) {
				for (String paramName : field.getLookupParameters()) {
					MField paramField = field.getDetail().getField(paramName);
					t.addColumn(new Column(paramName, paramField.getDataType()));
					row.addValue(paramField.getValue());
				}
			}
		}

		t.addRow(row);
		return t;
	}

	private List<LookupValue> handleLookupFromProcedureResponse(String procedureName, List<LookupValue> list, HashMap<Integer, LookupValue> map,
			SqlProcedureResult res) {
		try {
			if (res != null) {
				Table ta = res.getResultSet();
				if (ta != null) {
					for (Row r : ta.getRows()) {
						LookupValue lv = new LookupValue(//
								r.getValue(0).getIntegerValue(), //
								r.getValue(1).getStringValue(), //
								r.getValue(2) == null ? null : r.getValue(2).getStringValue());
						map.put(lv.keyLong, lv);
						list.add(lv);
					}
				}
			}
		} catch (Exception e) {
			handleLookupError(procedureName, list, map, e);
		}
		return list;
	}

	@Override
	public CompletableFuture<Value> getSQLValue(String tablename, String requestColumn, Value requestValue, String resultColumn, DataType resultType) {

		Table t = TableBuilder.newTable(tablename) //
				.withColumn(requestColumn, requestValue.getType())//
				.withColumn(resultColumn, resultType)//
				.create();
		Row row = RowBuilder.newRow() //
				.withValue(requestValue) //
				.withValue(null) //
				.create();
		t.addRow(row);

		CompletableFuture<Table> tableFuture = getTableAsync(t, false);

		return tableFuture.thenApply(ta -> {
			Value v = null;
			if (ta != null) {
				for (Row r : ta.getRows()) {
					v = r.getValue(1);
				}
			}
			return v;
		});
	}

	@Override
	public String getUserName() {
		return username;
	}

	@Override
	public void sendLogs() {
		try {
			ZipService.zipFile(getStoragePath().resolve(".metadata").toString(), getStoragePath().resolve("logs.zip").toString());

			// Kein Timeout, da Upload länger dauer kann
			HttpRequest request = HttpRequest.newBuilder().uri(server.resolve("upload/logs"))//
					.header(CONTENT_TYPE, APPLICATION_OCTET_STREAM)//
					.POST(BodyPublishers.ofByteArray(Files.readAllBytes(getStoragePath().resolve("logs.zip"))))//
					.build();

			log("CAS Request Send Logs:\n" + request);

			CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

			sendRequest.exceptionally(ex -> {
				handleCASError(ex, "Send Logs", true);
				return null;
			});

			sendRequest.thenApply(response -> {
				log("CAS Answer Send Logs: " + response.statusCode() + " " + response.body());
				if (response.statusCode() != 200) { // Fehlermeldung anzeigen
					Table error = new Table();
					error.setName(ERROR);
					error.addColumn(new Column(MESSAGE, DataType.STRING));
					error.addRow(RowBuilder.newRow().withValue(response.body()).create());
					ErrorObject eo = new ErrorObject(error, username, "sendingLogs");
					postError(eo);
					return null;
				}

				postNotification("msg.UploadSuccess");
				return response;
			});

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public String getSiteParameter(String key, String defaultVal) {
		if (siteParameters.containsKey(key)) {
			return siteParameters.get(key);
		}

		// Die SiteParameter abrufen, wenn Wert nicht gefunden wurde
		initSiteParameters();
		if (siteParameters.containsKey(key)) {
			return siteParameters.get(key);
		}

		// Notification, dass Default-Wert genutzt wird
		Table t = TableBuilder.newTable("Notification").withColumn(MESSAGE, DataType.STRING)//
				.withColumn("s", DataType.STRING)//
				.withColumn("s", DataType.STRING).create();
		Row r = RowBuilder.newRow().withValue("msg.UsingDefaultTSiteParameterValue").withValue(key).withValue(defaultVal).create();
		t.addRow(r);
		ErrorObject eo = new ErrorObject(t, username);
		postNotification(eo);

		return defaultVal;
	}

	private void handleCASError(Throwable ex, String method, boolean showErrorMessage) {
		handleCASError(ex, method, showErrorMessage, "msg.WFCNoResponseServer");
	}

	private void handleCASError(Throwable ex, String method, boolean showErrorMessage, String message) {
		log("CAS Error " + method + ":\n" + ex.getMessage());
		if (showErrorMessage) {
			postError(new ErrorObject(message, username, ex));
		}
	}

	/**
	 * Zeigt eine Nachricht als Notification an. Die Nachricht kann ein String oder ein ErrorObject sein
	 *
	 * @param message
	 */
	public void postNotification(Object message) {
		Map<String, Object> data = new HashMap<>(2);
		data.put(EventConstants.EVENT_TOPIC, Constants.BROKER_SHOWNOTIFICATION);
		data.put(IEventBroker.DATA, message);
		Event event = new Event(Constants.BROKER_SHOWNOTIFICATION, data);
		eventAdmin.postEvent(event);
	}

	public void postError(ErrorObject value) {
		// Selbe Fehlermeldung höchstens alle minTimeBetweenError Sekunden anzeigen
		if ((System.currentTimeMillis() - timeOfLastErrorMessage.getOrDefault(value.getMessage(), (long) -1)) > minTimeBetweenError * 1000) {
			Map<String, Object> data = new HashMap<>(2);
			data.put(EventConstants.EVENT_TOPIC, Constants.BROKER_SHOWERROR);
			data.put(IEventBroker.DATA, value);
			Event event = new Event(Constants.BROKER_SHOWERROR, data);
			eventAdmin.postEvent(event);

			timeOfLastErrorMessage.put(value.getMessage(), System.currentTimeMillis());

			log("CAS Error:\n" + value.getErrorTable().getRows().get(0).getValue(0).getStringValue() + "\nUser: " + value.getUser() + "\nProcedure/View: "
					+ value.getProcedureOrView());
		}
	}

	private void logCache(String message) {
		if (LOG_CACHE) {
			logger.info(message);
		}
	}

	private void log(String body, Table table, boolean procedure) {
		String sqlString = "";
		try {
			if (procedure) {
				sqlString = SQLStringUtil.prepareProcedureString(table);
			} else {
				sqlString = SQLStringUtil.prepareViewString(table);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		if (LOG_SQL_STRING) {
			body = body + "\n" + sqlString;
		}
		log(body);
	}

	private void log(String body, List<TransactionEntry> procedureList) {
		if (LOG_SQL_STRING) {
			StringBuilder sqlStringBuilder = new StringBuilder();
			try {
				for (TransactionEntry e : procedureList) {
					sqlStringBuilder.append(SQLStringUtil.prepareProcedureString(e.getTable()) + "\n");
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			String sqlString = sqlStringBuilder.toString().strip();
			body = body + "\n" + sqlString;
		}
		log(body);
	}

	private void log(String body) {
		if (logger != null) {
			logger.info(body);
		}
	}
}
