package me.wisisz.dto;

import java.math.BigDecimal;

import me.wisisz.model.OperationEntry;

public record OperationEntryDTO(
        String fname,
        String lname,
        String emailAddr,
        BigDecimal amount,
        String currencyCode) {
    public OperationEntryDTO(OperationEntry o) {
        this(
                o.getTeamMember().getPerson().getFname(),
                o.getTeamMember().getPerson().getLname(),
                o.getTeamMember().getPerson().getEmailAddr(),
                o.getAmount(),
                o.getOperation().getCurrencyCode());
    }

}
