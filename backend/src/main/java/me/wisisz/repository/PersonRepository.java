package me.wisisz.repository;

import me.wisisz.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmailAddr(String emailAddr);
}
