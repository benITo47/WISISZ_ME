package me.wisisz.repository;

import me.wisisz.model.TeamMemberBalances;
import me.wisisz.util.TeamMemberBalancesId;
import me.wisisz.model.Team;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for the `TeamMemberBalances` entity.
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
@Repository
public interface TeamMemberBalancesRepository  extends JpaRepository<TeamMemberBalances, TeamMemberBalancesId> {

    /**
     * Retrieves all balances for a specific team.
     * 
     * @param teamId ID of the team.
     * @return List of balances for the team members.
     */
    List<TeamMemberBalances> findByTeam(Team team);
}
