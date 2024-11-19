package core.contest_project.team.dto.response;

import core.contest_project.team.entity.join.RequestStatus;
import core.contest_project.team.entity.join.TeamJoinRequest;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MyTeamJoinRequestResponse(
        Long teamId,
        String teamName,
        String teamDescription,
        String teamImageUrl,
        RequestStatus status,  // PENDING, ACCEPTED, REJECTED
        LocalDateTime createdAt
) {
    public static MyTeamJoinRequestResponse from(TeamJoinRequest request) {
        return MyTeamJoinRequestResponse.builder()
                .teamId(request.getTeam().getId())
                .teamName(request.getTeam().getName())
                .teamDescription(request.getTeam().getDescription())
                .teamImageUrl(request.getTeam().getProfileImageUrl())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
