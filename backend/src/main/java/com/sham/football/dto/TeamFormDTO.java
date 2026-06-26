package com.sham.football.dto;

import java.util.ArrayList;
import java.util.List;

public class TeamFormDTO {

    private String team;
    private int wins;
    private int draws;
    private int losses;
    private List<String> recentResults = new ArrayList<>();
    private String form;

    public TeamFormDTO() {
    }

    public TeamFormDTO(String team, int wins, int draws, int losses, List<String> recentResults, String form) {
        this.team = team;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.recentResults = recentResults;
        this.form = form;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public List<String> getRecentResults() {
        return recentResults;
    }

    public void setRecentResults(List<String> recentResults) {
        this.recentResults = recentResults;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }
}