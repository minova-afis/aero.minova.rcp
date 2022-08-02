package aero.minova.rcp.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Input/Output Utilities
 */
public class IOUtil {

	private IOUtil() {}

	/**
	 * Kopiert eine Datei
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copyFile(File in, File out) throws IOException {
		try (FileInputStream fileInputStreamIn = new FileInputStream(in); FileOutputStream fileOutputStreamOut = new FileOutputStream(out)) {
			FileChannel inChannel = fileInputStreamIn.getChannel();
			FileChannel outChannel = fileOutputStreamOut.getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
		}
	}

	/**
	 * Öffnet den InputStream und liefert ein String mit dem Inhalt
	 * 
	 * @throws IOException
	 */
	public static String open(BufferedReader br) throws IOException {
		char[] chars = new char[1024];
		StringBuilder sb = new StringBuilder();
		int length = 0;
		while ((length = br.read(chars)) != -1) {
			sb.append(String.copyValueOf(chars, 0, length));
			chars = new char[1024];
		}
		return sb.toString();
	}

	/**
	 * Öffnet den InputStream und liefert ein String mit dem Inhalt
	 * 
	 * @param stream
	 * @return String mit dem Inhalt der Datei
	 * @throws IOException
	 */
	public static String open(InputStream stream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		return open(br);
	}

	/**
	 * Öffnet die Datei und liefert ein String mit dem Inhalt. Fehler werden verschwiegen
	 */
	public static String open(String fileName) {
		try {
			return openLoud(fileName);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Öffnet die Datei und liefert ein String mit dem Inhalt. Fehler werden weitergegeben
	 * 
	 * @throws IOException
	 */
	public static String openLoud(String fileName) throws IOException {
		char[] chars = new char[1024];
		StringBuilder sb = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(fileName));) {
			int length = 0;
			while ((length = br.read(chars)) != -1) {
				sb.append(String.copyValueOf(chars, 0, length));
				chars = new char[1024];
			}
		}

		return sb.toString();
	}

	/**
	 * Pfad für die Dateien vorbereiten oder einen Fehler bringen, wenn nicht möglich
	 */
	public static void preparePath(String path) throws IOException {
		File dir = new File(path);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Path '" + dir + "' could not be created");
		}
		if (!dir.isDirectory()) {
			throw new IOException("'" + dir + "' is not a directory");
		}
	}

	/**
	 * Überprüft den Dateinamen, wenn es die Datei schon gibt, wird ein anderer Name vorgeschlagen
	 * 
	 * @param fileName
	 *            Dateiname, der geprüft werden soll
	 * @return Wenn Dateiname nicht existiert wird fileName zurückgeliefert ansonsten wird Dateiname modifiziert
	 */
	public static String recommendFileNameIfExists(String fileName) {
		if (fileName == null) {
			throw new NullPointerException("Für eine NULL Datei kann keine Namensprüfung durchgeführt werden");
		}
		int counter = 0;
		String newFileName = fileName;
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex == -1) {
			dotIndex = fileName.length();
		}
		while (new File(newFileName).exists()) {
			newFileName = fileName.substring(0, dotIndex) + (++counter) + fileName.substring(dotIndex);
		}
		return newFileName;
	}

	/**
	 * Speichert InputStream in die angegebene Datei
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	public static void save(InputStream is, String fileName) throws IOException {
		if (is == null) {
			throw new IOException("No stream to save!");
		}
		if (fileName == null) {
			throw new IOException("No file name defined!");
		}

		try (BufferedInputStream bis = new BufferedInputStream(is); BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
			// falls InputStream bereits schon mal gelesen wurde, müssen wir den Positionszeiger zurücksetzen, sonst wird von dort aus weiter gelesen!
			is.reset();
			int data;
			while ((data = bis.read()) != -1) {
				bos.write(data);
			}
		}
	}

	/**
	 * Speichert den übergebenen Text in die angegebene Datei, bei Fehler wird false geliefert
	 */
	public static boolean save(String text, String fileName) {
		try {
			saveLoud(text, fileName);
			return true; // Erfolgreich gespeichert
		} catch (Exception ex) {
			return false; // Fehler beim Speichern
		}
	}

	/**
	 * Speichert den übergebenen Text in die angegebene Datei, Fehler werden weitergegeben
	 */
	public static void saveLoud(String text, String fileName) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
			bw.write(text);
			bw.flush();
		}
	}

	/**
	 * Speichert den übergebenen Text in die angegebene Datei, bei Fehler wird false geliefert
	 * 
	 * @param text
	 * @param fileName
	 * @param charset
	 *            z. B. UTF-8
	 * @return success
	 * @author wild
	 * @since 10.38.0
	 */
	public static boolean save(String text, String fileName, String charset) {
		try {
			saveLoud(text, fileName, charset);
			return true; // Erfolgreich gespeichert
		} catch (Exception ex) {
			return false; // Fehler beim Speichern
		}
	}

	/**
	 * Speichert den übergebenen Text in die angegebene Datei, Fehler werden weitergegeben
	 * 
	 * @param text
	 * @param fileName
	 * @param charset
	 *            z. B. UTF-8
	 * @throws IOException
	 * @author wild
	 * @since 10.38.0
	 */
	public static void saveLoud(String text, String fileName, String charset) throws IOException {
		try (Writer fileWriter = new OutputStreamWriter(new FileOutputStream(fileName), Charset.forName(charset))) {
			fileWriter.write(text);
			fileWriter.flush();
		}
	}
}