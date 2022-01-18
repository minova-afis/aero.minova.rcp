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
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
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

@Component
public class DataService implements IDataService {

	Logger logger;

	HashMap<String, String> serverHashes = new HashMap<>();

	/**
	 * Je Prozedur bzw. Tabellenneame gibt es einen Cache je KeyLong mit dem LookupValue
	 */
	private HashMap<String, HashMap<Integer, LookupValue>> cache = new HashMap<>();

	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
	private static final String CONTENT_TYPE = "Content-Type";
	public static final String TABLE_KEYTEXT = "KeyText";
	public static final String TABLE_KEYLONG = "KeyLong";
	public static final String TABLE_DESCRIPTION = "Description";
	public static final String TABLE_LASTACTION = "LastAction";
	public static final String TABLE_COUNT = "Count";
	public static final String TABLE_FILTERLASTACTION = "FilterLastAction";
	public static final String ERROR = "Error";

	private static int timeoutDuration = 15;
	private static int timeoutDurationOpenNotification = 1;
	private int minTimeBetweenError = 3;
	long timeOfLastConnectionErrorMessage = -1;

	private static final FilterValue fv = new FilterValue(">", "0", "");

	private static final boolean LOG = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/server"));
	private static final boolean LOG_CACHE = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/cache"));
	private static final boolean LOG_SQL_STRING = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/logsqlstring"));
	private static final boolean DISABLE_FILE_UPDATE = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/disablefileupdate"));

	private HttpClient httpClient;
	private Gson gson;

	private String username = null;// "admin";
	private String password = null;// "rqgzxTf71EAx8chvchMi";
	private String server = null;// "http://publictest.minova.com:17280/cas";

	private URI workspacePath;

	private Map<String, String> siteParameters;

	EventAdmin eventAdmin;

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
		this.server = server;
		this.workspacePath = workspacePath;
		init();
		initSiteParameters();

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
			// TODO: fix certificate-problems
			httpClient = HttpClient.newBuilder()//
					.sslContext(disabledSslVerificationContext())//
					.authenticator(authentication).build();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.registerTypeAdapter(Column.class, new ColumnSerializer()) //
				.setPrettyPrinting() //
				.create();
	}

	private void initSiteParameters() {
		siteParameters = new HashMap<>();
		Table requestTable = TableBuilder.newTable("tSiteParameter") //
				.withColumn(TABLE_KEYTEXT, DataType.STRING)//
				.withColumn("Value", DataType.STRING)//
				.withColumn(TABLE_LASTACTION, DataType.INTEGER).create();
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
		} catch (InterruptedException | ExecutionException e) {}
	}

	private static SSLContext disabledSslVerificationContext() {
		SSLContext sslContext = null;// Remove certificate validation

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
		} };

		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, new SecureRandom());
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
		return sslContext;
	}

	@Override
	public CompletableFuture<Table> getTableAsync(Table seachTable) {
		return getTableAsync(seachTable, true);
	}

	public CompletableFuture<Table> getTableAsync(Table searchTable, boolean showErrorMessage) {
		String body = gson.toJson(searchTable);

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/index")) //
				.header(CONTENT_TYPE, "application/json") //
				.method("GET", BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(timeoutDuration)).build();

		log("CAS Request Table:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""), searchTable, false);

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "Table", showErrorMessage);
			return null;
		});

		return sendRequest.thenApply(t -> {
			Table fromJson = gson.fromJson(t.body(), Table.class);
			if (fromJson.getName().equals(ERROR)) {
				ErrorObject e = new ErrorObject(fromJson, username, searchTable.getName());
				postError(e);
				return null;
			}
			log("CAS Answer Table:\n" + t.body());
			return fromJson;
		});
	}

	@Override
	public CompletableFuture<List<TransactionResultEntry>> callTransactionAsync(List<TransactionEntry> procedureList) {
		String body = gson.toJson(procedureList);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/x-procedure")) //
				.header(CONTENT_TYPE, "application/json") //
				.POST(BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(timeoutDuration)).build();

		log("CAS Call Transaction List:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""), procedureList);

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "Call Transaction List", true);
			return null;
		});

		return sendRequest.thenApply(t -> {
			log("CAS Answer Call Transaction List:\n" + t.body());
			Type listType = new TypeToken<ArrayList<TransactionResultEntry>>() {}.getType();

			List<TransactionResultEntry> transactionResults = gson.fromJson(t.body(), listType);

			for (TransactionResultEntry entry : transactionResults) {
				SqlProcedureResult entryResult = entry.getSQLProcedureResult();
				entryResult = checkProcedureResult(entryResult, gson.toJson(entryResult), entry.getId());
				if (entryResult == null) {
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
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/procedure")) //
				.header(CONTENT_TYPE, "application/json") //
				.POST(BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(timeoutDuration)).build();

		log("CAS Call Procedure:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""), table, true);

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "Call Procedure", showErrorMessage);
			return null;
		});

		return sendRequest.thenApply(t -> {
			log("CAS Answer Call Procedure:\n" + t.body());
			SqlProcedureResult fromJson = gson.fromJson(t.body(), SqlProcedureResult.class);
			return checkProcedureResult(fromJson, t.body(), table.getName());
		});
	}

	public SqlProcedureResult checkProcedureResult(SqlProcedureResult fromJson, String originalBody, String procedureName) {
		ErrorObject e = checkForError(fromJson, procedureName);
		if (e != null) {
			postError(e);
			return null;
		}
		return fromJson;
	}

	public ErrorObject checkForError(SqlProcedureResult fromJson, String procedureName) {
		// Returncode >= null -> kein Fehler -> nichts zu tun
		if (fromJson != null && fromJson.getReturnCode() >= 0) {
			return null;
		}

		// fromJson null ist oder kein Resultset: Default Fehlermeldung
		if (fromJson == null || fromJson.getResultSet() == null) {
			Table error = new Table();
			error.setName(ERROR);
			error.addColumn(new Column("Message", DataType.STRING));
			error.addRow(RowBuilder.newRow().withValue("msg.NoErrorMessageAvailable").create());
			fromJson = new SqlProcedureResult();
			fromJson.setResultSet(error);
			fromJson.setReturnCode(-1);
		}

		if (fromJson.getReturnCode() < 0 && fromJson.getResultSet() != null && ERROR.equals(fromJson.getResultSet().getName())) {
			return new ErrorObject(fromJson.getResultSet(), username, procedureName);
		}
		return null;
	}

	@Override
	public CompletableFuture<Path> getXMLAsync(Table table, String rootElement) {
		String body = gson.toJson(table);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/procedure")) //
				.header(CONTENT_TYPE, "application/json") //
				.POST(BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(timeoutDuration * 2)).build();

		Path path = getStoragePath().resolve("reports/" + table.getName() + table.getRows().get(0).getValue(0).getValue().toString() + ".xml");
		try {
			Files.createDirectories(path.getParent());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Path finalPath = Path.of(FileUtil.createFile(path.toString()));

		log("CAS Request XML Detail:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""), table, true);

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "XML Detail", true);
			return null;
		});

		return sendRequest.thenApply(t -> {
			log("CAS Answer XML Detail:\n" + t.body());
			SqlProcedureResult fromJson = gson.fromJson(t.body(), SqlProcedureResult.class);

			fromJson = checkProcedureResult(fromJson, t.body(), table.getName());
			if (fromJson == null) {
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
	public CompletableFuture<Path> getPDFAsync(Table table, String fileName) {
		String body = gson.toJson(table);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/procedure")) //
				.header(CONTENT_TYPE, "application/json") //
				.POST(BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(timeoutDuration * 2)).build();

		Path path = getStoragePath().resolve(fileName);

		try {
			Files.createDirectories(path.getParent());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Path finalPath = Path.of(FileUtil.createFile(path.toString()));

		log("CAS Request PDF:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""), table, true);

		CompletableFuture<HttpResponse<byte[]>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofByteArray());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "PDF", true);
			return null;
		});

		return sendRequest.thenApply(t -> {

			// Überprüfen, ob ein Fehler geworfen wurde (dann wird SqlProcedureResult zurückgegeben)
			try {
				String asString = new String(t.body(), StandardCharsets.UTF_8);
				SqlProcedureResult fromJson = gson.fromJson(asString, SqlProcedureResult.class);
				log("CAS Answer PDF:\n" + asString);
				fromJson = checkProcedureResult(fromJson, asString, table.getName());
				if (fromJson == null) {
					return null;
				}
			} catch (Exception e) {
				// Wenn hier ein Exception geworfen wird wurde kein SqlProcedureResult/kein Fehler geliefert, die Daten können gespeichert werden
			}

			// Ansonsten byteArray in File schreiben
			try {
				OutputStream out = new FileOutputStream(finalPath.toString());
				out.write(t.body());
				out.close();
				log("CAS Answer PDF:\n" + finalPath);
			} catch (IOException e) {
				handleCASError(e, "PDF", true, "msg.ErrorShowingFile");
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
				getStoragePath().resolve(filename).toFile().delete();
				downloadFile(filename);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return getCachedFileContent(filename);
	}

	@Override
	public boolean getHashedZip(String zipname) {
		logCache("Requested file: " + zipname);
		try {
			if (checkIfUpdateIsRequired(zipname)) {
				logCache(zipname + " need to download / update the file ");
				downloadFile(zipname);
				if (this.getStoragePath().resolve(zipname).toFile().exists()) {
					ZipService.unzipFile(this.getStoragePath().resolve(zipname).toFile(), this.getStoragePath().toString());
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
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
	public void downloadFile(String fileName) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/files/read?path=" + fileName))//
				.header(CONTENT_TYPE, APPLICATION_OCTET_STREAM) //
				.timeout(Duration.ofSeconds(timeoutDuration)).build();
		log("CAS Request File Sync:\n" + request + "\n" + fileName);
		Path localFile = getStoragePath().resolve(fileName);

		try {
			httpClient.send(request, BodyHandlers.ofFile(localFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
		} catch (Exception e) {
			handleCASError(e, "File Sync", false);
		}
	}

	@Override
	public boolean checkIfUpdateIsRequired(String fileName) throws IOException, InterruptedException {
		File file = getStoragePath().resolve(fileName).toFile();
		if (!file.exists()) { // Wenn es das file nicht gibt muss es immer geladen werden
			return true;
		}

		if (DISABLE_FILE_UPDATE) { // Wenn diese Option gesetzt ist sollen Files nicht geupdated werden
			return false;
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
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/files/hash?path=" + filename))//
				.header(CONTENT_TYPE, APPLICATION_OCTET_STREAM) //
				.timeout(Duration.ofSeconds(timeoutDuration)).build();

		log("CAS Request Server Hash for File:\n" + request + "\n" + filename);

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "Server Hash for File", false);
			return null;
		});

		return sendRequest.thenApply(response -> {
			log("CAS Answer Server Hash for File:\n" + response.body());
			if (response.statusCode() != 200) {
				throw new RuntimeException("Server returned " + response.statusCode());
			}
			return response;
		}).thenApply(HttpResponse::body);
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
				e.printStackTrace();
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
		return getLookupValuesFromTable(tableName, TABLE_DESCRIPTION, null, null, true, useCache);
	}

	@Override
	public CompletableFuture<List<LookupValue>> listLookup(MLookupField field, boolean useCache) {
		useCache = false;
		if (field.getLookupTable() != null) {
			return getLookupValuesFromTable(field.getLookupTable(), field.getLookupDescription(), null, null, false, useCache);
		} else {
			String procedureName = field.getLookupProcedurePrefix() + "List";
			return getLookupValuesFromProcedure(procedureName, field, null, null, false, useCache);
		}
	}

	private CompletableFuture<List<LookupValue>> getLookupValuesFromTable(String tableName, String lookupDescriptionColumnName, Integer keyLong, String keyText,
			boolean resolve, boolean useCache) {
		List<LookupValue> list = new ArrayList<>();
		HashMap<Integer, LookupValue> map = cache.computeIfAbsent(tableName, k -> new HashMap<>());

		if (useCache && !resolve && !map.isEmpty()) {
			System.out.println("UseCache: " + tableName);
			list.addAll(map.values());
			return CompletableFuture.supplyAsync(() -> list);
		} else if (useCache && map.containsKey(keyLong)) {
			System.out.println("UseCache: " + tableName + " (" + keyLong + ")");
			list.add(map.get(keyLong));
			return CompletableFuture.supplyAsync(() -> list);
		}

		Table t = TableBuilder.newTable(tableName) //
				.withColumn(TABLE_KEYLONG, DataType.INTEGER)//
				.withColumn(TABLE_KEYTEXT, DataType.STRING)//
				.withColumn(lookupDescriptionColumnName, DataType.STRING)//
				.withColumn(TABLE_LASTACTION, DataType.INTEGER)//
				.create();
		Row row = RowBuilder.newRow() //
				.withValue(keyLong) //
				.withValue(keyText) //
				.withValue(null) //
				.withValue(fv) //
				.create();
		t.addRow(row);

		CompletableFuture<Table> tableFuture = getTableAsync(t, false);
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
				System.out.println("Error, using cache: " + tableName);
				showNoResposeServerError("msg.WFCNoResponseServerUsingCache", e);
				list.addAll(map.values());
			}
			return list;
		});
	}

	private CompletableFuture<List<LookupValue>> getLookupValuesFromProcedure(String procedureName, MField field, Integer keyLong, String keyText,
			boolean resolve, boolean useCache) {
		List<LookupValue> list = new ArrayList<>();
		String hashName = resolve ? procedureName : CacheUtil.getNameList(field);
		HashMap<Integer, LookupValue> map = cache.computeIfAbsent(hashName, k -> new HashMap<>());

		if (useCache && !resolve && !map.isEmpty()) {
			System.out.println("UseCache: " + hashName);
			list.addAll(map.values());
			return CompletableFuture.supplyAsync(() -> list);
		} else if (useCache && map.containsKey(keyLong)) {
			System.out.println("UseCache: " + hashName + " (" + keyLong + ")");
			list.add(map.get(keyLong));
			return CompletableFuture.supplyAsync(() -> list);
		}

		Table t = TableBuilder.newTable(procedureName).create();
		Row row = RowBuilder.newRow().create();

		if (resolve) {
			t.addColumn(new Column(TABLE_KEYLONG, DataType.INTEGER));
			row.addValue(new Value(keyLong));

			t.addColumn(new Column(TABLE_KEYTEXT, DataType.STRING));
			row.addValue(new Value(keyText));

			t.addColumn(new Column(TABLE_FILTERLASTACTION, DataType.BOOLEAN));
			row.addValue(new Value(true));
		} else {
			t.addColumn(new Column(TABLE_COUNT, DataType.INTEGER));
			row.addValue(null);
			t.addColumn(new Column(TABLE_FILTERLASTACTION, DataType.BOOLEAN));
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

		CompletableFuture<SqlProcedureResult> tableFuture = callProcedureAsync(t, false);
		return tableFuture.thenApplyAsync(res -> {
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
				System.out.println("Error, using Cache: " + hashName);
				showNoResposeServerError("msg.WFCNoResponseServerUsingCache", e);
				list.addAll(map.values());
			}
			return list;
		});
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
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void setTimeout(int timeout) {
		timeoutDuration = timeout;
	}

	@Override
	public void setTimeoutOpenNotification(int timeoutOpen) {
		timeoutDurationOpenNotification = timeoutOpen;
	}

	@Override
	public void sendLogs() {
		try {
			ZipService.zipFile(getStoragePath().resolve(".metadata").toString(), getStoragePath().resolve("logs.zip").toString());

			// Kein Timeout, da Upload länger dauer kann
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/upload/logs"))//
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
					error.addColumn(new Column("Message", DataType.STRING));
					error.addRow(RowBuilder.newRow().withValue(response.body()).create());
					ErrorObject eo = new ErrorObject(error, username, "sendingLogs");
					postError(eo);
					return null;
				}

				postNotification("msg.UploadSuccess");
				return response;
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getSiteParameter(String key, String defaultVal) {
		if (siteParameters.containsKey(key)) {
			return siteParameters.get(key);
		}

		// Nochmal versuchen, die SiteParameter abzurufen, wenn Wert nicht gefunden wurde
		initSiteParameters();
		if (siteParameters.containsKey(key)) {
			return siteParameters.get(key);
		}

		// Notification, dass Default-Wert genutzt wird
		Table t = TableBuilder.newTable("Notification").withColumn("Message", DataType.STRING)//
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
			showNoResposeServerError(message, ex);
		}
	}

	public void postError(ErrorObject value) {
		Dictionary<String, Object> data = new Hashtable<>(2);
		data.put(EventConstants.EVENT_TOPIC, Constants.BROKER_SHOWERROR);
		data.put(IEventBroker.DATA, value);
		Event event = new Event(Constants.BROKER_SHOWERROR, data);
		eventAdmin.postEvent(event);

		log("CAS error:\n" + value.getErrorTable().getRows().get(0).getValue(0).getStringValue() + "\nUser: " + value.getUser() + "\nProcedure/View: "
				+ value.getProcedureOrView());
	}

	/**
	 * Zeigt eine Nachricht als Notification an. Die Nachricht kann ein String oder ein ErrorObject sein
	 * 
	 * @param message
	 */
	public void postNotification(Object message) {
		Dictionary<String, Object> data = new Hashtable<>(2);
		data.put(EventConstants.EVENT_TOPIC, Constants.BROKER_SHOWNOTIFICATION);
		data.put(IEventBroker.DATA, message);
		Event event = new Event(Constants.BROKER_SHOWNOTIFICATION, data);
		eventAdmin.postEvent(event);
	}

	public void showNoResposeServerError(String message, Throwable th) {
		// Fehlermeldung höchstens alle minTimeBetweenError Sekunden anzeigen
		if ((System.currentTimeMillis() - timeOfLastConnectionErrorMessage) > minTimeBetweenError * 1000) {
			Dictionary<String, Object> data = new Hashtable<>(2);
			data.put(EventConstants.EVENT_TOPIC, Constants.BROKER_SHOWCONNECTIONERRORMESSAGE);
			data.put(IEventBroker.DATA, new ErrorObject(message, username, th));
			Event event = new Event(Constants.BROKER_SHOWCONNECTIONERRORMESSAGE, data);
			eventAdmin.postEvent(event);
			timeOfLastConnectionErrorMessage = System.currentTimeMillis();
		}
	}

	private void logCache(String message) {
		if (LOG_CACHE) {
			System.out.println(message);
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
			e.printStackTrace();
		}

		if (LOG_SQL_STRING) {
			body = body + "\n" + sqlString;
		}
		log(body);
	}

	private void log(String body, List<TransactionEntry> procedureList) {
		String sqlString = "";
		try {
			for (TransactionEntry e : procedureList) {
				sqlString += SQLStringUtil.prepareProcedureString(e.getTable()) + "\n";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		sqlString = sqlString.strip();

		if (LOG_SQL_STRING) {
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
