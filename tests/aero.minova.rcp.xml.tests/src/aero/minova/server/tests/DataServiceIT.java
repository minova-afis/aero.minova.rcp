package aero.minova.server.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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
class DataServiceIT {

	private String username = "admin";
	private String password = "rqgzxTf71EAx8chvchMi";
	// Dies ist unser üblicher Server, von welchen wir unsere Daten abfragen
	private String server = "https://publictest.minova.com:17280";

	DataService dataService;

	@BeforeEach
	void configureDataService(@TempDir Path path) {
		URI uri = path.toUri();
		String stringUri = uri.toString() + File.pathSeparatorChar;
		dataService = new DataService();
		dataService.setCredentials(username, password, server, 
				URI.create(stringUri));
	}

	@Test
	@DisplayName("Simple test to easily debug the created URI")
	void canCreateUri(@TempDir Path path) {
		URI uri = path.toUri();
		String stringUri = uri.toString() + File.pathSeparatorChar;
		assertNotNull(uri);
		assertTrue(stringUri.startsWith("file"));
	}

	@Test
	@DisplayName("Ensures the server returns not 200 for files that do not exit")
	void ensureThatWeThrowAnExceptionForMissingFiles() {
		assertThrows(RuntimeException.class, () -> {
			dataService.getHashForFile("test").join();	
		});
	}
	
	@Test
	@DisplayName("Ensures that the server can hash application.mdi")
	void testName() {
		String join = dataService.getHashForFile("application.mdi").join();
		assertNotNull(join);
	}
	
	
	

}
