package me.wisisz.dto;

import me.wisisz.model.Team;
import me.wisisz.model.Operation;

import java.time.OffsetDateTime;
import java.util.Optional;

public record TeamDTO(
        Integer teamId,
        String teamName,
        String inviteCode,
        Optional<OffsetDateTime> newestOperationDate,
        Optional<OperationSummaryDTO> newestOperation) {

    public TeamDTO(Team t, Optional<Operation> o) {
        this(t.getId(), t.getTeamName(), t.getInviteCode(), o.map(Operation::getOperationDate), o.map(OperationSummaryDTO::new));
    }
}
