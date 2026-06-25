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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@SpringBootApplication
@EnableScheduling
public class FootballIntelligenceBackendApplication {

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
		disableSSLVerification();
		SpringApplication.run(FootballIntelligenceBackendApplication.class, args);
	}

}
