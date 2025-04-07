package me.wisisz.controller;

import me.wisisz.dto.OperationDTO;
import me.wisisz.dto.TeamWithMembersDTO;
import me.wisisz.model.Operation;
import me.wisisz.model.Person;
import me.wisisz.model.Team;
import me.wisisz.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> categories = teamService.getAllTeams();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamWithMembersDTO> getTeamById(@PathVariable Integer teamId) {
        Optional<TeamWithMembersDTO> team = teamService.getTeamById(teamId);
        if (team.isPresent()) {
            return new ResponseEntity<>(team.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{teamId}/operation")
    public ResponseEntity<List<OperationDTO>> getTeamOperations(@PathVariable Integer teamId) {
        Optional<List<OperationDTO>> operations = teamService.getTeamOperationsView(teamId);
        if (operations.isPresent()) {
            return new ResponseEntity<>(operations.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{teamId}/operation/balance")
    public ResponseEntity<Map<String, BigDecimal>> getTeamBalance(@PathVariable Integer teamId) {
        Optional<List<Operation>> operations = teamService.getTeamOperations(teamId);
        if (!operations.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Map<String, BigDecimal> balance = new HashMap<>();
        for (var o : operations.get()) {
            for (var e : o.getEntries()) {
                String email = e.getTeamMember().getPerson().getEmailAddr();
                BigDecimal amount = e.getAmount();
                balance.merge(email, amount, BigDecimal::add);
            }
        }
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }
}
