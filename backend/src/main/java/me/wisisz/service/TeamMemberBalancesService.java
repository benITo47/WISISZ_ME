package me.wisisz.service;

import me.wisisz.dto.TransactionDTO;
import me.wisisz.model.TeamMemberBalances;
import me.wisisz.repository.TeamMemberBalancesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
     * Retrieves balances for a specific team.
     * 
     * @param teamId ID of the team.
     * @return List of balances for team members within the specified team.
     */
    public List<TeamMemberBalances> getBalancesByTeam(Long teamId) {
        return teamMemberBalancesRepository.findByTeamId(teamId);
    }

    public List<TransactionDTO> getTeamTransactions(Long teamId) throws Exception {
        List<TeamMemberBalances> balance = teamMemberBalancesRepository.findByTeamId(teamId);
        balance.sort((a, b) -> a.getBalance().compareTo(b.getBalance()));

        List<TransactionDTO> transactions = new ArrayList<>();

        int begin = 0;
        int end = balance.size() - 1;

        while (begin < end) {
            TeamMemberBalances b = balance.get(begin);
            TeamMemberBalances e = balance.get(end);
            if (b.getBalance().signum() > 0 || e.getBalance().signum() < 0)
                throw new Exception("Invalid balance");

            BigDecimal owed = b.getBalance().abs().compareTo(e.getBalance().abs()) > 0 ? e.getBalance().abs()
                    : b.getBalance().abs();

            b.setBalance(b.getBalance().add(owed));
            e.setBalance(e.getBalance().subtract(owed));

            transactions.add(new TransactionDTO(b.getPerson(), e.getPerson(), owed));

            if (b.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                begin++;
            }

            if (e.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                end--;
            }
        }

        return transactions;
    }
}
