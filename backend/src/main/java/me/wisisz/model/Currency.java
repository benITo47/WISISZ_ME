package me.wisisz.model;

import jakarta.persistence.*;

@Entity
@Table(name = "currency", schema = "wisiszme")
public class Currency {

    @Id
    @Column(name = "currency_code")
    private String currencyCode;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
