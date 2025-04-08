package me.wisisz.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "team_member", schema = "wisiszme")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "default_share", nullable = false)
    private BigDecimal defaultShare;

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public BigDecimal getDefaultShare() {
        return defaultShare;
    }

    public void setDefaultShare(BigDecimal defaultShare) {
        this.defaultShare = defaultShare;
    }
}
