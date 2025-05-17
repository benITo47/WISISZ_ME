package me.wisisz.dto;

import java.util.List;

public class TeamOperationRequestDTO {
    private String title;
    private String totalAmount;
    private String categoryId;
    private String currencyCode;
    private String description;
    private String operationType;
    private List<OperationParticipantDTO> participants;
    

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTotalAmount() { return totalAmount; }
    public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public List<OperationParticipantDTO> getParticipants() { return participants; }
    public void setParticipants(List<OperationParticipantDTO> participants) { this.participants = participants; }
}