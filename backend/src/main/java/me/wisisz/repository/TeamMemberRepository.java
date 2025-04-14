package me.wisisz.repository;

import me.wisisz.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {
    boolean existsByPersonIdAndTeamId(Integer personId, Integer teamId);
    Optional<TeamMember> findByPerson_IdAndTeam_Id(Integer personId, Integer teamId);
    // Custom queries can be added here if needed
}
