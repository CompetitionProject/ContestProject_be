package core.contest_project.awaiter.team.repository;

import core.contest_project.awaiter.team.entity.TeamAwaiter;
import core.contest_project.awaiter.team.entity.TeamAwaiterId;
import core.contest_project.contest.entity.Contest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamAwaiterRepository extends JpaRepository<TeamAwaiter, TeamAwaiterId> {

    @Query("SELECT ta FROM TeamAwaiter ta " +
            "JOIN FETCH ta.team t " +  // team 정보만 fetch join
            "WHERE ta.contest.id = :contestId " +
            "AND (:cursorDateTime IS NULL OR ta.createdAt < :cursorDateTime) " +
            "ORDER BY ta.createdAt DESC")
    List<TeamAwaiter> findTeamAwaiters(
            @Param("contestId") Long contestId,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            Pageable pageable
    );

    @Query("SELECT ta.contest FROM TeamAwaiter ta " +
            "JOIN ta.team t " +
            "JOIN t.members tm " +
            "LEFT JOIN FETCH ta.contest.contentImages " +
            "WHERE tm.user.id = :userId " +
            "AND (:cursorDateTime IS NULL OR ta.contest.endDate < :cursorDateTime) " +
            "ORDER BY ta.contest.endDate DESC")
    List<Contest> findWaitingContests(
            @Param("userId") Long userId,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            Pageable pageable
    );

    @Modifying
    @Query("DELETE FROM TeamAwaiter ta WHERE ta.contest.id = :contestId")
    void deleteAllByContestId(@Param("contestId") Long contestId);
}
