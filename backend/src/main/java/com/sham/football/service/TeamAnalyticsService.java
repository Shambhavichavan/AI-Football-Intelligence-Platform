package com.sham.football.service;

import com.sham.football.dto.TeamFormDTO;
import com.sham.football.dto.TeamRankingDTO;
import com.sham.football.dto.TeamStatisticsDTO;
import com.sham.football.dto.MatchPredictionResponseDTO;
import com.sham.football.entity.Match;
import com.sham.football.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TeamAnalyticsService {

    private final MatchRepository repository;

    public TeamAnalyticsService(MatchRepository repository) {
        this.repository = repository;
    }

    public TeamFormDTO getTeamForm(String teamName) {
        String normalizedTeam = teamName == null ? "" : teamName.trim();

        List<Match> teamMatches = repository.findAll().stream()
                .filter(this::isCompleted)
                .filter(match -> isTeamInMatch(match, normalizedTeam))
                .sorted(matchDateComparator())
                .toList();

        int wins = 0;
        int draws = 0;
        int losses = 0;

        for (Match match : teamMatches) {
            String result = resultForTeam(match, normalizedTeam);
            if ("W".equals(result)) {
                wins++;
            } else if ("D".equals(result)) {
                draws++;
            } else {
                losses++;
            }
        }

        List<String> recentResults = teamMatches.stream()
                .limit(5)
                .map(match -> resultForTeam(match, normalizedTeam))
                .toList();

        TeamFormDTO dto = new TeamFormDTO();
        dto.setTeam(normalizedTeam);
        dto.setWins(wins);
        dto.setDraws(draws);
        dto.setLosses(losses);
        dto.setRecentResults(recentResults);
        dto.setForm(String.join("", recentResults));
        return dto;
    }

    public TeamStatisticsDTO getTeamStatistics(String teamName) {
        String normalizedTeam = normalizeTeamName(teamName);

        List<Match> teamMatches = repository.findAll().stream()
                .filter(this::isCompleted)
                .filter(match -> isTeamInMatch(match, normalizedTeam))
                .toList();

        int wins = 0;
        int draws = 0;
        int losses = 0;
        int goalsScored = 0;
        int goalsConceded = 0;
        int cleanSheets = 0;

        for (Match match : teamMatches) {
            boolean isHomeTeam = normalizedTeam.equalsIgnoreCase(defaultText(match.getHomeTeam()));
            int teamGoals = isHomeTeam ? match.getHomeScore() : match.getAwayScore();
            int opponentGoals = isHomeTeam ? match.getAwayScore() : match.getHomeScore();

            goalsScored += teamGoals;
            goalsConceded += opponentGoals;

            if (opponentGoals == 0) {
                cleanSheets++;
            }

            if (teamGoals > opponentGoals) {
                wins++;
            } else if (teamGoals == opponentGoals) {
                draws++;
            } else {
                losses++;
            }
        }

        int totalMatches = teamMatches.size();
        double winPercentage = totalMatches > 0 ? roundTwoDecimals((wins * 100.0) / totalMatches) : 0.0;
        double averageGoals = totalMatches > 0 ? roundTwoDecimals((double) goalsScored / totalMatches) : 0.0;

        TeamStatisticsDTO dto = new TeamStatisticsDTO();
        dto.setTeam(normalizedTeam);
        dto.setWins(wins);
        dto.setDraws(draws);
        dto.setLosses(losses);
        dto.setGoalsScored(goalsScored);
        dto.setGoalsConceded(goalsConceded);
        dto.setWinPercentage(winPercentage);
        dto.setCleanSheets(cleanSheets);
        dto.setAverageGoals(averageGoals);
        return dto;
    }

    public List<TeamRankingDTO> getTeamRankings() {
        Set<String> teams = collectTeams();

        return teams.stream()
                .map(team -> new TeamRankingDTO(team, calculateStrength(team, false)))
                .sorted(Comparator.comparingInt(TeamRankingDTO::getRating).reversed())
                .toList();
    }

    public MatchPredictionResponseDTO predictMatch(String homeTeam, String awayTeam) {
        String normalizedHome = normalizeTeamName(homeTeam);
        String normalizedAway = normalizeTeamName(awayTeam);

        int homeStrength = calculateStrength(normalizedHome, true);
        int awayStrength = calculateStrength(normalizedAway, false);

        TeamStatisticsDTO homeStats = getTeamStatistics(normalizedHome);
        TeamStatisticsDTO awayStats = getTeamStatistics(normalizedAway);

        double homeScore = homeStrength + (homeStats.getAverageGoals() * 3);
        double awayScore = awayStrength + (awayStats.getAverageGoals() * 3);
        double drawScore = Math.max(12.0, 34.0 - (Math.abs(homeScore - awayScore) * 0.5));

        double expHome = Math.exp(homeScore / 25.0);
        double expDraw = Math.exp(drawScore / 25.0);
        double expAway = Math.exp(awayScore / 25.0);
        double sum = expHome + expDraw + expAway;

        int homeWin = (int) Math.round((expHome / sum) * 100);
        int draw = (int) Math.round((expDraw / sum) * 100);
        int awayWin = 100 - homeWin - draw;

        if (awayWin < 0) {
            awayWin = 0;
        }

        MatchPredictionResponseDTO response = new MatchPredictionResponseDTO();
        response.setHome(normalizedHome);
        response.setAway(normalizedAway);
        response.setHomeWin(homeWin);
        response.setDraw(draw);
        response.setAwayWin(awayWin);
        response.setConfidenceScore(Math.max(homeWin, Math.max(draw, awayWin)));
        return response;
    }

    private boolean isCompleted(Match match) {
        return match.getHomeScore() != null && match.getAwayScore() != null;
    }

    private boolean isTeamInMatch(Match match, String teamName) {
        return teamName.equalsIgnoreCase(defaultText(match.getHomeTeam()))
                || teamName.equalsIgnoreCase(defaultText(match.getAwayTeam()));
    }

    private String normalizeTeamName(String teamName) {
        return teamName == null ? "" : teamName.trim();
    }

    private int calculateStrength(String teamName, boolean includeHomeAdvantage) {
        TeamStatisticsDTO stats = getTeamStatistics(teamName);
        TeamFormDTO form = getTeamForm(teamName);

        int recentFormBonus = form.getRecentResults().stream()
                .mapToInt(result -> {
                    if ("W".equals(result)) {
                        return 2;
                    }
                    if ("D".equals(result)) {
                        return 1;
                    }
                    return 0;
                })
                .sum();

        int homeAdvantage = includeHomeAdvantage ? 5 : 0;

        return (stats.getWins() * 4)
                + stats.getGoalsScored()
                - stats.getGoalsConceded()
                + recentFormBonus
                + homeAdvantage;
    }

    private Set<String> collectTeams() {
        Set<String> teams = new HashSet<>();

        for (Match match : repository.findAll()) {
            if (match.getHomeTeam() != null && !match.getHomeTeam().isBlank()) {
                teams.add(match.getHomeTeam().trim());
            }
            if (match.getAwayTeam() != null && !match.getAwayTeam().isBlank()) {
                teams.add(match.getAwayTeam().trim());
            }
        }

        return teams;
    }

    private String resultForTeam(Match match, String teamName) {
        boolean homeTeam = teamName.equalsIgnoreCase(defaultText(match.getHomeTeam()));
        int homeScore = match.getHomeScore();
        int awayScore = match.getAwayScore();

        if (homeScore == awayScore) {
            return "D";
        }

        if ((homeTeam && homeScore > awayScore) || (!homeTeam && awayScore > homeScore)) {
            return "W";
        }

        return "L";
    }

    private Comparator<Match> matchDateComparator() {
        return Comparator
                .comparing(this::safeParseDate, Comparator.reverseOrder())
                .thenComparing(Match::getId, Comparator.nullsLast(Comparator.reverseOrder()));
    }

    private Instant safeParseDate(Match match) {
        String matchDate = defaultText(match.getMatchDate());
        if (matchDate.isBlank()) {
            return Instant.EPOCH;
        }
        try {
            return Instant.parse(matchDate);
        } catch (Exception ignored) {
            return Instant.EPOCH;
        }
    }

    private String defaultText(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }

    private double roundTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}