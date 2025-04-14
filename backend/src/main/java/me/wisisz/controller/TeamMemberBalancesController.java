package me.wisisz.controller;

import me.wisisz.model.TeamMemberBalance;
import me.wisisz.service.TeamMemberBalancesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing team member balances.
 * 
 * Provides HTTP endpoints for interacting with the "team_member_balances" view.
 * 
 * Endpoints:
 * - GET /api/team-balances: Retrieves all balances.
 * - GET /api/team-balances/{teamId}: Retrieves balances for a specific team.
 * 
 * Notes:
 * - The controller interacts with the service layer to fetch data.
 */
@RestController
@RequestMapping("/api/team-balances")
public class TeamMemberBalancesController {

    private final TeamMemberBalancesService teamMemberBalancesService;

    @Autowired
    public TeamMemberBalancesController(TeamMemberBalancesService teamMemberBalancesService) {
        this.teamMemberBalancesService = teamMemberBalancesService;
    }

    /**
     * Retrieves all balances from the database.
     * 
     * @return Response entity containing a list of all balances.
     */
    @GetMapping
    public ResponseEntity<List<TeamMemberBalance>> getAllBalances() {
        List<TeamMemberBalance> balances = teamMemberBalancesService.getAllBalances();
        return new ResponseEntity<>(balances, HttpStatus.OK);
    }

    /**
     * Retrieves balances for a specific team.
     * 
     * @param teamId ID of the team.
     * @return Response entity containing a list of balances for the team members, or a 404 status if no data is found.
     */
    @GetMapping("/{teamId}")
    public ResponseEntity<List<TeamMemberBalance>> getBalancesByTeamId(@PathVariable Integer teamId) {
        List<TeamMemberBalance> balances = teamMemberBalancesService.getBalancesByTeamId(teamId);
        if (!balances.isEmpty()) {
            return new ResponseEntity<>(balances, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
