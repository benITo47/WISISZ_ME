package me.wisisz.dto;

import java.util.List;

import me.wisisz.model.Team;

public record TeamWithMembersDTO(
        Integer teamId,
        String teamName,
        List<TeamMemberDTO> members) {

    public TeamWithMembersDTO(Team t) {
        this(t.getId(), t.getTeamName(), t.getMemberships().stream().map(m -> new TeamMemberDTO(m)).toList());
    }
}
