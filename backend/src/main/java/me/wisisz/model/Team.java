package me.wisisz.model;

import jakarta.persistence.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "team", schema = "wisiszme")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Integer id;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TeamMember> memberships;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Operation> operations;

    @Column(name = "invite_code", nullable = false, insertable = false, updatable = false)
    private String inviteCode;

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer teamId) {
        this.id = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<TeamMember> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<TeamMember> memberships) {
        this.memberships = memberships;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}
