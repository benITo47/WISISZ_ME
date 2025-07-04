package me.wisisz.dto;

import java.math.BigDecimal;

import me.wisisz.model.Operation;

public record OperationSummaryDTO(
        Integer operationId,
        String title,
        String categoryName,
        String currencyCode,
        BigDecimal totalAmount) {

    public OperationSummaryDTO(Operation o) {
        this(
                o.getId(),
                o.getTitle(),
                o.getCategory().getCategoryName(),
                o.getCurrencyCode(),
                o.getTotalAmount());
    }
}
