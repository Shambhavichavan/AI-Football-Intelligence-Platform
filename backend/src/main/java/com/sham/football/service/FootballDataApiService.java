package com.sham.football.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sham.football.dto.MatchResponseDTO;
import com.sham.football.entity.Match;
import com.sham.football.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class FootballDataApiService {

    private final RestTemplate restTemplate;
        private final ObjectMapper objectMapper;
        private final MatchRepository matchRepository;

    @Value("${football.api.token}")
    private String token;

    public FootballDataApiService(
                        RestTemplate restTemplate,
                        ObjectMapper objectMapper,
                        MatchRepository matchRepository) {

        this.restTemplate = restTemplate;
                this.objectMapper = objectMapper;
                this.matchRepository = matchRepository;
    }

    public String getWorldCupMatches() {

        HttpHeaders headers =
                new HttpHeaders();

        headers.set(
                "X-Auth-Token",
                token
        );

        HttpEntity<Void> entity =
                new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        "https://api.football-data.org/v4/matches",
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                String json = response.getBody();

                try {
                                                JsonNode root = objectMapper.readTree(json == null ? "{}" : json);
                                                return root.toString();
                } catch (Exception e) {
                        throw new RuntimeException("Failed to parse football API response", e);
                }
    }

        public void saveMatches() {
                String json = getWorldCupMatches();

                try {
                        JsonNode root = objectMapper.readTree(json == null ? "{}" : json);
                        JsonNode matchesNode = root.path("matches");

                        if (!matchesNode.isArray()) {
                                return;
                        }

                        for (JsonNode matchNode : matchesNode) {
                                String externalId = matchNode.path("id").asText(null);
                                if (externalId == null || externalId.isBlank()) {
                                        continue;
                                }

                                MatchResponseDTO dto = new MatchResponseDTO();
                                dto.setHomeTeam(matchNode.path("homeTeam").path("name").asText(null));
                                dto.setAwayTeam(matchNode.path("awayTeam").path("name").asText(null));
                                dto.setStatus(matchNode.path("status").asText(null));

                                Optional<Match> existing = matchRepository.findByExternalMatchId(externalId);
                                Match match = existing.orElseGet(Match::new);

                                match.setExternalMatchId(externalId);
                                match.setHomeTeam(dto.getHomeTeam());
                                match.setAwayTeam(dto.getAwayTeam());
                                match.setStatus(dto.getStatus());
                                match.setLeague(matchNode.path("competition").path("name").asText(null));
                                match.setMatchDate(matchNode.path("utcDate").asText(null));
                                match.setHomeScore(matchNode.path("score").path("fullTime").path("home").isNumber()
                                                ? matchNode.path("score").path("fullTime").path("home").asInt()
                                                : null);
                                match.setAwayScore(matchNode.path("score").path("fullTime").path("away").isNumber()
                                                ? matchNode.path("score").path("fullTime").path("away").asInt()
                                                : null);
                                match.setCountry(matchNode.path("area").path("name").asText(null));

                                matchRepository.save(match);
                        }
                } catch (Exception e) {
                        throw new RuntimeException("Failed to save matches from football API", e);
                }
        }

        @Scheduled(fixedRate = 3600000)
        public void syncMatches() {

                saveMatches();
        }
}
