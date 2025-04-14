package me.wisisz.service;

import me.wisisz.model.TeamMemberBalance;
import me.wisisz.repository.TeamMemberBalanceRepository;
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

    private final TeamMemberBalanceRepository teamMemberBalanceRepository;

    @Autowired
    public TeamMemberBalancesService(TeamMemberBalanceRepository teamMemberBalanceRepository) {
        this.teamMemberBalanceRepository = teamMemberBalanceRepository;
    }

    /**
     * Retrieves all balances from the view.
     * 
     * @return List of all balances.
     */
    public List<TeamMemberBalance> getAllBalances() {
        return teamMemberBalanceRepository.findAll();
    }

    /**
     * Retrieves balances for a specific team.
     * 
     * @param teamId ID of the team.
     * @return List of balances for team members within the specified team.
     */
    public List<TeamMemberBalance> getBalancesByTeamId(Integer teamId) {
        return teamMemberBalanceRepository.findBalancesByTeamId(teamId);
    }
}
