package aero.minova.rcp.xml.tests;

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

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;

public class CasCommunicationTest {

	private HttpClient httpClient;
	private Authenticator authentication;
	private Gson gson;

	@Before
	public void setup() {
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

//	@Test
	public void getAuthentificationIndexHTML() throws Exception {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://mintest.minova.com:8084/index.html"))
				.build();
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

//	@Test
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

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://mintest.minova.com:8084/data/index")) //
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
