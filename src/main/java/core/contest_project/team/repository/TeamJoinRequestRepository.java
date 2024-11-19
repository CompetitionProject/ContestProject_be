package core.contest_project.team.repository;

import core.contest_project.team.entity.join.RequestStatus;
import core.contest_project.team.entity.join.TeamJoinRequest;
import core.contest_project.team.entity.join.TeamJoinRequestId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, TeamJoinRequestId> {
    /*@Query("SELECT r FROM TeamJoinRequest r " +
            "JOIN FETCH r.user u " +
            "LEFT JOIN FETCH u.userDetail " +
            "WHERE r.team.id = :teamId " +
            "AND r.status = :status " +
            "AND (:cursorDateTime IS NULL OR r.createdAt < :cursorDateTime) " +
            "ORDER BY r.createdAt DESC")
    List<TeamJoinRequest> findRequestsByTeamId(
            @Param("teamId") Long teamId,
            @Param("status") RequestStatus status,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            Pageable pageable
    );*/

    @Query("SELECT COUNT(r) > 0 FROM TeamJoinRequest r " +
            "WHERE r.team.id = :teamId " +
            "AND r.user.id = :userId " +
            "AND r.status = :status")
    boolean existsByTeamIdAndUserIdAndStatus(
            @Param("teamId") Long teamId,
            @Param("userId") Long userId,
            @Param("status") RequestStatus status
    );

    @Query("SELECT COUNT(r) > 0 FROM TeamJoinRequest r " +
            "WHERE r.id = :requestId " +
            "AND r.status = 'PENDING'")
    boolean existsPendingById(@Param("requestId") TeamJoinRequestId requestId);

    @Modifying
    @Query("DELETE FROM TeamJoinRequest r WHERE r.team.id = :teamId")
    void deleteAllByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT r FROM TeamJoinRequest r " +
            "JOIN FETCH r.user " +
            "WHERE r.team.id = :teamId " +
            "AND r.status = 'PENDING' " +
            "AND (:cursorDateTime IS NULL OR r.createdAt < :cursorDateTime) " +
            "ORDER BY r.createdAt DESC")
    List<TeamJoinRequest> findPendingRequests(
            @Param("teamId") Long teamId,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            Pageable pageable
    );

    @Query("SELECT r FROM TeamJoinRequest r " +
            "JOIN FETCH r.team " +
            "WHERE r.user.id = :userId " +
            "AND (:cursorDateTime IS NULL OR r.createdAt < :cursorDateTime) " +
            "ORDER BY r.createdAt DESC")
    List<TeamJoinRequest> findMyRequests(
            @Param("userId") Long userId,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            Pageable pageable
    );
}
