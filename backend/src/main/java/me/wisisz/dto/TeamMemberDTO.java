package me.wisisz.dto;

import java.math.BigDecimal;

import me.wisisz.model.TeamMember;

public record TeamMemberDTO(
        String fname,
        String lname,
        String emailAddr,
        BigDecimal defaultShare) {

    public TeamMemberDTO(TeamMember m) {
        this(m.getPerson().getFname(), m.getPerson().getLname(), m.getPerson().getEmailAddr(), m.getDefaultShare());
    }
}
