package aero.minova.rcp.dataservice.internal;

import java.io.File;
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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
	}

	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
	private static final String CONTENT_TYPE = "Content-Type";
	public static final String TABLE_KEYTEXT = "KeyText";
	public static final String TABLE_KEYLONG = "KeyLong";
	public static final String TABLE_DESCRIPTION = "Description";
	public static final String TABLE_LASTACTION = "LastAction";
	public static final String TABLE_COUNT = "Count";
	public static final String TABLE_FILTERLASTACTION = "FilterLastAction";

	private static final FilterValue fv = new FilterValue(">", "0", "");

	private static final boolean LOG = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/server"));
	private static final boolean LOG_CACHE = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.dataservice/debug/cache"));

	private HttpClient httpClient;
	private Gson gson;

	private String username = null;// "admin";
	private String password = null;// "rqgzxTf71EAx8chvchMi";
	// Dies ist unser üblicher Server, von welchen wir unsere Daten abfragen
	private String server = null;// "https://publictest.minova.com:17280";

	// Dies ist der Server, auf welchen wir derzeit zugreifen müssen, um die
	// Ticket-Anfragen zu versenden
	// private String server = "https://mintest.minova.com:8084";

	/**
	 * Anzahl der Aufrufe des Servers
	 */
	private int callCount = 0;
	private URI workspacePath;

	@Override
	public void setCredentials(String username, String password, String server, URI workspacePath) {
		this.username = username;
		this.password = password;
		this.server = server;
		this.workspacePath = workspacePath;
		init();
		BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		// allow to trigger components after the service has been initialized, see
		bundleContext.registerService(IDummyService.class.getName(), new IDummyService(), null);
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

		// PDF Ordner anfragen
		CompletableFuture.runAsync(() -> {
			try {
				boolean checkIfUpdateIsRequired = this.checkIfUpdateIsRequired("PDF.zip");
				if (checkIfUpdateIsRequired) {
					this.downloadFile("PDF.zip");
					ZipService.unzipFile(this.getStoragePath().resolve("PDF.zip").toFile(), this.getStoragePath().toString());
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable) {
		String body = gson.toJson(seachTable);

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/index")) //
				.header(CONTENT_TYPE, "application/json") //
				.method("GET", BodyPublishers.ofString(body))//
				.build();
		if (LOG) {
			logBody(body, ++callCount);
		}

		return httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(t -> {
			Table fromJson = gson.fromJson(t.body(), Table.class);
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
				.build();
		logBody(body, ++callCount);
		Path path = getStoragePath().resolve("PDF/" + tablename + detailTable.getRows().get(0).getValue(0).getIntegerValue().toString() + ".pdf");
		try {
			Files.createDirectories(path.getParent());
			Files.createFile(path);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return httpClient.sendAsync(request, BodyHandlers.ofFile(path)).thenApply(t -> {
			return path;
		});
	}

	@Override
	public CompletableFuture<SqlProcedureResult> getDetailDataAsync(String tableName, Table detailTable) {
		String body = gson.toJson(detailTable);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/procedure")) //
				.header(CONTENT_TYPE, "application/json") //
				.POST(BodyPublishers.ofString(body))//
				.build();
		// return CompletableFuture<SqlProcedureResult> future
		logBody(body, ++callCount);
		return httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(t -> {
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
					ErrorObject e = new ErrorObject(fromJson.getResultSet(), username);
					postError(e);
					return null;
				}
			}
			logBody(t.body());
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

	private void logBody(String body) {
		if (LOG) {
			System.out.println(body);
		}

	}

	private static void logCache(String message) {
		if (LOG_CACHE) {
			System.out.println(message);
		}

	}

	private void logBody(String body, int i) {
		if (LOG) {
			logBody("Call: " + i + "\n" + body);
		}
	}

	@Override
	public CompletableFuture<String> getHashedFile(String filename) {
		logCache("Requested file: " + filename);
		try {
			if (checkIfUpdateIsRequired(filename)) {
				logCache(filename + " need to download / update the file ");
				downloadFile(filename);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return getCachedFileContent(filename);

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
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/files/read?path=" + filename)).header(CONTENT_TYPE, APPLICATION_OCTET_STREAM) //
				.build();
		logBody("getFileSynch(" + filename + ")", ++callCount);
		return httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body);
	}

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
	@Override
	public void downloadFile(String fileName) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/files/read?path=" + fileName)).header(CONTENT_TYPE, APPLICATION_OCTET_STREAM) //
				.build();
		logBody("getFileSynch(" + fileName + ")", ++callCount);
		Path localFile = getStoragePath().resolve(fileName);
		httpClient.send(request, BodyHandlers.ofFile(localFile));
	}

	/**
	 * Only public for the integration tests
	 *
	 * @param filename
	 * @return
	 */
	public CompletableFuture<String> getServerHashForFile(String filename) {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/files/hash?path=" + filename)).header(CONTENT_TYPE, APPLICATION_OCTET_STREAM) //
				.build();

		return httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(response -> {
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
		CompletableFuture<String> serverHashForFile = getServerHashForFile(fileName);
		CompletableFuture<String> localHashForFile = getLocalHashForFile(file);
		String serverHash = serverHashForFile.join();
		String localHash = localHashForFile.join();
		return (!serverHash.equals(localHash));
	}

	private CompletableFuture<String> getCachedFileContent(String filename) {
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
			CompletableFuture<Table> tableFuture = getIndexDataAsync(t.getName(), t);
			Table ta = null;
			try {
				ta = tableFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
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

			CompletableFuture<SqlProcedureResult> tableFuture = getDetailDataAsync(t.getName(), t);
			Table ta = null;
			try {
				ta = tableFuture.get().getResultSet();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
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
	public CompletableFuture<List<LookupValue>> listLookup(MLookupField field, boolean useCache, String filterText) {
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
			CompletableFuture<Table> tableFuture = getIndexDataAsync(t.getName(), t);
			Table ta = null;
			try {
				ta = tableFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
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
					.withValue(false) //
					.create();
			for (String paramName : field.getLookupParameters()) {
				MField paramField = field.getDetail().getField(paramName);
				t.addColumn(new Column(paramName, paramField.getDataType()));
				row.addValue(paramField.getValue());
			}
			t.addRow(row);

			CompletableFuture<SqlProcedureResult> tableFuture = getDetailDataAsync(t.getName(), t);
			Table ta = null;
			try {
				ta = tableFuture.get().getResultSet();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
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
			tableFuture = getIndexDataAsync(t.getName(), t);
		} else {
			tableFuture = getDetailDataAsync(t.getName(), t);
		}

		return tableFuture;
	}

	@Override
	public String getUserName() {
		return username;
	}

//	@Override
//	public <T> T convert(Path f, Class<T> clazz) {
//		String content = Files.readString(f, StandardCharsets.UTF_8);
//		gson.fromJson(f., clazz)
//		return null;
//	}
}
