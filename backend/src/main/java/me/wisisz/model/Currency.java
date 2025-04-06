package me.wisisz.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "currency", schema = "wisiszme")
public class Currency {

    @Id
    @Column(name = "currency_code")
    private String currencyCode;
}
