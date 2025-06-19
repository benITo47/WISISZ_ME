package me.wisisz.dto;

import java.math.BigDecimal;
import java.util.Map;

import me.wisisz.model.Team;

public record TeamOperationsOverviewDTO(
        BigDecimal totalAmount,
        Map<String, BigDecimal> amountByCategory) {

    public TeamOperationsOverviewDTO(Team team) {
        this(team.getOperations().stream().map(op -> op.getTotalAmount()).reduce(BigDecimal.ZERO, BigDecimal::add),
             team.getOperations().stream().collect(java.util.stream.Collectors.groupingBy(op -> op.getCategory().getCategoryName(),
                        java.util.stream.Collectors.reducing(BigDecimal.ZERO, op -> op.getTotalAmount(), BigDecimal::add)))
        );
    }
}
