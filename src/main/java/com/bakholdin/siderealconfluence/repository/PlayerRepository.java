package com.bakholdin.siderealconfluence.repository;

import com.bakholdin.siderealconfluence.entity.Player;
import com.bakholdin.siderealconfluence.exceptions.UserException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query(value = "select p from Player p where p.user.username = ?1")
    Optional<Player> findByUsername(String username);

    default Player getByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new UserException(String.format("User %s is not in game", username)));
    }
}
