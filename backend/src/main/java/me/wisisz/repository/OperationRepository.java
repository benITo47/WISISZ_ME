package me.wisisz.repository;

import me.wisisz.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Integer> {
    // Custom queries can be added here if needed
}
