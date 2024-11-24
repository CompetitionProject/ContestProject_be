package core.contest_project.team.repository;

import core.contest_project.team.entity.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.leader WHERE t.id = :teamId")
    Optional<Team> findByIdWithLeader(@Param("teamId") Long teamId);

    @Query("SELECT t FROM Team t " +
            "LEFT JOIN FETCH t.leader " +
            "LEFT JOIN FETCH t.members m " +
            "LEFT JOIN FETCH m.user " +
            "WHERE t.id = :teamId")
    Optional<Team> findByIdWithLeaderAndMembers(@Param("teamId") Long teamId);

    @Query("SELECT t FROM Team t " +
            "LEFT JOIN FETCH t.leader " +
            "LEFT JOIN FETCH t.members " +
            "WHERE t.id = :teamId")
    Optional<Team> findByIdWithMembers(@Param("teamId") Long teamId);

    @Query("SELECT DISTINCT t FROM Team t " +
            "LEFT JOIN FETCH t.leader " +
            "LEFT JOIN FETCH t.members m " +
            "LEFT JOIN FETCH m.user " +
            "WHERE (m.user.id = :userId OR t.leader.id = :userId) " +
            "ORDER BY COALESCE(m.joinedAt, t.createdAt) DESC " +
            "LIMIT 3")  // 상위 3개만 조회
    List<Team> findTop3ByUserIdOrderByJoinedAtDesc(@Param("userId") Long userId);

    @Query("SELECT t FROM Team t " +  // DISTINCT 제거
            "LEFT JOIN t.members m " +
            "WHERE (m.user.id = :userId OR t.leader.id = :userId) " +
            "AND (:cursorDateTime IS NULL OR COALESCE(m.joinedAt, t.createdAt) < :cursorDateTime) " +
            "GROUP BY t.id " +  // 대신 GROUP BY 사용
            "ORDER BY MAX(COALESCE(m.joinedAt, t.createdAt)) DESC")  // MAX 사용
    List<Team> findAllByUserId(
            @Param("userId") Long userId,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            Pageable pageable
    );

    @Query("SELECT t FROM Team t " +
            "WHERE t.leader.id = :userId " +
            "ORDER BY t.createdAt DESC")
    List<Team> findAllByLeaderId(@Param("userId") Long userId);
}
