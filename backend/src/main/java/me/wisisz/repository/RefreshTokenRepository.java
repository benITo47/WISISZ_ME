package me.wisisz.repository;

import me.wisisz.model.Person;
import me.wisisz.model.RefreshToken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByPerson(Person person);
    Optional<RefreshToken> findByToken(String token);
}
