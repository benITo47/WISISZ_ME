package me.wisisz.service;

import me.wisisz.dto.OperationDTO;
import me.wisisz.dto.TeamWithMembersDTO;
import me.wisisz.model.Operation;
import me.wisisz.model.Team;
import me.wisisz.repository.TeamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<TeamWithMembersDTO> getTeamById(Integer teamId) {
        return teamRepository.findById(teamId).map(t -> new TeamWithMembersDTO(t));
    }

    public Optional<List<OperationDTO>> getTeamOperationsView(Integer teamId) {
        return teamRepository.findById(teamId)
                .map(t -> t.getOperations().stream().map(o -> new OperationDTO(o)).toList());
    }

    public Optional<List<Operation>> getTeamOperations(Integer teamId) {
        return teamRepository.findById(teamId)
                .map(t -> t.getOperations());
    }
}
