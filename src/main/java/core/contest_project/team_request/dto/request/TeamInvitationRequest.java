package core.contest_project.team_request.dto.request;

public record TeamInvitationRequest(
        Long teamId,
        Long contestId,
        Long targetId

) {
}
