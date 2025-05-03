package me.wisisz.repository;

import me.wisisz.model.TeamMemberBalances;
import me.wisisz.util.TeamMemberBalancesId;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for the `TeamMemberBalances` entity.
 * 
 * Provides methods to interact with the "team_member_balances" view in the
 * database.
 * 
 * Default CRUD methods:
 * - findAll(): Retrieves all records from the view.
 * - findById(): Retrieves a record by its composite key.
 * 
 * Custom queries:
 * - findByTeamId: Retrieves balances for all members within a specific
 * team, based on the team ID.
 */
@Repository
public interface TeamMemberBalancesRepository extends JpaRepository<TeamMemberBalances, TeamMemberBalancesId> {
    List<TeamMemberBalances> findByTeamId(Long teamId);
}
