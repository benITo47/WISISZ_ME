package me.wisisz.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "team", schema = "wisiszme")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Integer teamId;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @OneToMany(mappedBy = "team")
    private List<TeamMember> members;
}
