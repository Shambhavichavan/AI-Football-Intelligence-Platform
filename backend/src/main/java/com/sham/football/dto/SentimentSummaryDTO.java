package com.sham.football.dto;

public class SentimentSummaryDTO {

    private long totalMentions;
    private long positiveMentions;
    private long neutralMentions;
    private long negativeMentions;
    private double positivityRate;
    private double negativityRate;
    private String overallMood;
    private String topPositiveTeam;
    private String topNegativeTeam;

    public long getTotalMentions() {
        return totalMentions;
    }

    public void setTotalMentions(long totalMentions) {
        this.totalMentions = totalMentions;
    }

    public long getPositiveMentions() {
        return positiveMentions;
    }

    public void setPositiveMentions(long positiveMentions) {
        this.positiveMentions = positiveMentions;
    }

    public long getNeutralMentions() {
        return neutralMentions;
    }

    public void setNeutralMentions(long neutralMentions) {
        this.neutralMentions = neutralMentions;
    }

    public long getNegativeMentions() {
        return negativeMentions;
    }

    public void setNegativeMentions(long negativeMentions) {
        this.negativeMentions = negativeMentions;
    }

    public double getPositivityRate() {
        return positivityRate;
    }

    public void setPositivityRate(double positivityRate) {
        this.positivityRate = positivityRate;
    }

    public double getNegativityRate() {
        return negativityRate;
    }

    public void setNegativityRate(double negativityRate) {
        this.negativityRate = negativityRate;
    }

    public String getOverallMood() {
        return overallMood;
    }

    public void setOverallMood(String overallMood) {
        this.overallMood = overallMood;
    }

    public String getTopPositiveTeam() {
        return topPositiveTeam;
    }

    public void setTopPositiveTeam(String topPositiveTeam) {
        this.topPositiveTeam = topPositiveTeam;
    }

    public String getTopNegativeTeam() {
        return topNegativeTeam;
    }

    public void setTopNegativeTeam(String topNegativeTeam) {
        this.topNegativeTeam = topNegativeTeam;
    }
}
