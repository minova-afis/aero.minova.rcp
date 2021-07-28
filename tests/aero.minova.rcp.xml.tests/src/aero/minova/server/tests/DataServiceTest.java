package aero.minova.server.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.runners.MethodSorters;

import aero.minova.rcp.dataservice.internal.DataService;

/**
 * Integration test for the data service
 * 
 * @author Lars
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DataServiceTest {

	private String username = "admin";
	private String password = "rqgzxTf71EAx8chvchMi";
	// Dies ist unser üblicher Server, von welchen wir unsere Daten abfragen
	private String server = "http://publictest.minova.com:17280/cas";

	DataService dataService;

	@BeforeEach
	void configureDataService(@TempDir Path path) {
		URI uri = path.toUri();
		String stringUri = uri.toString();
		dataService = new DataService();
		dataService.setCredentials(username, password, server, 
				URI.create(stringUri));
	}

	@Test
	@DisplayName("Simple test to easily debug the created URI")
	void canCreateUri(@TempDir Path path) {
		URI uri = path.toUri();
		String stringUri = uri.toString() + File.separator;
		assertNotNull(uri);
		assertTrue(stringUri.startsWith("file"));
		assertFalse(stringUri.endsWith(";"));
	}

	@Test
	@DisplayName("Ensures the server returns not 200 for files that do not exit")
	void ensureThatWeThrowAnExceptionForMissingFiles() {
		assertThrows(RuntimeException.class, () -> {
			dataService.getServerHashForFile("test").join();	
		});
	}
	
	@Test
	@DisplayName("Ensures that the server can hash application.mdi")
	void hashApplicationMdi() {
		String join = dataService.getServerHashForFile("application.mdi").join();
		assertNotNull(join);
	}
	
	@Test
	@DisplayName("Get application.mdi twice should load from cache")
	void receiveTwiceTheSameFileShouldLoadFromCache() {
		// first call should download and create the cached file
		String firstVersion = dataService.getHashedFile("application.mdi").join();
		// second call should read the cached file
		String secondVersion = dataService.getHashedFile("application.mdi").join();

		// TODO Check that really the hash version was used, maybe Mockito can be used
		// to wrap the data service?
		assertEquals(firstVersion, secondVersion);
	}
	
	

}
