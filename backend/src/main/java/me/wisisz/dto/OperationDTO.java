package me.wisisz.dto;

import java.util.List;

import me.wisisz.model.Operation;

public record OperationDTO(
        Integer operationId,
        String teamName,
        String categoryName,
        List<OperationEntryDTO> operations) {

    public OperationDTO(Operation o) {
        this(
                o.getId(),
                o.getTeam().getTeamName(),
                o.getCategory().getCategoryName(),
                o.getEntries().stream().map(e -> new OperationEntryDTO(e)).toList());
    }
}
