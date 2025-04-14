package me.wisisz.repository;

import me.wisisz.model.TeamMemberBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for the `TeamMemberBalance` entity.
 * 
 * Provides methods to interact with the "team_member_balances" view in the database.
 * 
 * Default CRUD methods:
 * - findAll(): Retrieves all records from the view.
 * - findById(): Retrieves a record by its composite key.
 * 
 * Custom queries:
 * - findBalancesByTeamId: Retrieves balances for all members within a specific team, based on the team ID.
 */
public interface TeamMemberBalanceRepository extends JpaRepository<TeamMemberBalance, TeamMemberBalanceId> {

    /**
     * Retrieves all balances for a specific team.
     * 
     * @param teamId ID of the team.
     * @return List of balances for the team members.
     */
    @Query("SELECT tmb FROM TeamMemberBalance tmb WHERE tmb.teamId = :teamId")
    List<TeamMemberBalance> findBalancesByTeamId(@Param("teamId") Integer teamId);
}
