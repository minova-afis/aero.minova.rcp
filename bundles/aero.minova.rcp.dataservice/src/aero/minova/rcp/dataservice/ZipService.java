package aero.minova.rcp.dataservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipService {

	private ZipService() {}

	/**
	 * Unzips a archive file to the defined directory
	 * 
	 * @throws IOException
	 */
	public static void unzipFile(File fileZip, String destDirName) throws IOException {
		int lastIndexOf = fileZip.toString().lastIndexOf('.');
		String pathseparator = File.separator;
		int pathIndex = fileZip.toString().lastIndexOf(pathseparator);

		String targetDir = fileZip.toString().substring(pathIndex + 1, lastIndexOf);

		deleteChildrenInTargetFolder(Path.of(destDirName, targetDir).toFile());

		File destDir = new File(destDirName);
		byte[] buffer = new byte[1024];
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip))) {
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				File newFile = newFile(destDir, zipEntry);
				if (zipEntry.isDirectory()) {
					if (!newFile.isDirectory() && !newFile.mkdirs()) {
						throw new IOException("Failed to create directory " + newFile);
					}
				} else {
					// fix for Windows-created archives
					File parent = newFile.getParentFile();

					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException("Failed to create directory " + parent);
					}
					// write file content
					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
				}
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
		}
	}

	private static void deleteChildrenInTargetFolder(File dir) throws IOException {
		if (!Files.isDirectory(dir.toPath(), LinkOption.NOFOLLOW_LINKS)) {
			return;
		}
		for (File file : dir.listFiles()) {
			if (!file.isDirectory()) {
				Files.delete(file.toPath());
			}
		}
	}

	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	public static void zipFile(String sourceDirPath, String zipFilePath) throws IOException {
		if (Files.exists(Paths.get(zipFilePath))) {
			Files.delete(Paths.get(zipFilePath));
		}
		Path p = Files.createFile(Paths.get(zipFilePath));

		try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
			Path pp = Paths.get(sourceDirPath);

			try (Stream<Path> walk = Files.walk(pp)) {
				walk.filter(path -> !Files.isDirectory(path)).forEach(path -> {
					ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
					try {
						zs.putNextEntry(zipEntry);
						Files.copy(path, zs);
						zs.closeEntry();
					} catch (IOException e) {
						// Fehler abfangen
					}
				});
			}
		}
	}
}
