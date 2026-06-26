package com.sham.football;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@SpringBootApplication
@EnableScheduling
public class FootballIntelligenceBackendApplication {

	private static void configureDatasourceFromDatabaseUrl() {
		String springDatasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
		String databaseUrl = firstNonBlank(
				System.getenv("DATABASE_PRIVATE_URL"),
				System.getenv("DATABASE_URL"),
				System.getenv("POSTGRES_URL"),
				System.getenv("PGDATABASE_URL")
		);

		if (springDatasourceUrl != null && !springDatasourceUrl.isBlank()) {
			return;
		}

		if (databaseUrl == null || databaseUrl.isBlank()) {
			return;
		}

		try {
			URI uri = new URI(databaseUrl);
			String scheme = uri.getScheme();
			if (scheme == null) {
				return;
			}

			if (!"postgres".equalsIgnoreCase(scheme) && !"postgresql".equalsIgnoreCase(scheme)) {
				return;
			}

			String host = uri.getHost();
			int port = uri.getPort() == -1 ? 5432 : uri.getPort();
			String path = uri.getPath() == null ? "" : uri.getPath();
			String database = path.startsWith("/") ? path.substring(1) : path;

			if (host == null || host.isBlank() || database.isBlank()) {
				return;
			}

			String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
			if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
				jdbcUrl += "?" + uri.getQuery();
			}

			System.setProperty("spring.datasource.url", jdbcUrl);

			String userInfo = uri.getUserInfo();
			if (userInfo != null && !userInfo.isBlank()) {
				String[] parts = userInfo.split(":", 2);
				if (parts.length > 0 && !parts[0].isBlank()) {
					System.setProperty("spring.datasource.username", parts[0]);
				}
				if (parts.length > 1 && !parts[1].isBlank()) {
					System.setProperty("spring.datasource.password", parts[1]);
				}
			}
		} catch (URISyntaxException ignored) {
			// Keep defaults from application.properties when DATABASE_URL is invalid.
		}
	}

	private static String firstNonBlank(String... values) {
		if (values == null) {
			return null;
		}

		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}

		return null;
	}

	private static void configureWindowsTrustStore() {
		String os = System.getProperty("os.name", "").toLowerCase();
		if (os.contains("win") && System.getProperty("javax.net.ssl.trustStoreType") == null) {
			System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
		}
	}

	private static void disableSSLVerification() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
					}

					@Override
					public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
					}
				}
			};

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
	}

	public static void main(String[] args) {
		configureWindowsTrustStore();
		configureDatasourceFromDatabaseUrl();
		disableSSLVerification();
		SpringApplication.run(FootballIntelligenceBackendApplication.class, args);
	}

}
