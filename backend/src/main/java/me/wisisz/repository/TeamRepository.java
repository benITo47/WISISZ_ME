package me.wisisz.repository;

import me.wisisz.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    // Custom queries can be added here if needed
}
