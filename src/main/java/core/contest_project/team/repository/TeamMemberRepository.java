package core.contest_project.team.repository;


import core.contest_project.team.entity.member.TeamMember;
import core.contest_project.team.entity.member.TeamMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

    @Modifying
    @Query("DELETE FROM TeamMember tm WHERE tm.team.id = :teamId")
    void deleteAllByTeamId(@Param("teamId") Long teamId);


    @Query("select tm from TeamMember tm" +
            " join fetch tm.team" +
            " where tm.user.id=:userId")
    List<TeamMember> findAllByUserId(@Param("userId") Long userId);

    @Query("select tm from TeamMember tm" +
            " join fetch tm.user" +
            " where tm.team.id in :teamIds")
    List<TeamMember> findAllByTeamIds(@Param("teamIds") List<Long> teamIds);
}
