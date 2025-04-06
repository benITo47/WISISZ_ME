package me.wisisz.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "operation", schema = "wisiszme")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id")
    private Integer id;

    @Column(name = "team_id", nullable = false)
    private Integer teamId;

    @Column(name = "operation_date", nullable = false)
    private OffsetDateTime operationDate;

    @Column(name = "description")
    private String description;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "operation_type", nullable = false)
    private String operationType;
}
