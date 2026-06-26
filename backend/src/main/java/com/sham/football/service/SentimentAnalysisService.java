package com.sham.football.service;

import com.sham.football.dto.SentimentSummaryDTO;
import com.sham.football.entity.SentimentRecord;
import com.sham.football.repository.SentimentRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SentimentAnalysisService {

    private static final Set<String> POSITIVE_WORDS = Set.of(
            "great", "amazing", "brilliant", "dominant", "fantastic", "strong", "excellent", "clinical",
            "solid", "impressive", "resilient", "winner", "winning", "good", "confident", "sharp"
    );

    private static final Set<String> NEGATIVE_WORDS = Set.of(
            "bad", "poor", "awful", "weak", "terrible", "disappointing", "fragile", "sloppy",
            "worrying", "mistake", "losing", "loss", "chaotic", "flat", "injured", "struggling"
    );

        private static final Set<String> NEGATION_WORDS = Set.of(
            "not", "no", "never", "hardly", "barely", "without"
        );

    private final SentimentRecordRepository repository;

    public SentimentAnalysisService(SentimentRecordRepository repository) {
        this.repository = repository;
        seedIfEmpty();
    }

    public List<SentimentRecord> getAllRecords() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(SentimentRecord::getCreatedAt).reversed())
                .toList();
    }

    public SentimentRecord analyzeAndStore(String fanName, String team, String message) {
        String normalizedMessage = Optional.ofNullable(message).orElse("").trim();
        String normalizedTeam = Optional.ofNullable(team).orElse("Unknown").trim();
        String normalizedFanName = Optional.ofNullable(fanName).orElse("Anonymous").trim();

        double score = computeScore(normalizedMessage);
        String label = toLabel(score);

        SentimentRecord record = new SentimentRecord();
        record.setFanName(normalizedFanName.isEmpty() ? "Anonymous" : normalizedFanName);
        record.setTeam(normalizedTeam.isEmpty() ? "Unknown" : normalizedTeam);
        record.setMessage(normalizedMessage.isEmpty() ? "No message provided" : normalizedMessage);
        record.setScore(Math.round(score * 100.0) / 100.0);
        record.setLabel(label);
        record.setCreatedAt(LocalDateTime.now());

        return repository.save(record);
    }

    public SentimentSummaryDTO getSummary() {
        List<SentimentRecord> records = repository.findAll();

        long total = records.size();
        long positive = records.stream().filter(record -> "POSITIVE".equals(record.getLabel())).count();
        long neutral = records.stream().filter(record -> "NEUTRAL".equals(record.getLabel())).count();
        long negative = records.stream().filter(record -> "NEGATIVE".equals(record.getLabel())).count();

        double positivityRate = total == 0 ? 0.0 : (positive * 100.0) / total;
        double negativityRate = total == 0 ? 0.0 : (negative * 100.0) / total;

        String overallMood;
        if (positive > negative) {
            overallMood = "Positive";
        } else if (negative > positive) {
            overallMood = "Negative";
        } else {
            overallMood = "Neutral";
        }

        SentimentSummaryDTO dto = new SentimentSummaryDTO();
        dto.setTotalMentions(total);
        dto.setPositiveMentions(positive);
        dto.setNeutralMentions(neutral);
        dto.setNegativeMentions(negative);
        dto.setPositivityRate(round(positivityRate));
        dto.setNegativityRate(round(negativityRate));
        dto.setOverallMood(overallMood);
        dto.setTopPositiveTeam(getTopTeamByLabel(records, "POSITIVE"));
        dto.setTopNegativeTeam(getTopTeamByLabel(records, "NEGATIVE"));

        return dto;
    }

    private double computeScore(String message) {
        if (message == null || message.isBlank()) {
            return 0.0;
        }

        String[] tokens = message.toLowerCase(Locale.ROOT).replaceAll("[^a-z ]", " ").split("\\s+");

        int positive = 0;
        int negative = 0;
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            boolean negated = i > 0 && NEGATION_WORDS.contains(tokens[i - 1]);

            if (POSITIVE_WORDS.contains(token)) {
                if (negated) {
                    negative++;
                } else {
                    positive++;
                }
            }

            if (NEGATIVE_WORDS.contains(token)) {
                if (negated) {
                    positive++;
                } else {
                    negative++;
                }
            }
        }

        int totalPolarityWords = positive + negative;
        if (totalPolarityWords == 0) {
            return 0.0;
        }

        return ((positive - negative) * 100.0) / totalPolarityWords;
    }

    private String toLabel(double score) {
        if (score >= 20.0) {
            return "POSITIVE";
        }
        if (score <= -20.0) {
            return "NEGATIVE";
        }
        return "NEUTRAL";
    }

    private String getTopTeamByLabel(List<SentimentRecord> records, String label) {
        return records.stream()
                .filter(record -> label.equals(record.getLabel()))
                .collect(Collectors.groupingBy(SentimentRecord::getTeam, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                .orElse("N/A");
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void seedIfEmpty() {
        if (repository.count() > 0) {
            return;
        }

        analyzeAndStore("Amina", "Argentina", "Argentina looked brilliant and dominant today, truly amazing attack.");
        analyzeAndStore("Rayan", "Brazil", "Brazil were strong but a bit sloppy in defense.");
        analyzeAndStore("Sofia", "France", "France had a good start but the second half felt flat and worrying.");
        analyzeAndStore("Jonas", "Germany", "Germany delivered a solid and impressive pressing game.");
        analyzeAndStore("Lina", "England", "England performance was poor and disappointing in front of goal.");
        analyzeAndStore("Noah", "Argentina", "Clinical passing and excellent finishing by Argentina.");
    }
}
