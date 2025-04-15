package me.wisisz.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

import me.wisisz.util.TeamMemberBalancesId;

/**
 * Represents a view in the database that stores balances for team members.
 * This class maps to the "team_member_balances" view in the "wisiszme" schema.
 *
 * Each object of this class corresponds to a row in the view.
 * 
 * Key attributes:
 * - teamId: Represents the team ID.
 * - memberId: Represents the member ID within the team.
 * - personId: Represents the unique identifier of the person.
 * - firstName: Stores the first name of the team member.
 * - lastName: Stores the last name of the team member.
 * - balance: Stores the financial balance for the team member.
 * 
 * Notes:
 * - This entity is read-only and does not allow modification since it represents a database view.
 */
@Entity
@IdClass(TeamMemberBalancesId.class)
@Table(name = "team_member_balances", schema = "wisiszme")
public class TeamMemberBalances implements Serializable {

    @Id
    @Column(name = "team_id")
    private Integer teamId;

    @Id
    @Column(name = "member_id")
    private Integer memberId;

    @Id
    @Column(name = "person_id", nullable = false)
    private Integer personId;

    @Column(name = "fname", nullable = false)
    private String firstName;

    @Column(name = "lname", nullable = false)
    private String lastName;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    public TeamMemberBalances() {}

    // Constructor for initializing all fields
    public TeamMemberBalances(Integer teamId, Integer memberId, Integer personId, String firstName, String lastName, BigDecimal balance) {
        this.teamId = teamId;
        this.memberId = memberId;
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
    }

    // Getters (no setters, since this is a read-only entity)
    public Integer getTeamId() {
        return teamId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}