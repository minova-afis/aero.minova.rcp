package aero.minova.rcp.dataservice.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.dataservice.HashService;

public class FileUtil {

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
		path = number == 0 ? path : path.replace(".pdf", "").replaceAll("_\\d", "") + "_" + number + ".pdf";

		try {
			File file = new File(path);
			// Wenn es das File noch nicht gibt wird es erstellt
			if (!file.exists()) {
				file.createNewFile();
			} else {

				// Versuchen, das File zu löschen und neu erstellen
				if (file.delete()) {
					file.createNewFile();
					return path;
				}

				// Ansonsten leeren String in das File schreiben
				FileOutputStream writer = new FileOutputStream(path);
				writer.write(("").getBytes());
				writer.close();
			}
		} catch (IOException e) {
			return createFile(path, number + 1);
		}
		return path;
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
				e.printStackTrace();
			}
			return "-1";
		});
	}

	public static void ensureFoldersExist(File file) {
		File folder = file.getParentFile();
		if (!folder.exists()) {
			if (!folder.mkdirs()) {
				ensureFoldersExist(folder.getParentFile());
			}
		}
	}

}
