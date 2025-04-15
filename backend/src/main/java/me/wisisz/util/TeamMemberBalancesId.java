package me.wisisz.util;

import java.io.Serializable;
import java.util.Objects;

public class TeamMemberBalancesId implements Serializable {
    private Integer teamId;
    private Integer memberId;
    private Integer personId;

    public TeamMemberBalancesId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamMemberBalancesId)) return false;
        TeamMemberBalancesId that = (TeamMemberBalancesId) o;
        return Objects.equals(teamId, that.teamId) &&
               Objects.equals(memberId, that.memberId) &&
               Objects.equals(personId, that.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, memberId, personId);
    }
}
