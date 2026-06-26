package com.sham.football.dto;

public class PlayerAnalyticsDTO {

    private String topScorer;
    private String mostAssists;
    private String bestPlayer;
    private String worstForm;
    private double averageRating;

    public String getTopScorer() {
        return topScorer;
    }

    public void setTopScorer(String topScorer) {
        this.topScorer = topScorer;
    }

    public String getMostAssists() {
        return mostAssists;
    }

    public void setMostAssists(String mostAssists) {
        this.mostAssists = mostAssists;
    }

    public String getBestPlayer() {
        return bestPlayer;
    }

    public void setBestPlayer(String bestPlayer) {
        this.bestPlayer = bestPlayer;
    }

    public String getWorstForm() {
        return worstForm;
    }

    public void setWorstForm(String worstForm) {
        this.worstForm = worstForm;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
