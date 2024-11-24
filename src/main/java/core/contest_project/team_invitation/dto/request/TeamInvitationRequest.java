package core.contest_project.team_invitation.dto.request;

public record TeamInvitationRequest(
        Long teamId,
        Long contestId,
        Long targetId

) {
}
