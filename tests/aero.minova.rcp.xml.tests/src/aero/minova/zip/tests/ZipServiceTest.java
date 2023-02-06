package aero.minova.zip.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aero.minova.rcp.dataservice.ZipService;

class ZipServiceTest {

	@Test
	@DisplayName("Ensure that we can unzip a zip file")
	void ensureThatUnzipWorks(@TempDir Path tempDir) throws IOException {
		ZipService.unzipFile(Path.of("resources", "zipped", "i18n.zip1").toFile(), tempDir.toString());
		Path resolve = tempDir.resolve("i18n");
		assertTrue(resolve.toFile().exists(), "Extracted i18n Verzeichnis muss existieren");
		Path resolveMessageFile = resolve.resolve("messages.properties");
		assertTrue(resolveMessageFile.toFile().exists(), "Extracted message file muss existieren");
	}

	@Test
	@DisplayName("Ensure that the target folder is deleted before unzip operation")
	void ensureThatTargetFileIsDeleteBeforeUnzip(@TempDir Path tempDir) throws IOException {
		// Create new file in target folder
		Path targetDir = tempDir.resolve("i18n");
		Files.createDirectory(targetDir);
		Path existingFile = tempDir.resolve("i18n").resolve("existingfile.txt");

		Files.writeString(existingFile, "Randon Content", StandardOpenOption.CREATE_NEW);

		assertTrue(Files.exists(existingFile, LinkOption.NOFOLLOW_LINKS));
//
//		// Unzip file
		ZipService.unzipFile(Path.of("resources", "zipped", "i18n.zip1").toFile(), tempDir.toString());
		assertFalse(Files.exists(existingFile, LinkOption.NOFOLLOW_LINKS), "Zip operation should have delete the existing file");

	}

}
