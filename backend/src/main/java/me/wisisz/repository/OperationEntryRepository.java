package me.wisisz.repository;

import me.wisisz.model.OperationEntry;
import me.wisisz.model.Operation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationEntryRepository extends JpaRepository<OperationEntry, Integer> {
    List<OperationEntry> findByOperation(Operation operation);
}
