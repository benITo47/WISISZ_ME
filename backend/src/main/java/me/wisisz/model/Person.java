package me.wisisz.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "person", schema = "wisiszme")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Integer id;

    @Column(name = "fname", nullable = false)
    private String fname;

    @Column(name = "lname")
    private String lname;

    @Column(name = "email_addr", nullable = false, unique = true)
    private String emailAddr;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "person")
    private List<TeamMember> memberships;
}
