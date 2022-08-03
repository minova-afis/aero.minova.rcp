package aero.minova.rcp.dataservice.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.dataservice.HashService;

public class FileUtil {
	private FileUtil() {}

	/**
	 * Erstellt eine Datei. Falls sie existiert, wird sie geleert. <br>
	 * Sollte das Erstellen nicht funtionieren wird stattdessen versucht, eine Datei mit Endung "_1", "_2", ... zu erstellen (siehe #1105).
	 *
	 * @param path
	 * @throws FileNotFoundException
	 */
	public static String createFile(String path) {
		return createFile(path, 0);
	}

	private static String createFile(String path, int number) {

		if (number > 20) {
			return null;
		}

		path = number == 0 ? path : path.replace(".pdf", "").replaceAll("_\\d", "") + "_" + number + ".pdf";

		try {
			File file = new File(path);
			// Wenn es das File noch nicht gibt wird es erstellt
			if (!file.exists()) {
				if (file.getParentFile() != null) {
					file.getParentFile().mkdirs();
				}
				if (!file.createNewFile()) { // Fehler beim Erstellen, nächste Nummer ausprobieren
					throw new IOException();
				}
			} else {

				// Versuchen, das File zu löschen und neu erstellen
				if (deleteFile(file) && file.createNewFile()) {
					return path;
				}

				// Ansonsten leeren String in das File schreiben
				try (FileOutputStream writer = new FileOutputStream(path);) {
					writer.write(("").getBytes());
				}
			}
		} catch (IOException e) {
			return createFile(path, number + 1);
		}
		return path;
	}

	public static boolean deleteFile(File file) {
		try {
			Files.delete(file.toPath());
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Caller muss file.exists() aufgerufen haben
	 *
	 * @param file
	 * @return
	 */
	public static CompletableFuture<String> getLocalHashForFile(File file) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HashService.hashFile(file);
			} catch (IOException e) {
				// Fehler abfangen
			}
			return "-1";
		});
	}

	public static void ensureFoldersExist(File file) {
		File folder = file.getParentFile();
		if (!folder.exists() && !folder.mkdirs()) {
			ensureFoldersExist(folder.getParentFile());
		}
	}

	/**
	 * Löscht ein Verzeichnis mit allen Unterdateien. Wenn beim löschen einer Datei ein Fehler auftritt wird dieser abgefangen, und versucht die restlichen
	 * Dateien zu löschen
	 * 
	 * @param file
	 */
	public static void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}

		try {
			Files.delete(file.toPath());
		} catch (Exception e) {
			// Exception abfangen, damit Rest gelöscht werden kann
		}
	}

}
