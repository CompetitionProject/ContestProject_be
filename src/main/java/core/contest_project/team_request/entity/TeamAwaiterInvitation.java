package core.contest_project.team_request.entity;

import core.contest_project.common.error.team.TeamErrorResult;
import core.contest_project.common.error.team.TeamException;
import core.contest_project.team.entity.Team;
import core.contest_project.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class TeamAwaiterInvitation {

    @EmbeddedId
    private TeamAwaiterInvitationId id;

    @MapsId("teamId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @MapsId("targetUserId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    private Long contestId;

    @Enumerated(EnumType.STRING)
    private TeamInvitationStatus status;

    private LocalDateTime createdAt;

    public static TeamAwaiterInvitation createInvitation(Team team, User targetUser, Long contestId) {
        return TeamAwaiterInvitation.builder()
                .id(new TeamAwaiterInvitationId(team.getId(), targetUser.getId()))
                .team(team)
                .targetUser(targetUser)
                .contestId(contestId)
                .status(TeamInvitationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public TeamAwaiterInvitation accept() {
        validatePendingStatus();
        return this.toBuilder()
                .status(TeamInvitationStatus.ACCEPTED)
                .build();
    }

    public TeamAwaiterInvitation reject() {
        validatePendingStatus();
        return this.toBuilder()
                .status(TeamInvitationStatus.REJECTED)
                .build();
    }

    private void validatePendingStatus() {
        if (this.status != TeamInvitationStatus.PENDING) {
            throw new TeamException(TeamErrorResult.INVALID_INVITATION_STATUS);
        }
    }

    public boolean isPending() {
        return this.status == TeamInvitationStatus.PENDING;
    }
}
