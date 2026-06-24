package com.sham.football.repository;

import com.sham.football.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepository
        extends JpaRepository<Match, Long> {

        Optional<Match> findByExternalMatchId(String externalMatchId);
}
