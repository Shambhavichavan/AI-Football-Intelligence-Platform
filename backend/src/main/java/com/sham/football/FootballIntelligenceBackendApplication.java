package com.sham.football;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class FootballIntelligenceBackendApplication {

	private static void configureWindowsTrustStore() {
		String os = System.getProperty("os.name", "").toLowerCase();
		if (os.contains("win") && System.getProperty("javax.net.ssl.trustStoreType") == null) {
			System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
		}
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		configureWindowsTrustStore();
		SpringApplication.run(FootballIntelligenceBackendApplication.class, args);
	}

}
