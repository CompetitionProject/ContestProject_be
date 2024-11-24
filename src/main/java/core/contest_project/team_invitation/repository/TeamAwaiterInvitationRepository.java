package core.contest_project.team_invitation.repository;

import core.contest_project.team_invitation.entity.TeamAwaiterInvitation;
import core.contest_project.team_invitation.entity.TeamAwaiterInvitationId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TeamAwaiterInvitationRepository extends JpaRepository<TeamAwaiterInvitation, TeamAwaiterInvitationId> {
    @Query("SELECT COUNT(i) > 0 FROM TeamAwaiterInvitation i " +
            "WHERE i.id = :invitationId " +
            "AND i.status = 'PENDING'")
    boolean existsPendingById(@Param("invitationId") TeamAwaiterInvitationId invitationId);

    @Query("SELECT i FROM TeamAwaiterInvitation i " +
            "WHERE i.id = :invitationId " +
            "AND i.status = 'PENDING'")
    Optional<TeamAwaiterInvitation> findPendingById(@Param("invitationId") TeamAwaiterInvitationId invitationId);

    @Query("SELECT i FROM TeamAwaiterInvitation i " +
            "JOIN FETCH i.team " +
            "WHERE i.targetUser.id = :userId " +
            "AND (:cursorDateTime IS NULL OR i.createdAt < :cursorDateTime) " +
            "ORDER BY i.createdAt DESC")
    List<TeamAwaiterInvitation> findMyInvitations(
            @Param("userId") Long userId,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            Pageable pageable
    );

    @Query("SELECT i FROM TeamAwaiterInvitation i " +
            "JOIN FETCH i.targetUser " +
            "WHERE i.team.id = :teamId " +
            "AND (:cursorDateTime IS NULL OR i.createdAt < :cursorDateTime) " +
            "ORDER BY i.createdAt DESC")
    List<TeamAwaiterInvitation> findTeamSentInvitations(
            @Param("teamId") Long teamId,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            Pageable pageable
    );

    @Query("SELECT i FROM TeamAwaiterInvitation i " +
            "JOIN FETCH i.team " +
            "JOIN FETCH i.targetUser " +
            "WHERE i.id.targetUserId = :userId " +
            "AND i.status = 'PENDING' " +
            "ORDER BY i.createdAt DESC")
    Optional<TeamAwaiterInvitation> findPendingByTargetUserId(@Param("userId") Long userId);
}
