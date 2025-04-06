package me.wisisz.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "category", schema = "wisiszme")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;
}
