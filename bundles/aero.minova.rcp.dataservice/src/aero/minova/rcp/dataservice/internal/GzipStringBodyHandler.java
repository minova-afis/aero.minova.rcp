package aero.minova.rcp.dataservice.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class GzipStringBodyHandler implements BodyHandler<String> {
	@Override
	public BodySubscriber<String> apply(HttpResponse.ResponseInfo responseInfo) {
		BodySubscriber<byte[]> upstream = HttpResponse.BodySubscribers.ofByteArray();
		return HttpResponse.BodySubscribers.mapping(upstream, bytes -> {
			String encoding = responseInfo.headers().firstValue("Content-Encoding").orElse("");
			if ("gzip".equalsIgnoreCase(encoding)) {
				try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
						InputStreamReader reader = new InputStreamReader(gis, StandardCharsets.UTF_8);
						BufferedReader in = new BufferedReader(reader)) {
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = in.readLine()) != null) {
						sb.append(line);
					}
					return sb.toString();
				} catch (IOException e) {
					throw new RuntimeException("Fehler beim Dekomprimieren der GZIP-Antwort", e);
				}
			} else {
				return new String(bytes, StandardCharsets.UTF_8);
			}
		});
	}
}