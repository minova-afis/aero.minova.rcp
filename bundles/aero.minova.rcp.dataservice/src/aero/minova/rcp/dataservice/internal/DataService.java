package aero.minova.rcp.dataservice.internal;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

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

	private HttpRequest request;
	private HttpClient httpClient;
	private Authenticator authentication;
	private Gson gson;

	private void init() {

		authentication = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("admin", "admin".toCharArray());
			}
		};
		httpClient = HttpClient.newBuilder().authenticator(authentication).build();

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
		request = HttpRequest.newBuilder().uri(URI.create("http://mintest.minova.com:8084/data/index")) //
				.header("Content-Type", "application/json") //
				.method("GET", BodyPublishers.ofString(body))//
				.build();

		CompletableFuture<Table> future = httpClient.sendAsync(request, BodyHandlers.ofString())
	      .thenApply(t -> gson.fromJson( t.body(), Table.class));

		return future;
	}

	@Override
	public CompletableFuture<Table> getDetailDataAsync(String tableName, Table detailTable) {
		init();
		String body = gson.toJson(detailTable);
		request = HttpRequest.newBuilder().uri(URI.create("http://mintest.minova.com:8084/data/procedure")) //
				.header("Content-Type", "application/json") //
				.POST(BodyPublishers.ofString(body))//
				.build();

		CompletableFuture<Table> future = httpClient.sendAsync(request, BodyHandlers.ofString())
				.thenApply(t -> gson.fromJson(t.body(), SqlProcedureResult.class).getOutputParameters());
		System.out.println("test");

		return future;
	}
}
