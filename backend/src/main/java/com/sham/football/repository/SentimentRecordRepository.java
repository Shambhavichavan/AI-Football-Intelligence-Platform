package com.sham.football.repository;

import com.sham.football.entity.SentimentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SentimentRecordRepository extends JpaRepository<SentimentRecord, Long> {
    List<SentimentRecord> findByTeamIgnoreCase(String team);
}
