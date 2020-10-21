package aero.minova.rcp.dataservice.internal;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.osgi.service.component.annotations.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;

@Component
public class DataService implements IDataService {

	private static final boolean LOG = true;
	private HttpRequest request;
	private HttpClient httpClient;
	private Gson gson;

	private String username = "admin";
	private String password = "rqgzxTf71EAx8chvchMi";
	private String server = "https://publictest.minova.com:17280";

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
		return  httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(t -> {
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
		return  httpClient.sendAsync(request, BodyHandlers.ofString())
				.thenApply(t -> {
					SqlProcedureResult fromJson = gson.fromJson(t.body(), SqlProcedureResult.class);
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
			System.out.println(body);
		}

	}

	public CompletableFuture<String> getFile(String filename) {
		init();
		request = HttpRequest.newBuilder().uri(URI.create(server + "/files/read?path=" + filename))
				.header("Content-Type", "application/octet-stream") //
				.build();
		return httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body);

	}

}
