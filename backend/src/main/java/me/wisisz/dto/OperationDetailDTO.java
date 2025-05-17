package me.wisisz.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import me.wisisz.model.Operation;

public record OperationDetailDTO(
        String title,
        String description,
        BigDecimal totalAmount,
        OffsetDateTime operationDate,
        List<OperationMemberDTO> participants) {

    public OperationDetailDTO(Operation o) {
        this(
                o.getTitle(),
                o.getDescription(),
                o.getTotalAmount(),
                o.getOperationDate(),
                o.getEntries().stream().map(e -> new OperationMemberDTO(e.getTeamMember(), e.getAmount(), o.getCurrencyCode())).distinct().toList());
    }
}
