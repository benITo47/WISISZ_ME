package me.wisisz.service;

import me.wisisz.model.TeamMemberBalances;
import me.wisisz.repository.TeamMemberBalancesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing team member balances.
 * 
 * This class contains business logic for retrieving and processing data
 * from the "team_member_balances" view in the database.
 * 
 * Methods:
 * - getAllBalances: Retrieves all records from the view.
 * - getBalancesByTeamId: Retrieves balances for a specific team.
 * 
 * Notes:
 * - The service interacts with the repository layer to fetch data.
 */
@Service
public class TeamMemberBalancesService {

    private final TeamMemberBalancesRepository teamMemberBalancesRepository;

    @Autowired
    public TeamMemberBalancesService(TeamMemberBalancesRepository teamMemberBalancesRepository) {
        this.teamMemberBalancesRepository = teamMemberBalancesRepository;
    }

    /**
     * Retrieves all balances from the view.
     * 
     * @return List of all balances.
     */
    public List<TeamMemberBalances> getAllBalances() {
        return teamMemberBalancesRepository.findAll();
    }

    /**
     * Retrieves balances for a specific team.
     * 
     * @param teamId ID of the team.
     * @return List of balances for team members within the specified team.
     */
    public List<TeamMemberBalances> getBalancesByTeam(Long teamId) {
        return teamMemberBalancesRepository.findByTeamId(teamId);
    }
}
