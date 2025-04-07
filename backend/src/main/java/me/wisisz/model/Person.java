package me.wisisz.model;

import jakarta.persistence.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
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

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TeamMember> memberships;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<TeamMember> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<TeamMember> memberships) {
        this.memberships = memberships;
    }

    @JsonIgnore
    public List<Team> getTeams() {
        return memberships.stream().map(m -> m.getTeam()).toList();
    }
}
