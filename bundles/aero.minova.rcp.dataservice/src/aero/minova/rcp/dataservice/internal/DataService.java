package aero.minova.rcp.dataservice.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.HashService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.IDummyService;
import aero.minova.rcp.dataservice.ZipService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.FilterValue;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.util.ErrorObject;

@Component
public class DataService implements IDataService {

	EventAdmin eventAdmin;

	Logger logger;

	HashMap<String, String> serverHashes = new HashMap<>();

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MANDATORY)
	void registerEventAdmin(EventAdmin admin) {
		this.eventAdmin = admin;
	}

	void unregisterEventAdmin(EventAdmin admin) {
		this.eventAdmin = null;
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

	public void postNotification(String message) {
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

	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
	private static final String CONTENT_TYPE = "Content-Type";
	public static final String TABLE_KEYTEXT = "KeyText";
	public static final String TABLE_KEYLONG = "KeyLong";
	public static final String TABLE_DESCRIPTION = "Description";
	public static final String TABLE_LASTACTION = "LastAction";
	public static final String TABLE_COUNT = "Count";
	public static final String TABLE_FILTERLASTACTION = "FilterLastAction";

	private static int timeoutDuration = 15;
	private static int timeoutDurationOpenNotification = 1;
	private int minTimeBetweenError = 3;
	long timeOfLastConnectionErrorMessage = -1;

	private static final FilterValue fv = new FilterValue(">", "0", "");

	private static final boolean LOG = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/server"));
	private static final boolean LOG_CACHE = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/cache"));

	private HttpClient httpClient;
	private Gson gson;

	private String username = null;// "admin";
	private String password = null;// "rqgzxTf71EAx8chvchMi";
	// Dies ist unser üblicher Server, von welchen wir unsere Daten abfragen
	private String server = null;// "http://publictest.minova.com:17280/cas";

	// Dies ist der Server, auf welchen wir derzeit zugreifen müssen, um die
	// Ticket-Anfragen zu versenden
	// private String server = "https://mintest.minova.com:8084";

	private URI workspacePath;

	@Override
	public void setCredentials(String username, String password, String server, URI workspacePath) {
		this.username = username;
		this.password = password;
		this.server = server;
		this.workspacePath = workspacePath;
		init();

		// im Falle der Unit tests haben wir keinen bundle context
		if (FrameworkUtil.getBundle(this.getClass()) != null) {
			BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
			// allow to trigger components after the service has been initialized, see
			// WFCTranslationDownloadService#getDummyService
			bundleContext.registerService(IDummyService.class.getName(), new IDummyService(), null);

		}
	}

	/**
	 * Erstellt eine Datei falls sie existiert, wird sie geleert.
	 *
	 * @param path
	 */
	public void createFile(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				if (file.delete()) {
					file.createNewFile();
					return;
				}
				FileOutputStream writer = new FileOutputStream(path);
				writer.write(("").getBytes());
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init() {

		Authenticator authentication = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password.toCharArray());
			}
		};
		// TODO: fix certificate-problems
		httpClient = HttpClient.newBuilder()//
				.sslContext(disabledSslVerificationContext())//
				.authenticator(authentication).build();

		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.setPrettyPrinting() //
				.create();
	}

	@Override
	public CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable) {
		return getIndexDataAsync(tableName, seachTable, true);
	}

	public CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable, boolean showErrorMessage) {
		String body = gson.toJson(seachTable);

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/index")) //
				.header(CONTENT_TYPE, "application/json") //
				.method("GET", BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(timeoutDuration)).build();

		log("CAS Request Index:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""));

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "Index", showErrorMessage);
			return null;
		});

		return sendRequest.thenApply(t -> {
			Table fromJson = gson.fromJson(t.body(), Table.class);
			if (fromJson.getName().equals("Error")) {
				ErrorObject e = new ErrorObject(fromJson, username, tableName);
				postError(e);
				return null;
			}
			log("CAS Answer Index:\n" + t.body());
			return fromJson;
		});
	}

	/**
	 * Laden einer PDF Datei
	 *
	 * @param tablename
	 * @param detailTable
	 * @return
	 */
	@Override
	public CompletableFuture<Path> getPDFAsync(String tablename, Table detailTable) {
		String body = gson.toJson(detailTable);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/procedure")) //
				.header(CONTENT_TYPE, "application/json") //
				.POST(BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(timeoutDuration * 2)).build();

		Path path = getStoragePath().resolve("PDF/" + tablename + detailTable.getRows().get(0).getValue(0).getIntegerValue().toString() + ".pdf");
		try {
			Files.createDirectories(path.getParent());
			createFile(path.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}

		log("CAS Request PDF Detail:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""));

		CompletableFuture<HttpResponse<Path>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofFile(path));

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "PDF Detail", true);
			return null;
		});

		return sendRequest.thenApply(t -> {
			log("CAS Answer PDF Detail:\n" + t.body());
			return path;
		});
	}

	@Override
	public CompletableFuture<SqlProcedureResult> getDetailDataAsync(String tableName, Table detailTable) {
		return getDetailDataAsync(tableName, detailTable, true);
	}

	public CompletableFuture<SqlProcedureResult> getDetailDataAsync(String tableName, Table detailTable, boolean showErrorMessage) {
		String body = gson.toJson(detailTable);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/procedure")) //
				.header(CONTENT_TYPE, "application/json") //
				.POST(BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(timeoutDuration)).build();

		log("CAS Request Detail Data:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""));

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "Detail Data", showErrorMessage);
			return null;
		});

		return sendRequest.thenApply(t -> {
			log("CAS Answer Detail Data:\n" + t.body());
			SqlProcedureResult fromJson = gson.fromJson(t.body(), SqlProcedureResult.class);
			if (fromJson.getReturnCode() == null) {
				String errorMessage = null;
				Pattern fullError = Pattern.compile("com.microsoft.sqlserver.jdbc.SQLServerException: .*? \\| .*? \\| .*? \\| .*?\\\"");
				Matcher m = fullError.matcher(t.body());
				if (m.find()) {
					errorMessage = m.group(0);
				}
				Pattern cutError = Pattern.compile("com.microsoft.sqlserver.jdbc.SQLServerException: .*? \\| .*? \\| .*? \\| ");
				errorMessage = cutError.matcher(errorMessage).replaceAll("");
				errorMessage = errorMessage.replaceAll("\"", "");
				Table error = new Table();
				error.setName("Error");
				error.addColumn(new Column("Message", DataType.STRING));
				error.addRow(RowBuilder.newRow().withValue(errorMessage).create());
				fromJson = new SqlProcedureResult();
				fromJson.setResultSet(error);
				// FehlerCode
				fromJson.setReturnCode(-1);
			}
			if (fromJson.getReturnCode() == -1) {
				if (fromJson.getResultSet() != null && "Error".equals(fromJson.getResultSet().getName())) {
					ErrorObject e = new ErrorObject(fromJson.getResultSet(), username, tableName);
					postError(e);
					return null;
				}
			}
			return fromJson;
		});
	}

	@Override
	public CompletableFuture<SqlProcedureResult> getGridDataAsync(String tableName, Table detailTable) {
		return getGridDataAsync(tableName, detailTable, true);
	}

	public CompletableFuture<SqlProcedureResult> getGridDataAsync(String tableName, Table detailTable, boolean showErrorMessage) {
		String body = gson.toJson(detailTable);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/procedure")) //
				.header(CONTENT_TYPE, "application/json") //
				.POST(BodyPublishers.ofString(body))//
				.timeout(Duration.ofSeconds(timeoutDuration)).build();

		log("CAS Request Grid Data:\n" + request.toString() + "\n" + body.replaceAll("\\s", ""));

		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());

		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "Grid Data", showErrorMessage);
			return null;
		});

		return sendRequest.thenApply(t -> {
			log("CAS Answer Grid Data:\n" + t.body());
			SqlProcedureResult fromJson = gson.fromJson(t.body(), SqlProcedureResult.class);
			if (fromJson.getReturnCode() == null) {
				String errorMessage = null;
				Pattern fullError = Pattern.compile("com.microsoft.sqlserver.jdbc.SQLServerException: .*? \\| .*? \\| .*? \\| .*?\\\"");
				Matcher m = fullError.matcher(t.body());
				if (m.find()) {
					errorMessage = m.group(0);
				}
				Pattern cutError = Pattern.compile("com.microsoft.sqlserver.jdbc.SQLServerException: .*? \\| .*? \\| .*? \\| ");
				errorMessage = cutError.matcher(errorMessage).replaceAll("");
				errorMessage = errorMessage.replaceAll("\"", "");
				Table error = new Table();
				error.setName("Error");
				error.addColumn(new Column("Message", DataType.STRING));
				error.addRow(RowBuilder.newRow().withValue(errorMessage).create());
				fromJson = new SqlProcedureResult();
				fromJson.setResultSet(error);
				// FehlerCode
				fromJson.setReturnCode(-1);
			}
			if (fromJson.getReturnCode() == -1) {
				if (fromJson.getResultSet() != null && "Error".equals(fromJson.getResultSet().getName())) {
					ErrorObject e = new ErrorObject(fromJson.getResultSet(), username, tableName);
					postError(e);
					return null;
				}
			}
			return fromJson;
		});
	}

	private static SSLContext disabledSslVerificationContext() {
		// Remove certificate validation
		SSLContext sslContext = null;

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

	private void logCache(String message) {
		if (LOG_CACHE) {
			System.out.println(message);
		}
	}

	private void log(String body) {
		if (logger != null) {
			logger.info(body);
		}
	}

	@Override
	public CompletableFuture<String> getHashedFile(String filename) {
		logCache("Requested file: " + filename);
		try {
			if (checkIfUpdateIsRequired(filename) && false) {
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
	 * @param localPath
	 *            Lokaler Pfad für die Datei. Der Pfad vom #filename wird noch mit angehängt.
	 * @param filename
	 *            relativer Pfad und Dateiname auf dem Server
	 * @return Die Datei, wenn sie geladen werden konnte; ansonsten null
	 */
	private CompletableFuture<String> downloadAsync(String filename) {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/files/read?path=" + filename))//
				.header(CONTENT_TYPE, APPLICATION_OCTET_STREAM) //
				.timeout(Duration.ofSeconds(timeoutDuration)).build();
		log("CAS Request File Async:\n" + request + "\n" + filename);
		CompletableFuture<HttpResponse<String>> sendRequest = httpClient.sendAsync(request, BodyHandlers.ofString());
		sendRequest.exceptionally(ex -> {
			handleCASError(ex, "File Async", true);
			return null;
		});
		return sendRequest.thenApply(HttpResponse::body);
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

	/**
	 * Caller muss file.exists() aufgerufen haben
	 *
	 * @param file
	 * @return
	 */
	public CompletableFuture<String> getLocalHashForFile(File file) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HashService.hashFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "-1";
		});

	}

	@Override
	public boolean checkIfUpdateIsRequired(String fileName) throws IOException, InterruptedException {
		File file = getStoragePath().resolve(fileName).toFile();
		if (!file.exists()) {
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

		CompletableFuture<String> localHashForFile = getLocalHashForFile(file);
		String localHash = localHashForFile.join();
		return (!localHash.equals(serverHash));
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

	private void saveFile(String fileName, CompletableFuture<String> future) {
		future.thenAccept(s -> {
			try {
				File cachedFile = null;
				try {
					cachedFile = new File(new URI(workspacePath + fileName));
					ensureFoldersExist(cachedFile);
				} catch (URISyntaxException e) {
					e.printStackTrace();
					return;
				}
				Files.writeString(cachedFile.toPath(), future.join());
				logCache("Cached file: " + fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}

		});
	}

	static void ensureFoldersExist(File file) {
		File folder = file.getParentFile();
		if (!folder.exists()) {
			if (!folder.mkdirs()) {
				ensureFoldersExist(folder.getParentFile());
			}
		}
	}

	@Override
	public Path getStoragePath() {
		return Path.of(workspacePath);
	}

	/**
	 * Je Prozedur bzw. Tabellenneame gibt es einen Cache je KeyLong mit dem LookupValue
	 */
	private HashMap<String, HashMap<Integer, LookupValue>> cache = new HashMap<>();

	@Override
	public CompletableFuture<List<LookupValue>> resolveLookup(MLookupField field, boolean useCache, Integer keyLong, String keyText) {
		ArrayList<LookupValue> list = new ArrayList<>();
		if (field.getLookupTable() != null) {
			String tableName = field.getLookupTable();

			HashMap<Integer, LookupValue> map = cache.computeIfAbsent(tableName, k -> new HashMap<>());
			if (useCache) {
				if (map.get(keyLong) != null) {
					System.out.println("UseCache: " + tableName);
					list.add(map.get(keyLong));
					return CompletableFuture.supplyAsync(() -> list);
				}
			}
			Table t = TableBuilder.newTable(tableName) //
					.withColumn(TABLE_KEYLONG, DataType.INTEGER)//
					.withColumn(TABLE_KEYTEXT, DataType.STRING)//
					.withColumn(TABLE_DESCRIPTION, DataType.STRING)//
					.withColumn(TABLE_LASTACTION, DataType.INTEGER)//
					.create();
			Row row = RowBuilder.newRow() //
					.withValue(keyLong) //
					.withValue(keyText) //
					.withValue(null) //
					.withValue(fv) //
					.create();
			t.addRow(row);
			CompletableFuture<Table> tableFuture = getIndexDataAsync(t.getName(), t, false);
			Table ta = null;
			try {
				ta = tableFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("Error, using Cache: " + tableName);
				showNoResposeServerError("msg.WFCNoResponseServerUsingCache", e);
				list.add(map.get(keyLong));
				return CompletableFuture.supplyAsync(() -> list);
			}
			if (ta != null && !ta.getRows().isEmpty()) {
				Row r = ta.getRows().get(0);
				LookupValue lv = new LookupValue(//
						r.getValue(0).getIntegerValue(), //
						r.getValue(1).getStringValue(), //
						r.getValue(2) == null ? null : r.getValue(2).getStringValue());
				map.put(keyLong, lv);
				list.add(lv);
			}
			return CompletableFuture.supplyAsync(() -> list);
		} else {
			// LookupProcedurePrefix
			String procedureName = field.getLookupProcedurePrefix() + "Resolve";
			HashMap<Integer, LookupValue> map = cache.computeIfAbsent(procedureName, k -> new HashMap<>());
			if (useCache) {
				if (map.get(keyLong) != null) {
					System.out.println("UseCache: " + procedureName);
					list.add(map.get(keyLong));
					return CompletableFuture.supplyAsync(() -> list);
				}
			}
			Table t = TableBuilder.newTable(procedureName) //
					.withColumn(TABLE_KEYLONG, DataType.INTEGER)//
					.withColumn(TABLE_KEYTEXT, DataType.STRING)//
					.withColumn(TABLE_FILTERLASTACTION, DataType.BOOLEAN) //
					.create();
			Row row = RowBuilder.newRow() //
					.withValue(keyLong) //
					.withValue(keyText) //
					.withValue(false) //
					.create();
			t.addRow(row);

			CompletableFuture<SqlProcedureResult> tableFuture = getDetailDataAsync(t.getName(), t, false);
			Table ta = null;
			try {
				ta = tableFuture.get().getResultSet();
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("Error, using cache: " + procedureName);
				showNoResposeServerError("msg.WFCNoResponseServerUsingCache", e);
				list.add(map.get(keyLong));
				return CompletableFuture.supplyAsync(() -> list);
			}
			if (ta != null && !ta.getRows().isEmpty()) {
				Row r = ta.getRows().get(0);
				LookupValue lv = new LookupValue(//
						r.getValue(0).getIntegerValue(), //
						r.getValue(1).getStringValue(), //
						r.getValue(2) == null ? null : r.getValue(2).getStringValue());
				map.put(keyLong, lv);
				list.add(lv);
			}
			return CompletableFuture.supplyAsync(() -> list);
		}
	}

	@Override
	public CompletableFuture<List<LookupValue>> resolveGridLookup(String tableName, boolean useCache) {
		ArrayList<LookupValue> list = new ArrayList<>();
		HashMap<Integer, LookupValue> map = cache.computeIfAbsent(tableName, k -> new HashMap<>());
		if (useCache && !map.isEmpty()) {
			System.out.println("UseCache: " + tableName);
			list.addAll(map.values());
			return CompletableFuture.supplyAsync(() -> list);
		}
		Table t = TableBuilder.newTable(tableName) //
				.withColumn(TABLE_KEYLONG, DataType.INTEGER)//
				.withColumn(TABLE_KEYTEXT, DataType.STRING)//
				.withColumn(TABLE_DESCRIPTION, DataType.STRING)//
				.withColumn(TABLE_LASTACTION, DataType.INTEGER)//
				.create();
		Row row = RowBuilder.newRow() //
				.withValue(null) //
				.withValue(null) //
				.withValue(null) //
				.withValue(fv) //
				.create();
		t.addRow(row);

		CompletableFuture<Table> tableFuture = getIndexDataAsync(t.getName(), t, false);
		Table ta = null;
		try {
			ta = tableFuture.get();
		} catch (Exception e) {
			System.out.println("Error, using cache: " + tableName);
			showNoResposeServerError("msg.WFCNoResponseServerUsingCache", e);
			list.addAll(map.values());
			return CompletableFuture.supplyAsync(() -> list);
		}
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
		return CompletableFuture.supplyAsync(() -> list);
	}

	@Override
	public CompletableFuture<List<LookupValue>> listLookup(MLookupField field, boolean useCache, String filterText) {
		useCache = false;
		ArrayList<LookupValue> list = new ArrayList<>();
		if (field.getLookupTable() != null) {
			String tableName = field.getLookupTable();

			HashMap<Integer, LookupValue> map = cache.computeIfAbsent(tableName, k -> new HashMap<>());
			if (useCache) {
				if (!map.isEmpty()) {
					System.out.println("UseCache: " + tableName);
					list.addAll(map.values());
					return CompletableFuture.supplyAsync(() -> list);
				}
			}
			Table t = TableBuilder.newTable(tableName) //
					.withColumn(TABLE_KEYLONG, DataType.INTEGER)//
					.withColumn(TABLE_KEYTEXT, DataType.STRING)//
					.withColumn(TABLE_DESCRIPTION, DataType.STRING)//
					.withColumn(TABLE_LASTACTION, DataType.INTEGER)//
					.create();
			Row row = RowBuilder.newRow() //
					.withValue(null) //
					.withValue(filterText) //
					.withValue(null) //
					.withValue(fv) //
					.create();
			t.addRow(row);
			row = RowBuilder.newRow() //
					.withValue(null) //
					.withValue(null) //
					.withValue(filterText) //
					.withValue(fv) //
					.create();
			t.addRow(row);
			CompletableFuture<Table> tableFuture = getIndexDataAsync(t.getName(), t, false);
			Table ta = null;
			try {
				ta = tableFuture.get();
			} catch (Exception e) {
				System.out.println("Error, using cache: " + tableName);
				showNoResposeServerError("msg.WFCNoResponseServerUsingCache", e);
				list.addAll(map.values());
				return CompletableFuture.supplyAsync(() -> list);
			}
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
			return CompletableFuture.supplyAsync(() -> list);
		} else {
			// Wenn wir eine Prozedur haben müssen wie die Auswahl auf die Werte
			// einschränken, die möglich sind. Dafür sollten die Kriterien übergeben werden
			String hashName = CacheUtil.getNameList(field);
			// LookupProcedurePrefix
			String procedureName = field.getLookupProcedurePrefix() + "List";
			HashMap<Integer, LookupValue> map = cache.computeIfAbsent(hashName, k -> new HashMap<>());

			if (useCache) {
				if (!map.isEmpty()) {
					System.out.println("UseCache: " + hashName);
					list.addAll(map.values());
					return CompletableFuture.supplyAsync(() -> list);
				}
			}
			Table t = TableBuilder.newTable(procedureName) //
					.withColumn(TABLE_COUNT, DataType.INTEGER) //
					.withColumn(TABLE_FILTERLASTACTION, DataType.BOOLEAN) //
					.create();
			Row row = RowBuilder.newRow() //
					.withValue(null) //
					.withValue(true) // true, damit gelöschte Einträge nicht zurückgegeben werden
					.create();
			for (String paramName : field.getLookupParameters()) {
				MField paramField = field.getDetail().getField(paramName);
				t.addColumn(new Column(paramName, paramField.getDataType()));
				row.addValue(paramField.getValue());
			}
			t.addRow(row);

			CompletableFuture<SqlProcedureResult> tableFuture = getDetailDataAsync(t.getName(), t, false);
			Table ta = null;
			try {
				ta = tableFuture.get().getResultSet();
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("Error, using Cache: " + hashName);
				showNoResposeServerError("msg.WFCNoResponseServerUsingCache", e);
				list.addAll(map.values());
				return CompletableFuture.supplyAsync(() -> list);
			} catch (NullPointerException ex) {
				if (LOG) {
					System.out.println("listLookup: Wir haben ein Problem, posten es an den Benutzer, aber null ist hier in Ordnung!");
				}
			}
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
			return CompletableFuture.supplyAsync(() -> list);
		}
	}

	private CompletableFuture<?> getRequestedTable(int keyLong, String keyText, MField field, String purpose) {
		MDetail detail = field.getDetail();
		String tableName;
		boolean isTable = false;
		if (field.getLookupTable() != null) {
			tableName = field.getLookupTable();
			isTable = true;
		} else {
			tableName = field.getLookupProcedurePrefix() + purpose;
		}
		TableBuilder tableBuilder = TableBuilder.newTable(tableName);
		RowBuilder rowBuilder = RowBuilder.newRow();
		if (isTable) {
			tableBuilder = tableBuilder.withColumn(TABLE_KEYLONG, DataType.INTEGER)//
					.withColumn(TABLE_KEYTEXT, DataType.STRING)//
					.withColumn(TABLE_DESCRIPTION, DataType.STRING)//
					.withColumn(TABLE_LASTACTION, DataType.INTEGER);//
			if (keyLong == 0) {
				rowBuilder = rowBuilder.withValue(null);
			} else {
				rowBuilder = rowBuilder.withValue(keyLong);
			}
			rowBuilder = rowBuilder.withValue(keyText);
			rowBuilder = rowBuilder.withValue(null);
			rowBuilder = rowBuilder.withValue(">0");
		} else {
			// Reihenfolge der Werte einhalten!
			if (!purpose.equals("List")) {
				tableBuilder = tableBuilder//
						.withColumn(TABLE_KEYLONG, DataType.INTEGER)//
						.withColumn(TABLE_KEYTEXT, DataType.STRING);
				if (keyLong == 0) {
					rowBuilder = rowBuilder.withValue(null).withValue(keyText);
				} else {
					rowBuilder = rowBuilder.withValue(keyLong).withValue(null);
				}
			} else if (purpose.equals("List")) {
				tableBuilder = tableBuilder.withColumn(TABLE_COUNT, DataType.INTEGER);
				rowBuilder = rowBuilder.withValue(null);
			}
			tableBuilder = tableBuilder.withColumn(TABLE_FILTERLASTACTION, DataType.BOOLEAN);
			rowBuilder = rowBuilder.withValue(false);

			// Einschränken der angegebenen Optionen anhand bereits ausgewählter
			// Optionen (Kontrakt nur für Kunde x,...)
			// Für nicht-lookups(bookingdate)->Text übernehmen wenn nicht null
			// bei leeren feldern ein nullfeld anhängen, alle parameter müssen für die
			// anfrage gesetzt sein
			if (purpose.equals("List")) {
				List<String> parameters = field.getLookupParameters();
				for (String param : parameters) {
					MField parameterControl = detail.getField(param);
					tableBuilder.withColumn(param, parameterControl.getDataType());
					if (parameterControl.getValue() != null) {
						rowBuilder.withValue(parameterControl.getValue().getValue());
					} else {
						rowBuilder.withValue(null);
					}
				}
			}
		}
		Table t = tableBuilder.create();
		Row row = rowBuilder.create();
		t.addRow(row);
		CompletableFuture<?> tableFuture;
		if (field.getLookupTable() != null) {
			tableFuture = getIndexDataAsync(t.getName(), t, true);
		} else {
			tableFuture = getDetailDataAsync(t.getName(), t, true);
		}

		return tableFuture;
	}

	@Override
	public String getUserName() {
		return username;
	}

	@Override
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	private void handleCASError(Throwable ex, String method, boolean showErrorMessage) {
		log("CAS Error " + method + ":\n" + ex.getMessage());
		if (showErrorMessage) {
			showNoResposeServerError("msg.WFCNoResponseServer", ex);
		}
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
				if (response.statusCode() != 200) {
					throw new RuntimeException("Server returned " + response.statusCode());
				}
				postNotification("msg.UploadSuccess");
				return response;
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
