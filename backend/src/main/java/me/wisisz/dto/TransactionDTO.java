package me.wisisz.dto;

import java.math.BigDecimal;

import me.wisisz.model.Person;

public record TransactionDTO(
        String fromFirstName,
        String fromLastName,
        String fromEmailAddr,
        String toFirstName,
        String toLastName,
        String toEmailAddr,
        BigDecimal amount) {
    public TransactionDTO(Person from, Person to, BigDecimal amount) {
        this(
                from.getFname(),
                from.getLname(),
                from.getEmailAddr(),
                to.getFname(),
                to.getLname(),
                to.getEmailAddr(),
                amount);
    }
}
