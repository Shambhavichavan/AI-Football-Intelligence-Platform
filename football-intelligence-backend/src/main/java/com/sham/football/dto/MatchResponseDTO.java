package com.sham.football.dto;

public class MatchResponseDTO {

    private String homeTeam;
    private String awayTeam;
    private String status;

    public MatchResponseDTO() {
    }

    public MatchResponseDTO(String homeTeam, String awayTeam, String status) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
