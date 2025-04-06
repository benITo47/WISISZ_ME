package me.wisisz.repository;

import me.wisisz.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    // Custom queries can be added here if needed
}
