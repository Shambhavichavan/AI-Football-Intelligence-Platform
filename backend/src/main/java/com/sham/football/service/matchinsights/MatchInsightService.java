package com.sham.football.service.matchinsights;

import com.sham.football.dto.MatchInsightDTO;
import com.sham.football.entity.Match;
import com.sham.football.repository.MatchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchInsightService {

    private final MatchRepository repository;

    public MatchInsightService(MatchRepository repository) {
        this.repository = repository;
    }

    public MatchInsightDTO getMatchInsight(Long matchId) {
        Match match = getMatch(matchId);

        MatchInsightDTO dto = new MatchInsightDTO();
        dto.setPossession(calculatePossessionAdvantage(match));
        dto.setAttackEfficiency(calculateAttackEfficiency(match));
        dto.setDefensiveEfficiency(calculateDefensiveEfficiency(match));
        dto.setKeyMoments(generateKeyMoments(match));
        dto.setSummary(generateSummary(match));
        return dto;
    }

    public String getMatchSummary(Long matchId) {
        Match match = getMatch(matchId);
        return generateSummary(match);
    }

    public List<String> generateKeyMoments(Match match) {
        List<String> moments = new ArrayList<>();

        if (!isCompleted(match)) {
            moments.add("No final key moments available yet. The match is still pending completion.");
            return moments;
        }

        int homeScore = match.getHomeScore();
        int awayScore = match.getAwayScore();
        int totalGoals = homeScore + awayScore;

        if (homeScore > awayScore) {
            moments.add("Momentum shifted early toward " + match.getHomeTeam() + ".");
        } else if (awayScore > homeScore) {
            moments.add("Momentum shifted early toward " + match.getAwayTeam() + ".");
        } else {
            moments.add("Both teams traded control phases throughout the match.");
        }

        if (totalGoals > 0) {
            int keyMinute = Math.min(90, 55 + (totalGoals * 7));
            moments.add("Key moment: " + keyMinute + "' decisive goal sequence changed the game tempo.");
        } else {
            moments.add("Defensive structure dominated the game with no breakthrough goals.");
        }

        if (Math.abs(homeScore - awayScore) >= 2) {
            moments.add("The winning side established a clear margin and controlled the final phase.");
        } else {
            moments.add("The game stayed competitive until the closing minutes.");
        }

        return moments;
    }

    public double calculateMomentum(Match match) {
        if (!isCompleted(match)) {
            return 50.0;
        }

        int diff = Math.abs(match.getHomeScore() - match.getAwayScore());
        if (diff == 0) {
            return 50.0;
        }
        return clamp(58.0 + (diff * 10.0), 0.0, 95.0);
    }

    public double calculatePossessionAdvantage(Match match) {
        if (!isCompleted(match)) {
            return 50.0;
        }

        int diff = match.getHomeScore() - match.getAwayScore();
        return roundOneDecimal(clamp(50.0 + (diff * 6.5), 30.0, 70.0));
    }

    public double calculateAttackEfficiency(Match match) {
        if (!isCompleted(match)) {
            return 0.0;
        }

        int goalsCreated = Math.max(match.getHomeScore(), match.getAwayScore());
        int totalGoals = match.getHomeScore() + match.getAwayScore();
        double base = goalsCreated * 22.0;
        double pressureBonus = totalGoals > 2 ? 8.0 : 0.0;
        return roundOneDecimal(clamp(base + pressureBonus, 0.0, 100.0));
    }

    public double calculateDefensiveEfficiency(Match match) {
        if (!isCompleted(match)) {
            return 0.0;
        }

        int conceded = Math.min(match.getHomeScore(), match.getAwayScore());
        double rating = 86.0 - (conceded * 18.0);
        if (conceded == 0) {
            rating += 10.0;
        }
        return roundOneDecimal(clamp(rating, 0.0, 100.0));
    }

    public String generateSummary(Match match) {
        String home = safe(match.getHomeTeam());
        String away = safe(match.getAwayTeam());

        if (!isCompleted(match)) {
            return home + " vs " + away + " is not completed yet. Insights will update once the final score is available.";
        }

        int homeScore = match.getHomeScore();
        int awayScore = match.getAwayScore();
        double possession = calculatePossessionAdvantage(match);
        double attack = calculateAttackEfficiency(match);
        double defense = calculateDefensiveEfficiency(match);
        double momentum = calculateMomentum(match);

        if (homeScore > awayScore) {
            return home + " secured a " + homeScore + "-" + awayScore + " victory against " + away + ". "
                    + "They controlled approximately " + possession + "% possession, attack efficiency reached " + attack
                    + "%, defensive efficiency stood at " + defense + "%, and momentum peaked at " + roundOneDecimal(momentum) + "%.";
        }

        if (awayScore > homeScore) {
            return away + " secured a " + awayScore + "-" + homeScore + " away win over " + home + ". "
                    + "Home possession estimate was " + possession + "%, attack efficiency reached " + attack
                    + "%, defensive efficiency stood at " + defense + "%, and momentum peaked at " + roundOneDecimal(momentum) + "%.";
        }

        return "The match ended in a closely contested draw (" + homeScore + "-" + awayScore + ") between " + home + " and " + away + ". "
                + "Estimated home possession was " + possession + "%, with attack efficiency at " + attack
                + "% and defensive efficiency at " + defense + "%.";
    }

    private Match getMatch(Long matchId) {
        return repository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));
    }

    private boolean isCompleted(Match match) {
        return match.getHomeScore() != null && match.getAwayScore() != null;
    }

    private String safe(String value) {
        return value == null ? "Unknown Team" : value;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
