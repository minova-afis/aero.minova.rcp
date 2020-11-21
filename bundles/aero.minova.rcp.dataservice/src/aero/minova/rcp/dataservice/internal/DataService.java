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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.service.component.annotations.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;
import aero.minova.rcp.model.builder.RowBuilder;

@Component
public class DataService implements IDataService {

	private static final boolean LOG = true;
	private HttpRequest request;
	private HttpClient httpClient;
	private Gson gson;

	private String username = null;// "admin";
	private String password = null;// "rqgzxTf71EAx8chvchMi";
	// Dies ist unser üblicher Server, von welchen wir unsere Daten abfragen
	private String server = null;// "https://publictest.minova.com:17280";

	// Dies ist der Server, auf welchen wir derzeit zugreifen müssen, um die
	// Ticket-Anfragen zu versenden
	// private String server = "https://mintest.minova.com:8084";

	@Override
	public void setCredentials(String username, String password, String server) {
		this.username = username;
		this.password = password;
		this.server = server;
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

		gson = new Gson();
		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.setPrettyPrinting() //
				.create();
	}

	@Override
	public CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable) {
		init();
		String body = gson.toJson(seachTable);
		logBody(body);

		request = HttpRequest.newBuilder().uri(URI.create(server + "/data/index")) //
				.header("Content-Type", "application/json") //
				.method("GET", BodyPublishers.ofString(body))//
				.build();
		// return CompletableFuture<Table> future
		return httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(t -> {
			System.out.println(t);
			return gson.fromJson(t.body(), Table.class);
		});

	}

	@Override
	public CompletableFuture<SqlProcedureResult> getDetailDataAsync(String tableName, Table detailTable) {
		init();
		String body = gson.toJson(detailTable);
		logBody(body);
		request = HttpRequest.newBuilder().uri(URI.create(server + "/data/procedure")) //
				.header("Content-Type", "application/json") //
				.POST(BodyPublishers.ofString(body))//
				.build();
		// return CompletableFuture<SqlProcedureResult> future
		return httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(t -> {
			SqlProcedureResult fromJson = gson.fromJson(t.body(), SqlProcedureResult.class);
			if (fromJson.getReturnCode() == null) {
				String errorMessage = null;
				Pattern fullError = Pattern
						.compile("com.microsoft.sqlserver.jdbc.SQLServerException: .*? \\| .*? \\| .*? \\| .*?\\\"");
				Matcher m = fullError.matcher(t.body());
				if (m.find()) {
					errorMessage = m.group(0);
				}
				Pattern cutError = Pattern
						.compile("com.microsoft.sqlserver.jdbc.SQLServerException: .*? \\| .*? \\| .*? \\| ");
				errorMessage = cutError.matcher(errorMessage).replaceAll("");
				errorMessage.replace("\"", "");
				Table error = new Table();
				Row r = new Row();
				error.setName("Error");
				error.addColumn(new Column("Message", DataType.STRING));
				error.addRow(RowBuilder.newRow().withValue(errorMessage).create());
				fromJson = new SqlProcedureResult();
				fromJson.setResultSet(error);
				// FehlerCode
				fromJson.setReturnCode(-1);

			}
			logBody(t.body());
			return fromJson;
		});

	}

	@Override
	public CompletableFuture<Integer> getReturnCodeAsync(String tableName, Table detailTable) {
		init();
		String body = gson.toJson(detailTable);
		logBody(body);
		request = HttpRequest.newBuilder().uri(URI.create(server + "/data/procedure-with-return-code")) //
				.header("Content-Type", "application/json") //
				.POST(BodyPublishers.ofString(body))//
				.build();
		// return CompletableFuture<Integer> future
		return httpClient.sendAsync(request, BodyHandlers.ofString())
				.thenApply(t -> gson.fromJson(t.body(), Table.class).getRows().get(0).getValue(0).getIntegerValue());

	}

	public static SSLContext disabledSslVerificationContext() {
		// Remove certificate validation
		SSLContext sslContext = null;

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, new SecureRandom());
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
		return sslContext;
	}

	void logBody(String body) {
		if (LOG) {
			// TODO Logging verbessern
			System.out.println(body);
		}

	}

	/**
	 * synchrones laden einer Datei vom Server.
	 *
	 * @param localPath Lokaler Pfad für die Datei. Der Pfad vom #filename wird noch
	 *                  mit angehängt.
	 * @param filename  relativer Pfad und Dateiname auf dem Server
	 * @return Die Datei, wenn sie geladen werden konnte; ansonsten null
	 */
	@Override
	public File getFileSynch(String filename) {
		String path = null;
		try {
			path = Platform.getInstanceLocation().getURL().toURI().toString();
			File cachedFile = new File(new URI(path + filename));
			if (cachedFile.exists()) {
				return cachedFile;
			}
		} catch (URISyntaxException e) {
		}
		return getFileSynch(path, filename);
	}

	/**
	 * synchrones laden einer Datei vom Server.
	 *
	 * @param localPath Lokaler Pfad für die Datei. Der Pfad vom #filename wird noch
	 *                  mit angehängt.
	 * @param filename  relativer Pfad und Dateiname auf dem Server
	 * @return Die Datei, wenn sie geladen werden konnte; ansonsten null
	 */
	@Override
	public File getFileSynch(String localPath, String filename) {
		init();
		request = HttpRequest.newBuilder().uri(URI.create(server + "/files/read?path=" + filename))
				.header("Content-Type", "application/octet-stream") //
				.build();

		try {
			String body = httpClient.send(request, BodyHandlers.ofString()).body();
			URI uri = new URI(localPath + filename);
			Files.writeString(Path.of(uri), body, StandardOpenOption.CREATE);
			return new File(uri);
		} catch (IOException | URISyntaxException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * asynchrones Laden eines Files vom Server
	 */
	@Override
	public CompletableFuture<String> getFile(String filename) {
		init();
		request = HttpRequest.newBuilder().uri(URI.create(server + "/files/read?path=" + filename))
				.header("Content-Type", "application/octet-stream") //
				.build();
		return httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body);

	}

	@Override
	public void loadFile(UISynchronize sync, String filename) {
		loadFile(sync, filename, false);
	}

	public void loadFile(UISynchronize sync, String filename, boolean reload) {
		try {
			String workspacePath = Platform.getInstanceLocation().getURL().toURI().toString().substring(5);
			Path localPath = FileSystems.getDefault().getPath(workspacePath + filename);
			if (!reload && Files.exists(localPath)) {
				return; // Datei existiert lokal und muss nicht nachgeladen werden
			}
			Files.createDirectories(localPath.getParent());
			try {
				CompletableFuture<String> fileFuture = getFile(filename);
				fileFuture.thenAccept(bytes -> sync.asyncExec(() -> {
					try {
						byte[] file;
						try {
							String result = bytes.substring(1, bytes.length() - 2);
							String byteValues[] = result.split(",");
							file = new byte[byteValues.length];
							int i = 0;
							for (String string : byteValues) {
								file[i++] = Byte.parseByte(string);
							}
						} catch (NumberFormatException nfe) {
							// Es ist wohl keine Datei angekommen
							file = new byte[0];
						}
						Files.write(localPath, file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}));
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			}
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Path getStoragePath() {
		Location instanceLocation = Platform.getInstanceLocation();
		Path p;
		try {
			p = Paths.get(instanceLocation.getURL().toURI());
		} catch (URISyntaxException e) {
			Platform.getLog(this.getClass()).error(e.getMessage());
			throw new RuntimeException(e);
		}
		return p;
	}
}
