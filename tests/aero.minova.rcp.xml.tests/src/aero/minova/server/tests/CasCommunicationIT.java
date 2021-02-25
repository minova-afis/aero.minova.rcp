package aero.minova.server.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;

public class CasCommunicationIT {

	private String username = "admin";
	private String password = "rqgzxTf71EAx8chvchMi";
	// Dies ist unser üblicher Server, von welchen wir unsere Daten abfragen
	private String server = "https://publictest.minova.com:17280";

	private HttpClient httpClient;
	private Authenticator authentication;
	private Gson gson;

	@BeforeEach
	public void setup() {
		authentication = new Authenticator() {
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

	private static SSLContext disabledSslVerificationContext() {
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

	@Test
	public void getAuthentificationIndexHTML() throws Exception {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server))
				.build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
			System.out.println(response.body());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		assertNotNull(response);
		assertEquals(302, response.statusCode());
	}

	@Test
	@Disabled
	public void getRestAPI() throws Exception {
		String body = "{\n" + "    \"name\": \"vWorkingTimeIndex2\",\n" + "    \"columns\": [\n" + "        {\n"
				+ "            \"name\": \"EmployeeText\",\n" + "            \"type\": \"STRING\"\n" + "        }\n"
				+ "        , {\n" + "            \"name\": \"CustomerText\",\n" + "            \"type\": \"STRING\"\n"
				+ "        }\n" + "        , {\n" + "            \"name\": \"ChargedQuantity\",\n"
				+ "            \"type\": \"STRING\"\n" + "        }\n" + "        , {\n"
				+ "            \"name\": \"&\",\n" + "            \"type\": \"BOOLEAN\"\n" + "        }\n" + "    ],\n"
				+ "    \"rows\": [\n" + "        {\n" + "            \"values\" : [\n"
				+ "                \"s-like %\"\n" + "                ,\"s-SKY\"\n" + "                , \"s->=1\"\n"
				+ "                , \"b-false\"\n" + "            ]\n" + "        }\n" + "    ]\n" + "}";

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server)) //
				.header("Content-Type", "application/json") //
				.method("GET", BodyPublishers.ofString(body))//
				.build();

		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
			System.out.println(response.body());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		assertNotNull(response);

		assertTrue(response.body().length() > 0);
		Table newTable = gson.fromJson(response.body(), Table.class);
		assertNotNull(newTable);
		assertEquals(newTable.getColumnCount(), 3);
		assertEquals(200, response.statusCode());

	}

//	@Test
	public void postProcedureWithResultSet() throws Exception {
		String body = "{\n" + "    \"name\": \"spWorkingTimeServiceResolve\",\n" + "    \"columns\": [\n"
				+ "        {\n" + "            \"name\": \"KeyLong\",\n" + "            \"type\": \"INTEGER\"\n"
				+ "        }\n" + "        , {\n" + "            \"name\": \"KeyText\",\n"
				+ "            \"type\": \"STRING\"\n" + "        }\n" + "        , {\n"
				+ "            \"name\": \"FilterLastAction\",\n" + "            \"type\": \"BOOLEAN\"\n"
				+ "        }\n" + "    ],\n" + "    \"rows\": [\n" + "        {\n" + "            \"values\" : [\n"
				+ "                \"n-31\"\n" + "                , null\n" + "                , \"b-0\"\n"
				+ "            ]\n" + "        }\n" + "    ]\n" + "}";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://mintest.minova.com:8084/data/procedure")) //
				.header("Content-Type", "application/json") //
				.POST(BodyPublishers.ofString(body)).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
			System.out.println(response.body());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		assertNotNull(response);
		assertTrue(response.body().length() > 0);
		SqlProcedureResult sql = gson.fromJson(response.body(), SqlProcedureResult.class);
		Table newTable = sql.getResultSet();
		assertNotNull(newTable);
		assertEquals(3, newTable.getColumnCount());
		assertEquals(Integer.valueOf(31), newTable.getRows().get(0).getValue(0).getIntegerValue());
		assertEquals("ZPROGRAM", newTable.getRows().get(0).getValue(1).getStringValue());
		assertEquals("Programmierung", newTable.getRows().get(0).getValue(2).getStringValue());
		assertEquals(200, response.statusCode());
	}

//	@Test
	public void postProcedureWithReturnCode() throws Exception {
		String body = "{\n" + "    \"name\": \"spInsertWorkingTime\",\n" + "    \"columns\": [\n" + "        {\n"
				+ "            \"name\": \"KeyLong\",\n" + "            \"type\": \"INTEGER\"\n" + "        }\n"
				+ "        , {\n" + "            \"name\": \"EmployeeKey\",\n" + "            \"type\": \"INTEGER\"\n"
				+ "        }\n" + "        , {\n" + "            \"name\": \"ServiceContractKey\",\n"
				+ "            \"type\": \"INTEGER\"\n" + "        }\n" + "        , {\n"
				+ "            \"name\": \"OrderReceiverrKey\",\n" + "            \"type\": \"INTEGER\"\n"
				+ "        }\n" + "        , {\n" + "            \"name\": \"ServiceObjectKey\",\n"
				+ "            \"type\": \"INTEGER\"\n" + "        }\n" + "        , {\n"
				+ "            \"name\": \"ServiceKey\",\n" + "            \"type\": \"INTEGER\"\n" + "        }\n"
				+ "        , {\n" + "            \"name\": \"BookingDate\",\n" + "            \"type\": \"INSTANT\"\n"
				+ "        }\n" + "        , {\n" + "            \"name\": \"StartDate\",\n"
				+ "            \"type\": \"INSTANT\"\n" + "        }\n" + "        , {\n"
				+ "            \"name\": \"EndDate\",\n" + "            \"type\": \"INSTANT\"\n" + "        }\n"
				+ "        , {\n" + "            \"name\": \"RenderedQuantity\",\n"
				+ "            \"type\": \"DOUBLE\"\n" + "        }\n" + "        , {\n"
				+ "            \"name\": \"ChargedQuantity\",\n" + "            \"type\": \"DOUBLE\"\n" + "        }\n"
				+ "        , {\n" + "            \"name\": \"Description\",\n" + "            \"type\": \"STRING\"\n"
				+ "        }\n" + "        , {\n" + "            \"name\": \"Spelling\",\n"
				+ "            \"type\": \"BOOLEAN\"\n" + "        }\n" + "    ],\n" + "    \"rows\": [\n"
				+ "        {\n" + "            \"values\" : [\n" + "                \"n-1\"\n"
				+ "                , \"n-1\"\n" + "                , \"n-81\"\n" + "                , \"n-1\"\n"
				+ "                , \"n-8\"\n" + "                , \"n-31\"\n"
				+ "                , \"i-2020-08-05T00:00:00.00Z\"\n"
				+ "                , \"i-2020-08-05T16:00:00.00Z\"\n"
				+ "                , \"i-2020-08-05T17:00:00.00Z\"\n" + "                , \"d-1\"\n"
				+ "                , \"d-0.5\"\n" + "                , \"s-Test via CAS\"\n"
				+ "                , \"b-0\"\n" + "            ]\n" + "        }\n" + "    ]\n" + "}";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://mintest.minova.com:8084/data/procedure")) //
				.header("Content-Type", "application/json") //
				.POST(BodyPublishers.ofString(body)).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
			System.out.println(response.body());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(500, response.statusCode());
	}

//	@Test
	public void getIndexDataWorkingTimeIndex() {

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://mintest.minova.com:8084/login")).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
			System.out.println(response.body());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		assertNotNull(response);
		assertEquals(200, response.statusCode());
	}

}
