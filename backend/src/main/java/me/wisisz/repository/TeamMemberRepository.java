package me.wisisz.repository;

import me.wisisz.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {
    // Custom queries can be added here if needed
}
