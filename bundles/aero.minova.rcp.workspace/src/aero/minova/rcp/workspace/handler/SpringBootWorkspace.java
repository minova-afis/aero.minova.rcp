package aero.minova.rcp.workspace.handler;

import static java.nio.file.Files.isRegularFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

import aero.minova.rcp.preferences.WorkspaceAccessPreferences;
import aero.minova.rcp.workspace.LifeCycle;
import aero.minova.rcp.workspace.WorkspaceException;

@SuppressWarnings("restriction")
public class SpringBootWorkspace extends WorkspaceHandler {

	private static final String XXXXXXXXXXXXXXXXXXXX = "xxxxxxxxxxxxxxxxxxxx";

	private static final String KEYSTORE_FILE_NAME = "keystore.p12";

	private static final int TIMEOUT_DURATION = 15;

	public SpringBootWorkspace(String profile, URL connection, Logger logger) {
		super(logger);
		workspaceData.setConnection(connection);
		workspaceData.setProfile(profile);
	}

	@Override
	public boolean checkConnection(String username, String password, String applicationArea, Boolean saveAsDefault) throws WorkspaceException {
		String profile = getProfile();
		List<ISecurePreferences> workspaceAccessDatas = WorkspaceAccessPreferences.getSavedWorkspaceAccessData(logger);
		ISecurePreferences store = null;
		try {
			for (ISecurePreferences iSecurePreferences : workspaceAccessDatas) {
				if (profile.equals(iSecurePreferences.get(WorkspaceAccessPreferences.PROFILE, null))) {
					store = iSecurePreferences;
					break;
				}
			}
			if (store != null) {
				if (username == null || username.length() == 0) {
					username = store.get(WorkspaceAccessPreferences.USER, "");
				}
				if (applicationArea == null || applicationArea.length() == 0) {
					applicationArea = store.get(WorkspaceAccessPreferences.APPLICATION_AREA, "");
				}
				if (getConnectionString() == null || getConnectionString().length() == 0) {
					workspaceData.setConnection(new URL(store.get(WorkspaceAccessPreferences.URL, "")));
					// Check if this is an valid value
					URI.create(getConnectionString());
				}
			}
		} catch (StorageException | MalformedURLException e) {
			throw new WorkspaceException(e.getMessage());
		}
		workspaceData.setUsername(username);
		workspaceData.setPassword(password);
		workspaceData.setProfile(profile);
		workspaceData.setApplicationArea(applicationArea);

		// Profil speichern
		WorkspaceAccessPreferences.storeWorkspaceAccessData(profile, getConnectionString(), getUsername(), getPassword(), getProfile(), applicationArea,
				saveAsDefault);

		return true;
	}

	@Override
	public void open() throws WorkspaceException {

		if (!getPassword().equals(XXXXXXXXXXXXXXXXXXXX)) {
			// Entwender das Passwort ist null/leer oder es wurde manuell eingetragten / geändert
			try {
				checkCredentials(getPassword());
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
			}
		}

		if (!Platform.getInstanceLocation().isSet()) {
			// 1. Auslesen aus dem Store, ggf. Setzen
			String defaultPath = System.getProperty("user.home");

			// build the desired path for the workspace
			URL instanceLocationUrl = null;
			try {
				if (!getApplicationArea().isEmpty()) {
					instanceLocationUrl = new URL(getApplicationArea());
				} else {
					Path path = Path.of(defaultPath, LifeCycle.DEFAULT_CONFIG_FOLDER, workspaceData.getWorkspaceHashHex());
					instanceLocationUrl = new URL("file", null, path.toString());
				}
				Platform.getInstanceLocation().set(instanceLocationUrl, false);
			} catch (IllegalStateException | IOException e) {
				logger.error(e);
			}
			URL workspaceURL = Platform.getInstanceLocation().getURL();
			File workspaceDir = new File(workspaceURL.getPath());
			if (!workspaceDir.exists()) {
				workspaceDir.mkdir();
				logger.info(MessageFormat.format("Workspace {0} neu angelegt.", workspaceDir));
			}
			checkDir(workspaceDir, "config");
			checkDir(workspaceDir, "data");
			checkDir(workspaceDir, "plugins");

			for (ISecurePreferences store : WorkspaceAccessPreferences.getSavedWorkspaceAccessData(logger)) {
				try {
					if (getProfile().equals(store.get(WorkspaceAccessPreferences.PROFILE, null))) {

						if (getPassword().isEmpty() || getPassword().equals(XXXXXXXXXXXXXXXXXXXX)) {
							// ausgelesendes Passwort vom Store nehmen
							workspaceData.setPassword(store.get(WorkspaceAccessPreferences.PASSWORD, null));
						} else {
							// ausgelesendes Passwort vom Dialog in den Store setzen
							store.put(WorkspaceAccessPreferences.PASSWORD, getPassword(), true);
						}
						store.put(WorkspaceAccessPreferences.APPLICATION_AREA, instanceLocationUrl.toString(), false);
						store.flush();
						break;
					}
				} catch (StorageException | IOException e) {
					logger.error(e);
				}
			}

		} else {
			// 2. Auslesen aus dem Store dieser wurde bereits gestezt!
			for (ISecurePreferences store : WorkspaceAccessPreferences.getSavedWorkspaceAccessData(logger)) {
				try {
					if (getProfile().equals(store.get(WorkspaceAccessPreferences.PROFILE, null))) {
						if (getPassword().isEmpty() || getPassword().equals(XXXXXXXXXXXXXXXXXXXX)) {
							// ausgelesendes Passwort vom Store nehmen
							workspaceData.setPassword(store.get(WorkspaceAccessPreferences.PASSWORD, null));
						} else {
							// ausgelesendes Passwort vom Dialog in den Store setzen
							store.put(WorkspaceAccessPreferences.PASSWORD, getPassword(), true);
						}
						break;
					}
				} catch (StorageException e) {
					logger.error(e);
				}
			}
		}
		try {
			checkCredentials(workspaceData.getPassword());
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
	}

	public static SSLContext disabledSslVerificationContext() {
		try {
			final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			final Path trustStorePath = Paths.get(System.getProperty("user.home")).resolve(LifeCycle.DEFAULT_CONFIG_FOLDER).resolve(KEYSTORE_FILE_NAME);
			if (isRegularFile(trustStorePath)) {
				trustStore.load(new FileInputStream(trustStorePath.toString()), "geheim".toCharArray());
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(trustStore);
				TrustManager[] trustManagers = tmf.getTrustManagers();

				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, trustManagers, null);
				return sslContext;
			} else {
				/*
				 * Falls kein Keystore vorhanden ist, vertrauen wird erstmal allem, damit es für die lokale Entwicklung einfacher ist.
				 */
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
				} };
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, trustAllCerts, new SecureRandom());
				return sslContext;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Diese Methode überprüft die angegebenen Einträge und versucht eine verbindung zum Server herzustellen
	 *
	 * @throws WorkspaceException
	 * @throws UnsupportedEncodingException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void checkCredentials(String pw) throws WorkspaceException, UnsupportedEncodingException {

		String encodedUser = new String(getUsername().getBytes(), StandardCharsets.ISO_8859_1.toString());
		String encodedPW = new String(pw.getBytes(), StandardCharsets.ISO_8859_1.toString());

		Authenticator authentication = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(encodedUser, encodedPW.toCharArray());
			}
		};

		try {
			URI uri = URI.create(getConnectionString().endsWith("/") ? getConnectionString() : getConnectionString().concat("/"));
			HttpRequest request = HttpRequest.newBuilder().uri(uri.resolve("ping")) //
					.header("Content-Type", "application/json") //
					.method("GET", BodyPublishers.ofString(""))//
					.timeout(Duration.ofSeconds(TIMEOUT_DURATION)).build();

			HttpClient httpClient = HttpClient.newBuilder()//
					.sslContext(disabledSslVerificationContext())//
					.authenticator(authentication).build();

			logger.info("CAS Request Ping: \n" + request.toString());
			HttpResponse<String> answer = httpClient.send(request, BodyHandlers.ofString());

			logger.info("CAS Answer Ping: \n" + answer.toString());
			if (((answer.statusCode() <= 199) || (answer.statusCode() >= 300))) {
				throw new WorkspaceException("Unexpected Answer, please check Server!");
			}
		} catch (ConnectException e) {
			logger.error(e);
			throw new WorkspaceException("ConnectException " + e.getMessage());
		} catch (IOException e) {
			logger.error(e);
			throw new WorkspaceException("IOException\nUser, Password or Server incorrect?");
		} catch (InterruptedException e) {
			logger.error(e);
			Thread.currentThread().interrupt();
			throw new WorkspaceException("InterruptedException " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error(e);
			throw new WorkspaceException("IllegalArgumentException " + e.getMessage() + "\nInvalid URL?");
		} catch (NullPointerException e) {
			logger.error(e);
			throw new WorkspaceException("NullPointerException " + e.getMessage() + "\nPlease enter Password again");
		}
	}

	@Override
	public String getMessage() {
		return workspaceData.getMessage();
	}

}
