package core.contest_project.team_request.dto.response;

import core.contest_project.team_request.entity.TeamAwaiterInvitation;
import core.contest_project.team_request.entity.TeamInvitationStatus;
import core.contest_project.user.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TeamSentInvitationResponse(
        Long targetUserId,
        String targetUserNickname,
        String targetUserProfileUrl,
        String targetUserField,    // 사용자 분야
        String targetUserDuty,     // 사용자 직무
        TeamInvitationStatus status,
        LocalDateTime createdAt
) {
    public static TeamSentInvitationResponse from(TeamAwaiterInvitation invitation) {
        User targetUser = invitation.getTargetUser();
        return TeamSentInvitationResponse.builder()
                .targetUserId(targetUser.getId())
                .targetUserNickname(targetUser.getNickname())
                .targetUserProfileUrl(targetUser.getSnsProfileImageUrl())
                .targetUserField(targetUser.getUserField())
                .targetUserDuty(targetUser.getDuty())
                .status(invitation.getStatus())
                .createdAt(invitation.getCreatedAt())
                .build();
    }
}
