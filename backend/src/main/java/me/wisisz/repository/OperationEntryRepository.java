package me.wisisz.repository;

import me.wisisz.model.OperationEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationEntryRepository extends JpaRepository<OperationEntry, Integer> {
    // Custom queries can be added here if needed
}
