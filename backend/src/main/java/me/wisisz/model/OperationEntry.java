package me.wisisz.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "operation_entry", schema = "wisiszme")
public class OperationEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Integer entryId;

    @ManyToOne
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private TeamMember teamMember;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
}
