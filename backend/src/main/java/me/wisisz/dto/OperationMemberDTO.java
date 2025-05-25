package me.wisisz.dto;

import java.math.BigDecimal;

import me.wisisz.model.TeamMember;

public record OperationMemberDTO(
        Integer personId,
        String fname,
        String lname,
        String emailAddr,
        BigDecimal paidAmount,
        String currencyCode) {

    public OperationMemberDTO(TeamMember m, BigDecimal amount, String currencyCode) {
        this(m.getPerson().getId(), m.getPerson().getFname(), m.getPerson().getLname(), m.getPerson().getEmailAddr(),
                amount, currencyCode);
    }
}
