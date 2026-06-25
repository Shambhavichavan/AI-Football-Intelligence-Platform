package com.sham.football.controller;

import com.sham.football.dto.MatchDTO;
import com.sham.football.service.FootballDataApiService;
import com.sham.football.service.FootballService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FootballController {

    private final FootballService footballService;
    private final FootballDataApiService footballDataApiService;

    public FootballController(
            FootballService footballService,
            FootballDataApiService footballDataApiService
    ) {
        this.footballService = footballService;
        this.footballDataApiService = footballDataApiService;
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

    @GetMapping("/team-form")
    public Map<String, Object> teamForm(
            @RequestParam(defaultValue = "Argentina") String team
    ) {
        return footballService.teamForm(team);
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
