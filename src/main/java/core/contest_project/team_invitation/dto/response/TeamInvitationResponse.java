package core.contest_project.team_invitation.dto.response;

import core.contest_project.team_invitation.entity.TeamAwaiterInvitation;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TeamInvitationResponse(
        Long teamId,
        String teamName,
        String teamDescription,
        String teamImageUrl,
        String contestName,
        LocalDateTime createdAt
) {
    public static TeamInvitationResponse from(TeamAwaiterInvitation invitation, String contestTitle) {
        return TeamInvitationResponse.builder()
                .teamId(invitation.getTeam().getId())
                .teamName(invitation.getTeam().getName())
                .teamDescription(invitation.getTeam().getDescription())
                .teamImageUrl(invitation.getTeam().getProfileImageUrl())
                .contestName(contestTitle)
                .createdAt(invitation.getCreatedAt())
                .build();
    }
}
