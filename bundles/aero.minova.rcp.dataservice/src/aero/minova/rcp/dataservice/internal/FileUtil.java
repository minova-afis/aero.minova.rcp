package aero.minova.rcp.dataservice.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.dataservice.HashService;

public class FileUtil {

	/**
	 * Erstellt eine Datei falls sie existiert, wird sie geleert.
	 *
	 * @param path
	 */
	public static void createFile(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				if (file.delete()) {
					file.createNewFile();
					return;
				}
				FileOutputStream writer = new FileOutputStream(path);
				writer.write(("").getBytes());
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
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
