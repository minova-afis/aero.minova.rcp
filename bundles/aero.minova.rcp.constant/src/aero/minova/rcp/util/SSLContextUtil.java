package aero.minova.rcp.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLContextUtil {

	private SSLContextUtil() {}

	/**
	 * ACHTUNG: wir vertrauen aktuell jedem. Das führt ggf zu Sicherheitslücken
	 * 
	 * @return
	 */
	public static SSLContext getTrustAllSSLContext() {
		SSLContext sslContext = null;
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
					// Wir vertrauen allen
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
					// Wir vertrauen allen
				}
			} };
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, new SecureRandom());
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
		return sslContext;
	}

}
