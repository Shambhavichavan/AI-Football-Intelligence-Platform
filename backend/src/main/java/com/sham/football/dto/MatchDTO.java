package com.sham.football.dto;

public class MatchDTO {

    private String homeTeam;
    private String awayTeam;
    private String league;
    private String status;
    private Integer homeScore;
    private Integer awayScore;

    public MatchDTO() {
    }

    public MatchDTO(String homeTeam, String awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public MatchDTO(String homeTeam, String awayTeam, String league, String status) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.league = league;
        this.status = status;
    }

    public MatchDTO(String homeTeam, String awayTeam, String league, String status, Integer homeScore, Integer awayScore) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.league = league;
        this.status = status;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }
}
