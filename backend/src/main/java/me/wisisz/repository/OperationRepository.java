package me.wisisz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import me.wisisz.model.Operation;

public interface OperationRepository extends JpaRepository<Operation, Integer> {
    Optional<Operation> findByIdAndTeamId(Integer operationId, Integer teamId);

    Optional<Operation> findFirstByTeamIdOrderByOperationDateDesc(Integer teamId);

    @Query("""
        SELECT o FROM Operation o
        JOIN o.entries e
        JOIN e.teamMember tm
        WHERE o.team.id = :teamId
          AND tm.person.id = :personId
          AND tm.team.id = :teamId
        ORDER BY o.operationDate DESC
        LIMIT 1
    """)
    Optional<Operation> findFirstMeOperation(Integer teamId, Integer personId);
}
