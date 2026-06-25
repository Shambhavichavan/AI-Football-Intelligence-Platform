package com.sham.football.service;

import com.sham.football.dto.MatchDTO;
import com.sham.football.entity.Match;
import com.sham.football.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FootballService {

        private final MatchRepository repository;

        public FootballService(MatchRepository repository) {
                this.repository = repository;
        }

    public List<MatchDTO> getMatches() {

        return repository.findAll()
                .stream()
                .map(match ->
                        new MatchDTO(
                                match.getHomeTeam(),
                                match.getAwayTeam(),
                                match.getLeague(),
                                match.getStatus(),
                                match.getHomeScore(),
                                match.getAwayScore()
                        )
                )
                .toList();
    }

    public void loadSampleData() {

        repository.save(
                new Match(
                        "Argentina",
                        "France",
                        "World Cup",
                        "Scheduled"
                )
        );

        repository.save(
                new Match(
                        "Brazil",
                        "Germany",
                        "World Cup",
                        "Scheduled"
                )
        );
    }

        public List<Map<String, Object>> topTeams() {
                Map<String, Integer> counts = new HashMap<>();

                for (Match match : repository.findAll()) {
                        if (match.getHomeTeam() != null && !match.getHomeTeam().isBlank()) {
                                counts.merge(match.getHomeTeam(), 1, Integer::sum);
                        }
                        if (match.getAwayTeam() != null && !match.getAwayTeam().isBlank()) {
                                counts.merge(match.getAwayTeam(), 1, Integer::sum);
                        }
                }

                return counts.entrySet().stream()
                                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                                .limit(10)
                                .map(entry -> {
                                        Map<String, Object> row = new HashMap<>();
                                        row.put("team", entry.getKey());
                                        row.put("matches", entry.getValue());
                                        return row;
                                })
                                .toList();
        }

        public Map<String, Object> teamForm(String team) {
                int wins = 0;
                int draws = 0;
                int losses = 0;

                for (Match match : repository.findAll()) {
                        if (match.getHomeScore() == null || match.getAwayScore() == null) {
                                continue;
                        }

                        boolean isHomeTeam = team.equalsIgnoreCase(match.getHomeTeam());
                        boolean isAwayTeam = team.equalsIgnoreCase(match.getAwayTeam());
                        if (!isHomeTeam && !isAwayTeam) {
                                continue;
                        }

                        if (match.getHomeScore().equals(match.getAwayScore())) {
                                draws++;
                        } else if ((isHomeTeam && match.getHomeScore() > match.getAwayScore())
                                        || (isAwayTeam && match.getAwayScore() > match.getHomeScore())) {
                                wins++;
                        } else {
                                losses++;
                        }
                }

                Map<String, Object> result = new HashMap<>();
                result.put("team", team);
                result.put("wins", wins);
                result.put("draws", draws);
                result.put("losses", losses);
                return result;
        }

        public List<Map<String, Object>> upcomingMatches() {
                List<Map<String, Object>> items = new ArrayList<>();

                for (Match match : repository.findAll()) {
                        String status = match.getStatus() == null ? "" : match.getStatus();
                        if (!("SCHEDULED".equalsIgnoreCase(status) || "TIMED".equalsIgnoreCase(status))) {
                                continue;
                        }

                        Map<String, Object> row = new HashMap<>();
                        row.put("externalMatchId", match.getExternalMatchId());
                        row.put("homeTeam", match.getHomeTeam());
                        row.put("awayTeam", match.getAwayTeam());
                        row.put("league", match.getLeague());
                        row.put("status", match.getStatus());
                        row.put("matchDate", match.getMatchDate());
                        items.add(row);
                }

                return items;
        }

        public List<Map<String, Object>> liveMatches() {
                List<Map<String, Object>> items = new ArrayList<>();

                for (Match match : repository.findAll()) {
                        String status = match.getStatus() == null ? "" : match.getStatus();
                        if (!("IN_PLAY".equalsIgnoreCase(status)
                                        || "PAUSED".equalsIgnoreCase(status)
                                        || "LIVE".equalsIgnoreCase(status))) {
                                continue;
                        }

                        Map<String, Object> row = new HashMap<>();
                        row.put("externalMatchId", match.getExternalMatchId());
                        row.put("homeTeam", match.getHomeTeam());
                        row.put("awayTeam", match.getAwayTeam());
                        row.put("league", match.getLeague());
                        row.put("status", match.getStatus());
                        row.put("homeScore", match.getHomeScore());
                        row.put("awayScore", match.getAwayScore());
                        row.put("matchDate", match.getMatchDate());
                        items.add(row);
                }

                return items;
        }
}
