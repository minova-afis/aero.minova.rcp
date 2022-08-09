package aero.minova.rcp.dataservice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class HashService {

	/**
	 * Calculates the MD5 hash code of a file. SHA-256 would calculate a 256 bit long hash code while MD5 only produces a 128 bit long hash code of the file
	 * content. To minimize network traffic we use MD5, also because MD5 is considered faster as SHA-256.
	 * 
	 * @param f
	 * @return MD5 calculated string
	 * @throws IOException
	 */

	public static String hashFile(File f) throws IOException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 is not available, check Java installation");
		}
		try (BufferedInputStream in = new BufferedInputStream((new FileInputStream(f)));
				DigestOutputStream out = new DigestOutputStream(OutputStream.nullOutputStream(), md)) {
			in.transferTo(out);
		}

		String fx = "%0" + (md.getDigestLength() * 2) + "x";
		return String.format(fx, new BigInteger(1, md.digest()));
	}

	public static String hashDirectory(Path path) throws IOException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 is not available, check Java installation");
		}

		try (Stream<Path> walk = Files.walk(path)) {
			walk.filter(f -> f.toFile().isFile()).forEach(f -> {
				try {
					md.update(hashFile(f.toFile()).getBytes());
				} catch (IOException e) {
					throw new RuntimeException("Error while trying to Hash File " + f.getFileName(), e);
				}
			});
		}

		String fx = "%0" + (md.getDigestLength() * 2) + "x";
		return String.format(fx, new BigInteger(1, md.digest()));
	}

	@FunctionalInterface
	public interface ThrowingConsumer<T, E extends Exception> {
		void accept(T t) throws E;
	}

	static <T> Consumer<T> throwingConsumerWrapper(ThrowingConsumer<T, Exception> throwingConsumer) {
		return i -> {
			try {
				throwingConsumer.accept(i);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		};
	}
}
