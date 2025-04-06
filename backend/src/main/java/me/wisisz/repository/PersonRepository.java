package me.wisisz.repository;

import me.wisisz.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    // Custom queries can be added here if needed
}
