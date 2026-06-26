package com.sham.football.service;

import com.sham.football.dto.PlayerAnalyticsDTO;
import com.sham.football.entity.Player;
import com.sham.football.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PlayerAnalyticsService {

    private final PlayerRepository repository;

    public PlayerAnalyticsService(PlayerRepository repository) {
        this.repository = repository;
        seedPlayersIfEmpty();
    }

    public List<Player> getPlayers() {
        return repository.findAll();
    }

    public String topScorer() {
        return repository.findAll().stream()
                .max(Comparator.comparingInt(Player::getGoals))
                .map(player -> player.getName() + " (" + player.getGoals() + ")")
                .orElse("N/A");
    }

    public String mostAssists() {
        return repository.findAll().stream()
                .max(Comparator.comparingInt(Player::getAssists))
                .map(player -> player.getName() + " (" + player.getAssists() + ")")
                .orElse("N/A");
    }

    public String bestPlayer() {
        return repository.findAll().stream()
                .max(Comparator.comparingDouble(Player::getRating))
                .map(player -> player.getName() + " (" + player.getRating() + ")")
                .orElse("N/A");
    }

    public String worstForm() {
        return repository.findAll().stream()
                .min(Comparator.comparingDouble(Player::getRating))
                .map(player -> player.getName() + " (" + player.getRating() + ")")
                .orElse("N/A");
    }

    public double averageRating() {
        return repository.findAll().stream()
                .mapToDouble(Player::getRating)
                .average()
                .orElse(0.0);
    }

    public PlayerAnalyticsDTO getAnalytics() {
        PlayerAnalyticsDTO dto = new PlayerAnalyticsDTO();
        dto.setTopScorer(topScorer());
        dto.setMostAssists(mostAssists());
        dto.setBestPlayer(bestPlayer());
        dto.setWorstForm(worstForm());
        dto.setAverageRating(Math.round(averageRating() * 100.0) / 100.0);
        return dto;
    }

    private void seedPlayersIfEmpty() {
        if (repository.count() > 0) {
            return;
        }

        repository.save(buildPlayer("Lionel Messi", "Argentina", 9, 7, 36, 820, 8.9, 1, 0, 89.0));
        repository.save(buildPlayer("Kylian Mbappe", "France", 8, 3, 41, 810, 8.5, 0, 0, 84.0));
        repository.save(buildPlayer("Vinicius Jr", "Brazil", 6, 6, 33, 790, 8.0, 2, 0, 86.0));
        repository.save(buildPlayer("Jude Bellingham", "England", 5, 5, 28, 760, 7.8, 3, 0, 88.0));
        repository.save(buildPlayer("Florian Wirtz", "Germany", 2, 2, 19, 700, 6.7, 4, 1, 80.0));
    }

    private Player buildPlayer(
            String name,
            String team,
            int goals,
            int assists,
            int shots,
            int minutes,
            double rating,
            int yellowCards,
            int redCards,
            double passAccuracy
    ) {
        Player player = new Player();
        player.setName(name);
        player.setTeam(team);
        player.setGoals(goals);
        player.setAssists(assists);
        player.setShots(shots);
        player.setMinutes(minutes);
        player.setRating(rating);
        player.setYellowCards(yellowCards);
        player.setRedCards(redCards);
        player.setPassAccuracy(passAccuracy);
        return player;
    }
}
