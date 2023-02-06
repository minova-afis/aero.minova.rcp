package aero.minova.hash.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aero.minova.rcp.dataservice.HashService;

class HashFileTest {

	@Test
	@DisplayName("HashService should be able to hash a file")
	void ensureThatFileCanBeHashed() throws IOException {
		assertNotNull(HashService.hashFile(Path.of("resources", "some.txt").toFile()), "Failed to hash file");
	}

	@Test
	@DisplayName("Ensure our hash code is a standard one")
	void ensureThatHashCodeOfFileIsConstant() throws IOException {
		// Used https://www.md5hashgenerator.com/ to calculate the
		String expectedHash = "29633ba0bce6378ec4df14250deccbc0";
		String expecteHashAlternative = "275826d8fa067d6cac40091a36ea6a86";
		String hashFile = HashService.hashFile(Path.of("resources", "some.txt").toFile());
		boolean oneOfThemShouldBeOk = expectedHash.equals(hashFile) || expecteHashAlternative.equals(hashFile);
		assertTrue(oneOfThemShouldBeOk, "Not the expected hash code");
	}

	@Test
	@DisplayName("Ensure that the hashing the same file twice returns the same hash code")
	void ensureThatTheSameFileHasedTwiceHasSameHashCode() throws IOException {
		// Used https://passwordsgenerator.net/md5-hash-generator/ to calculate the
		// hashcode with lower case flag set
		String hashedOnce = HashService.hashFile(Path.of("resources", "some.txt").toFile());
		String hashedTwice = HashService.hashFile(Path.of("resources", "some.txt").toFile());
		assertEquals(hashedOnce, hashedTwice);
	}

	@Test
	@DisplayName("Ensure that we work with a directory")
	void ensureThatTestDirectoryIsADirectory() {
		Path path = Path.of("resources");
		assertTrue(path.toFile().isDirectory());
	}

	@Test
	@DisplayName("Ensure that we can hash a directory")
	void ensureThatWeCanHashADirectory() throws IOException {
		// Used https://passwordsgenerator.net/md5-hash-generator/ to calculate the
		// hashcode with lower case flag set
		String hashedOnce = HashService.hashDirectory(Path.of("resources"));
		assertNotNull(hashedOnce);
	}

	@Test
	@DisplayName("Ensure that two directories with the same content have same hash")
	void ensureThatWeHashTwoDirectoriesWithSameContentTheSame() throws IOException {
		// Used https://passwordsgenerator.net/md5-hash-generator/ to calculate the
		// hashcode with lower case flag set
		String hashedDir1 = HashService.hashDirectory(Path.of("resources", "copy1"));
		String hashedDir2 = HashService.hashDirectory(Path.of("resources", "copy2"));
		assertEquals(hashedDir1, hashedDir2);
	}

	@Test
	@DisplayName("Ensure that two directories with ame content have same hash")
	void ensureThatWeHashTwoDirectoriesTheSame() throws IOException {
		// Used https://passwordsgenerator.net/md5-hash-generator/ to calculate the
		// hashcode with lower case flag set
		String hashedDir1 = HashService.hashDirectory(Path.of("resources", "copy1"));
		String hashedDir2 = HashService.hashDirectory(Path.of("resources", "copy2"));
		assertEquals(hashedDir1, hashedDir2);
	}

	@Test
	@DisplayName("Ensure that two temporary directories with same files names and content have same hash")
	void hashTwoDynamicDirectoryWhichHaveSameContent(@TempDir Path tempDir, @TempDir Path tempDir2) throws IOException {

		Path file1 = tempDir.resolve("myfile.txt");

		List<String> input = Arrays.asList("input1", "input2", "input3");
		Files.write(file1, input);

		assertTrue(Files.exists(file1), "File should exist");
		String hashDirectory1 = HashService.hashDirectory(tempDir);

		Path file2 = tempDir2.resolve("myfile.txt");

		Files.write(file2, input);
		assertTrue(Files.exists(file2), "File should exist");

		String hashDirectory2 = HashService.hashDirectory(tempDir2);
		assertEquals(hashDirectory1, hashDirectory2);

	}

	@Test
	@DisplayName("Ensure that two directories with different content have different hash")
	void hashDynamicDirectoryWhichHaveFileWithSameContentButDifferentName(@TempDir Path tempDir, @TempDir Path tempDir2) throws IOException {

		Path file1 = tempDir.resolve("myfile.txt");

		List<String> input = Arrays.asList("input1", "input2", "input3");
		Files.write(file1, input);

		assertTrue(Files.exists(file1), "File should exist");
		String hashDirectory1 = HashService.hashDirectory(tempDir);

		Path file2 = tempDir.resolve("myfile2.txt");

		Files.write(file2, input);
		assertTrue(Files.exists(file2), "File should exist");

		String hashDirectory2 = HashService.hashDirectory(tempDir2);
		assertNotEquals(hashDirectory1, hashDirectory2);

	}
}
