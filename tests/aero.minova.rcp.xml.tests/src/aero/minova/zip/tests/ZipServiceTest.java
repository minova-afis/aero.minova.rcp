package aero.minova.zip.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.runners.MethodSorters;

import aero.minova.rcp.dataservice.ZipService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ZipServiceTest {

	@Test
	@DisplayName("Ensure that we can unzip a zip file")
	void ensureThatUnzipWorks(@TempDir Path tempDir)
			throws IOException {
		System.out.println(tempDir.toString());
		ZipService.unzipFile(Path.of("resources", "zipped", "i18n.zip1").toFile(), tempDir.toString());
		Path resolve = tempDir.resolve("i18n");
		assertTrue(resolve.toFile().exists(), "Extracted i18n Verzeichnis muss existieren");
		Path resolveMessageFile = resolve.resolve("messages.properties");
		assertTrue(resolveMessageFile.toFile().exists(), "Extracted message file muss existieren");
	}
}
