package com.sham.football.dto;

import java.util.ArrayList;
import java.util.List;

public class MatchInsightDTO {

    private double possession;
    private double attackEfficiency;
    private double defensiveEfficiency;
    private List<String> keyMoments = new ArrayList<>();
    private String summary;

    public double getPossession() {
        return possession;
    }

    public void setPossession(double possession) {
        this.possession = possession;
    }

    public double getAttackEfficiency() {
        return attackEfficiency;
    }

    public void setAttackEfficiency(double attackEfficiency) {
        this.attackEfficiency = attackEfficiency;
    }

    public double getDefensiveEfficiency() {
        return defensiveEfficiency;
    }

    public void setDefensiveEfficiency(double defensiveEfficiency) {
        this.defensiveEfficiency = defensiveEfficiency;
    }

    public List<String> getKeyMoments() {
        return keyMoments;
    }

    public void setKeyMoments(List<String> keyMoments) {
        this.keyMoments = keyMoments;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
