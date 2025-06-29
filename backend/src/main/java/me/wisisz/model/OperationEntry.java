package me.wisisz.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "operation_entry", schema = "wisiszme")
public class OperationEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private TeamMember teamMember;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer entryId) {
        this.id = entryId;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public TeamMember getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(TeamMember teamMember) {
        this.teamMember = teamMember;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
