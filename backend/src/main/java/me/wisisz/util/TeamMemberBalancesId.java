package me.wisisz.util;

import java.io.Serializable;
import java.util.Objects;

import me.wisisz.model.Team;
import me.wisisz.model.TeamMember;
import me.wisisz.model.Person;

public class TeamMemberBalancesId implements Serializable {
    private Team team;
    private TeamMember teamMember;
    private Person person;

    public TeamMemberBalancesId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamMemberBalancesId)) return false;
        TeamMemberBalancesId that = (TeamMemberBalancesId) o;
        return Objects.equals(team, that.team) &&
               Objects.equals(teamMember, that.teamMember) &&
               Objects.equals(person, that.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, teamMember, person);
    }
}
