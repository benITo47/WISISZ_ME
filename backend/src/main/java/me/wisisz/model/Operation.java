package me.wisisz.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "operation", schema = "wisiszme")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnore
    private Team team;

    @Column(name = "operation_date", nullable = false)
    private OffsetDateTime operationDate;

    @Column(name = "description")
    private String description;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "operation_type", nullable = false)
    private String operationType;

    @OneToMany(mappedBy = "operation", fetch = FetchType.LAZY)
    private List<OperationEntry> entries;

    @Column(name = "title", nullable = false)
    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public OffsetDateTime getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(OffsetDateTime operationDate) {
        this.operationDate = operationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Category getCategoryId() {
        return category;
    }

    public void setCategoryId(Category category) {
        this.category = category;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public List<OperationEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<OperationEntry> entries) {
        this.entries = entries;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
