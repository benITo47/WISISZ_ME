package me.wisisz.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import me.wisisz.model.Team;
import me.wisisz.model.Operation;

public record TeamWithMembersDTO(
        Integer teamId,
        String teamName,
        String inviteCode,
        List<TeamMemberDTO> members,
        Optional<OffsetDateTime> newestOperationDate,
        Optional<OperationSummaryDTO> newestOperation) {

    public TeamWithMembersDTO(Team t, Optional<Operation> o) {
        this(t.getId(), t.getTeamName(), t.getInviteCode(), t.getMemberships().stream().map(m -> new TeamMemberDTO(m)).toList(), o.map(Operation::getOperationDate), o.map(OperationSummaryDTO::new));
    }
}
