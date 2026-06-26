package com.sham.football.controller;

import com.sham.football.dto.MatchDTO;
import com.sham.football.dto.MatchInsightDTO;
import com.sham.football.dto.MatchPredictionRequestDTO;
import com.sham.football.dto.MatchPredictionResponseDTO;
import com.sham.football.dto.PlayerAnalyticsDTO;
import com.sham.football.dto.SentimentRequestDTO;
import com.sham.football.dto.SentimentSummaryDTO;
import com.sham.football.dto.TeamFormDTO;
import com.sham.football.dto.TeamRankingDTO;
import com.sham.football.dto.TeamStatisticsDTO;
import com.sham.football.entity.Player;
import com.sham.football.entity.SentimentRecord;
import com.sham.football.service.FootballDataApiService;
import com.sham.football.service.FootballService;
import com.sham.football.service.PlayerAnalyticsService;
import com.sham.football.service.SentimentAnalysisService;
import com.sham.football.service.TeamAnalyticsService;
import com.sham.football.service.matchinsights.MatchInsightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FootballController {

    private final FootballService footballService;
    private final FootballDataApiService footballDataApiService;
    private final TeamAnalyticsService teamAnalyticsService;
    private final MatchInsightService matchInsightService;
    private final PlayerAnalyticsService playerAnalyticsService;
    private final SentimentAnalysisService sentimentAnalysisService;

    public FootballController(
            FootballService footballService,
            FootballDataApiService footballDataApiService,
            TeamAnalyticsService teamAnalyticsService,
            MatchInsightService matchInsightService,
            PlayerAnalyticsService playerAnalyticsService,
            SentimentAnalysisService sentimentAnalysisService
    ) {
        this.footballService = footballService;
        this.footballDataApiService = footballDataApiService;
        this.teamAnalyticsService = teamAnalyticsService;
        this.matchInsightService = matchInsightService;
        this.playerAnalyticsService = playerAnalyticsService;
        this.sentimentAnalysisService = sentimentAnalysisService;
    }

    @GetMapping("/matches")
    public List<MatchDTO> getMatches() {

        return footballService.getMatches();
    }

    @PostMapping("/load-data")
    public String loadData() {
        // Load both sample data and fetch real matches from external API
        footballService.loadSampleData();
        footballDataApiService.saveMatches();
        return "Loaded sample data and fetched matches from football-data.org API";
    }

    @GetMapping("/live")
    public String liveMatches() {

        return footballDataApiService
                .getWorldCupMatches();
    }

    @GetMapping("/top-teams")
    public List<Map<String, Object>> topTeams() {
        return footballService.topTeams();
    }

    @GetMapping("/team-form/{teamName}")
    public TeamFormDTO teamFormByPath(@PathVariable String teamName) {
        return teamAnalyticsService.getTeamForm(teamName);
    }

    @GetMapping("/team-statistics/{teamName}")
    public TeamStatisticsDTO teamStatistics(@PathVariable String teamName) {
        return teamAnalyticsService.getTeamStatistics(teamName);
    }

    @GetMapping("/team-rankings")
    public List<TeamRankingDTO> teamRankings() {
        return teamAnalyticsService.getTeamRankings();
    }

    @PostMapping("/predict")
    public MatchPredictionResponseDTO predict(@RequestBody MatchPredictionRequestDTO request) {
        return teamAnalyticsService.predictMatch(request.getHome(), request.getAway());
    }

    @GetMapping("/match-insights/{matchId}")
    public MatchInsightDTO getMatchInsights(@PathVariable Long matchId) {
        return matchInsightService.getMatchInsight(matchId);
    }

    @GetMapping("/match-summary/{matchId}")
    public String getMatchSummary(@PathVariable Long matchId) {
        return matchInsightService.getMatchSummary(matchId);
    }

    @GetMapping("/players")
    public List<Player> players() {
        return playerAnalyticsService.getPlayers();
    }

    @GetMapping("/players/analytics")
    public PlayerAnalyticsDTO playerAnalytics() {
        return playerAnalyticsService.getAnalytics();
    }

    @GetMapping("/sentiments")
    public List<SentimentRecord> sentiments() {
        return sentimentAnalysisService.getAllRecords();
    }

    @GetMapping("/sentiments/summary")
    public SentimentSummaryDTO sentimentSummary() {
        return sentimentAnalysisService.getSummary();
    }

    @PostMapping("/sentiments/analyze")
    public SentimentRecord analyzeSentiment(@RequestBody SentimentRequestDTO request) {
        return sentimentAnalysisService.analyzeAndStore(
                request.getFanName(),
                request.getTeam(),
                request.getMessage()
        );
    }

    @GetMapping("/team-form")
    public TeamFormDTO teamForm(
            @RequestParam(defaultValue = "Argentina") String team
    ) {
        return teamAnalyticsService.getTeamForm(team);
    }

    @GetMapping("/upcoming-matches")
    public List<Map<String, Object>> upcomingMatches() {
        return footballService.upcomingMatches();
    }

    @GetMapping("/live-matches")
    public List<Map<String, Object>> liveMatchesFromDb() {
        return footballService.liveMatches();
    }
}
